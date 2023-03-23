## spring-cloud-aws


## spring data + dynamodb

> <https://github.com/boostchicken/spring-data-dynamodb>


## local dynamodb + docker


```shell
$ docker pull amazon/dynamodb-local
$ docker run -d -p 8000:8000 amazon/dynamodb-local
```
```shell
curl -d '{"nickName": "test-user", "customerType": "BRONZE"}' \
    -H "Content-Type: application/json" \
    -X POST http://localhost:8080/customer

curl -H "Content-Type: application/json" \
    -X GET http://localhost:8080/customer/f5f7e084-03a8-4976-a6c1-625f40b1640d 
```
