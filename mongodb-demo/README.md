## 설치

```shell
openssl rand -base64 756 > mongodb-keyfile
chmod 400 mongodb-keyfile

docker compose up -d
# 20초정도 있다가 실행, 서버가 실행될때까지
docker exec -it mongodb_container mongosh --host localhost -u admin -p password /init/init-mongo.js
```



```shell
curl -X POST "http://localhost:8080/users" \
-H "Content-Type: application/json" \
-d '{
  "username": "john_doe",
  "email": "john.doe@example.com",
  "age": 30,
  "gender": "male",
  "nickname": "johny",
  "desc": "Software developer at XYZ"
}'
```


```shell
curl -X GET "http://localhost:8080/users"
curl -X GET "http://localhost:8080/users/6761131eb6d0eb6d16bc2473"
curl -X GET "http://localhost:8080/users/search?username=john"
curl -X GET "http://localhost:8080/users/search?username=john&email=john@example.com"
```


```shell
curl -X GET "http://localhost:8080/users/search/prefix?prefix=j"
```
