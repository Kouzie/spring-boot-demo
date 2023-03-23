## preview

rabbitMQ + AMQP 데모

## install

```shell
docker run -d -p 5672:5672 -p 15672:15672 \
--name rabbitmq \
-e RABBITMQ_DEFAULT_USER=guest \
-e RABBITMQ_DEFAULT_PASS=guest \
--platform linux/amd64 \
rabbitmq:3.8.18-management
```
