## preview

JPA 관련 클래스, 어노테이션 데모

## install

```shell
docker run -d \
 -p 3306:3306 \
 -e MYSQL_ROOT_PASSWORD=root \
 -e MYSQL_DATABASE=demo \
 --name=mysql \
 mysql:8.0.32
```

## test

```shell
curl -XGET 'http://localhost:8080/boards'
curl -XGET 'http://localhost:8080/boards/1'
curl -XPOST 'http://localhost:8080/boards/random'

curl -XGET 'http://localhost:8080/boards/thumbnail'
curl -XGET 'http://localhost:8080/boards/thumbnail/1'
curl -XPOST 'http://localhost:8080/boards/thumbnail' \
--header 'Content-Type: application/json' \
--data '{ "boardId": 1 }' 
```
