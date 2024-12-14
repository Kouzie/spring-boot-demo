## preview

> <https://www.baeldung.com/nats-java-client>  
> <https://docs.nats.io/>  
> <https://github.com/nats-io/nats.java>

`spring.profiles.active` 사용  
`sync`, `async` java client 구현  

## install

```shell
# install nats cli
$ brew install nats-io/nats-tools/nats

$ docker run --name nats -d \
 -v "$(pwd)/etc/nats:/etc/nats" \
 -p 4222:4222 \
 -p 8222:8222 \
 nats:2.9.15 \
 --http_port 8222 -c /etc/nats/default.conf
```

`default.conf` 에서 각종 `nats` 관련 설정 가능  
> <https://docs.nats.io/running-a-nats-service/configuration>

## publish message

메세지 `sub` `pub` 테스트

```shell
$ nats sub test --server=nats://localhost:4222 --user=admin --password=password

$ curl --location 'http://localhost:8080/nats/publish' \
--header 'Content-Type: application/json' \
--data '{
    "subject": "default.subject",
    "message": "hello world"
}'
````

메세지 `reply` `pub` 테스트

```shell
$ nats reply test --server=nats://localhost:4222 --user=admin --password=password "OK RECEIVED"

$ nats sub reply.test --server=nats://localhost:4222 --user=admin --password=password

$ curl --location 'http://localhost:8080/nats/publish' \
--header 'Content-Type: application/json' \
--data '{
    "subject": "default.subject",
    "message": "hello world",
    "replyTo": "reply.test"
}'
````

## subscribe subject

```shell
curl -X POST http://localhost:8080/nats/subscribe \
-H "Content-Type: application/json" \
-d '{"subject": "demo.subject"}'

curl -X DELETE http://localhost:8080/nats/unsubscribe \
-H "Content-Type: application/json" \
-d '{"subject": "demo.subject"}'
```