## preview

mqtt paho lib 로 mqq broker 에 연결 테스트
mqtt broker 는 rabbitMQ 에 mqtt 플러그인을 설치해서 사용

## install

```shell
docker build -t rabbitmq_mqtt ./rabbitmq
docker run -d -p 5672:5672 -p 15672:15672 -p 1883:1883 \
--restart=unless-stopped \
--name rabbitmq \
-e RABBITMQ_DEFAULT_USER=guest \
-e RABBITMQ_DEFAULT_PASS=guest \
-v "$(pwd)/data/rabbitmq/lib:/var/lib/rabbitmq" \
-v "$(pwd)/data/rabbitmq/log:/var/log/rabbitmq" \
rabbitmq_mqtt
```

## Test

```shell
mosquitto_pub --url "mqtt://guest:guest@localhost:1883/computer/part/cpu" -t "/computer/part/cpu" -m "i5 10000"
mosquitto_pub --url "mqtt://guest:guest@localhost:1883/computer/part/cpu" -t "/computer/part/gpu" -m "rtx 1080"
```