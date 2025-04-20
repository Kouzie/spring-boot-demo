## preview

TLS 기반 rabbitmq mqtt broker 설치

`root - intermediate - server`.

아래 명령으로 depth 2 인증서 구조 생성   

RootCA 생성

```shell
openssl genrsa -out root_ca.key 2048

openssl req -x509 -new -key root_ca.key -days 3650 -out root_ca.crt \
  -subj "/C=KR/ST=Gyeonggi-do/L=Seongnam-si/\
O=Demo-Comp/OU=Demo-Service/\
CN=RootCA/emailAddress=kouzie@test.com"
```

IntermediateCA 생성

```shell
openssl genrsa -out intermediate_ca.key 2048

openssl req -new -key intermediate_ca.key \
  -out intermediate_ca.csr \
  -subj "/C=KR/ST=Gyeonggi-do/L=Seongnam-si/\
O=Demo-Comp/OU=Demo-Service/\
CN=IntermediateCA/emailAddress=kouzie@test.com"

openssl x509 -req -in intermediate_ca.csr \
  -CA root_ca.crt \
  -CAkey root_ca.key \
  -CAcreateserial \
  -days 1825 \
  -out intermediate_ca.crt \
  -extensions v3_ca \
  -extfile <(cat <<EOF
[v3_ca]
basicConstraints = critical,CA:TRUE
keyUsage = critical, digitalSignature, cRLSign, keyCertSign
subjectKeyIdentifier = hash
authorityKeyIdentifier = keyid:always,issuer
EOF
)
```

ServerCA 생성

```shell
openssl genrsa -out server.key 2048

openssl req -new -key server.key -out server.csr \
  -subj "/C=KR/ST=Gyeonggi-do/L=Seongnam-si/\
O=Demo-Comp/OU=Demo-Service/\
CN=ServerCN/emailAddress=kouzie@test.com"

openssl x509 -req -in server.csr \
  -CA intermediate_ca.crt \
  -CAkey intermediate_ca.key \
  -CAcreateserial \
  -days 365 -out server.crt
```

### 인증서 종류

- client.crt client.key
  - 클라이언트 인증서
  - mqtt 연결용
- root_ca.crt root_ca.key
  - 루트 인증서
  - 중간인증서 생성용, 인증서 검증용
- intermediate_ca.crt intermediate_ca.key
  - 중간인증서
- server.crt server.key
  - 서버 인증서
  - rabbitmq 브로커에서 사용, 클라이언트 인증서 생성 API 에서 사용

Spring Boot 서버에선 paho mqtt client 연결을 위해 client.crt 를 사용하요
데모로 만든 client 인증서 생성 API 에서 server.crt 를 사용한다.  

### 브로커 실행

mqtt client 는 client.crt. root_ca.crt 만으로 인증서 체인을 검증해야 함으로
모든 인증서를 체인으로 줄수 있도록 아래와 같이 겹치는 구조로 생성해야함.

```sh
cat intermediate_ca.crt root_ca.crt > cacert-chain.crt
cat server.crt intermediate_ca.crt > server-fullchain.crt
```

인증은 rabbitmq 에서 제공하는 http backend 플러그인 사용

아래 3개 인증서 볼륨설정후 실행

- cacert-chain.crt  
- server.key  
- server-fullchain.crt  

```shell
docker-compose up
```

### 서버 실행

java paho mqtt client 에선 p12 형식의 인증서를 요구함으로 client_123.crt 를 p12 형식으로 변환.  

> name 은 인증서 항목(alias)

```shell
openssl pkcs12 -export \
  -in client_123.crt \
  -inkey client_123.key \
  -out client_123.p12 \
  -name "mqtt-client" \
  -certfile root_ca.crt \  
  -password pass:mypass123
```

## 테스트 명령

CLI 툴 mqtt client 는 client_456.crt 사용

```shell
mosquitto_pub -h localhost -p 8883 \
  --cafile root_ca.crt \
  --cert client_456.crt \
  --key client_456.key \
  --tls-version tlsv1.2 \
  --insecure \
  -t "/computer/part/cpu" \
  -m "i5 10000" \
  -i my-client-456
```

```shell
mosquitto_pub -h localhost -p 8883 \
  --cafile root_ca.crt \
  --cert client_456.crt \
  --key client_456.key \
  --tls-version tlsv1.2 \
  --insecure \
  -t "/computer/part/gpu" \
  -m "rtx 1080" \
  -i my-client-456
```

```sh
mosquitto_sub -h localhost -p 8883 \
  --cafile root_ca.crt \
  --cert client_456.crt \
  --key client_456.key \
  --insecure \
  -t '#' \
  --tls-version tlsv1.2 \
  -i my-client-456
```