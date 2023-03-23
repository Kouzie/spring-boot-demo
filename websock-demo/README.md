## preview

아래 3개 웹소켓 라이브러리를 사용  

- Spring webscoket
- STOMP
- tomcat websocket

일반적인 websocket 구현시 spring websocket 사용 추천

## 참고  

> <https://spring.io/guides/gs/messaging-stomp-websocket/>

## test

profile 로 spring, stomp, tomcat 변경 후  
브라우저에서 아래 url 로 테스트 진행  

- http://127.0.0.1:8080/spring/sample  
- http://127.0.0.1:8080/stomp/sample  
- http://127.0.0.1:8080/tomcat/sample  

> localhost 로 접속하지 않도록 주의(cors 에러)