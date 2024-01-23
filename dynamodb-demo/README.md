## spring data + dynamodb

> <https://github.com/boostchicken/spring-data-dynamodb>

## local dynamodb + docker


```shell
$ docker pull amazon/dynamodb-local
$ docker run -d -p 8000:8000 amazon/dynamodb-local
```
```shell

curl --location 'http://localhost:8080/customer' \
--header 'Content-Type: application/json' \
--data '{
    "nickName": "TEST_NICKNAME",
    "customerType": "GOLD"
}'

curl --location 'http://localhost:8080/customer/f53cdf8f-ebfe-42fa-ac44-2202eecef126'

curl --location --request DELETE 'http://localhost:8080/customer/f53cdf8f-ebfe-42fa-ac44-2202eecef126' 
```
