## REST Login

```shell
TOKEN=$(curl -X POST http://localhost:8080/auth/login_demo \
-H "Content-Type: application/json" \
-d '{
  "username": "admin",
  "password": "admin"
}' | jq -r '.jwtToken')

curl -X GET http://localhost:8080/sample/admin \
-H "Authorization: Bearer $TOKEN"

curl -X GET http://localhost:8080/sample/custom/1 \
-H "Authorization: Bearer $TOKEN"

```

