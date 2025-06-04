## preview

Http Data -> Java DTO (POJO) 변환 테스트

아래 3개 라이브러리에 대해 테스트 진행

- ObjectMapper
- Spring Web MVC Request
- Mapstruct

아래 postman 파일 참고
[test.postman_collection.json](test.postman_collection.json) 

## API 테스트 - exception advice

정상 API 테스트

```shell
curl -X GET http://localhost:8080/validation/test

curl -X POST http://localhost:8080/validation/data \
-H "Content-Type: application/json" \
-d '{"id": "123", "name": "John Doe", "age": 30}'
```

비정상 API 테스트(default 한글)

```shell
curl -X POST http://localhost:8080/validation/test
#{"code":"405","error":"MethodNotAllow","description":"잘못된 요청 메소드입니다."}

curl -X GET http://localhost:8080/validation/unknown
#{"code":"404","error":"PageNotFound","description":"페이지가 존재하지 않습니다."}

curl -X GET http://localhost:8080/validation/error
#{"code":"500","error":"ServerError","description":"서버 오류가 발생하였습니다."}%

curl -X POST http://localhost:8080/validation/data \
-H "Content-Type: application/json" \
-d '{"id": "123", "name": "John Doe", "age": "five"}'
#{"code":"400","error":"BadRequest","description":"age 요청이 올바르지 않습니다."}

curl -X POST http://localhost:8080/validation/data \
-H "Content-Type: application/json" \
-d '{"id": "123", "name": "John Doe"}'
#{"code":"422","error":"ValidationError","description":"요청 형식이 올바르지 않습니다."}
```

비정상 API 테스트(영어)

```shell
curl -X GET http://localhost:8080/error \
     -H "Accept-Language: en"
#{"code":"500","error":"ServerError","description":"Server error occurred."}
```

## Docker 이미지 생성

컨테이너 환경에서 locale 명령 실행시 어떠한 설정도 안되어있는것을 확인할 수 있는데

```shell
# locale
LANG=
LANGUAGE=
LC_CTYPE="C.UTF-8"
LC_NUMERIC="C.UTF-8"
LC_TIME="C.UTF-8"
LC_COLLATE="C.UTF-8"
LC_MONETARY="C.UTF-8"
LC_MESSAGES="C.UTF-8"
LC_PAPER="C.UTF-8"
LC_NAME="C.UTF-8"
LC_ADDRESS="C.UTF-8"
LC_TELEPHONE="C.UTF-8"
LC_MEASUREMENT="C.UTF-8"
LC_IDENTIFICATION="C.UTF-8"
LC_ALL=C.UTF-8
```

이 경우 en locale 을 기본적으로 사용하는듯하다.

`ko_KR.UTF-8` locale 을 사용할 수 있도록 docker image 를 작성하면 `messages.properties` 를 기본 메세지 속성으로 사용할 수 있다.

```shell
./gradlew locale-message-demo:build 
cd locale-message-demo
docker build -t locale-message-demo .

docker run -d -p 8080:8080 --name locale-demo locale-message-demo
docker rm -f locale-demo 
```
