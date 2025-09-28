## 실행  

```shell
docker-compose -f docker-compose.yml -f docker-compose-service.yml up -d
```

## curl

```shell
curl -X GET http://localhost:8080/greeting
curl -X GET http://localhost:8080/greeting/40/42
curl -X GET http://localhost:8080/greeting/record
```

```shell
curl -X GET http://localhost:8081/calculating/greet
curl -X GET http://localhost:8081/calculating/1/2
```
