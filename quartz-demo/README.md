## preview 

- FIRED_TRIGGERS: 현재 실행된(fired- 스케쥴의 트리거정보(비동기 동작시 바로 실행완료 상태가 되어버리므로 해당 테이블은 스쳐지나감-
- PAUSED_TRIGGER_GRPS: 
- LOCKS: 
- SIMPLE_TRIGGERS: 
- SIMPROP_TRIGGERS: 
- CRON_TRIGGERS: 등록된 스케쥴의 cron 정보
- BLOB_TRIGGERS: 
- TRIGGERS: 등록된 스케쥴의 trigger 정보
- JOB_DETAILS: 실행될 QuartzJob 정보
- CALENDARS: 특정 시간에 스케쥴을 동작시키지 않길 원할 경우 사용
- SCHEDULER_STATE: 스케쥴러 정보, instance_name 칼럼값으로 스케쥴러 구분이 가능, last_checkin_time이 checkin_interval 값의 간격으로 계속해서 갱신된다. 서버(scheduler-가 죽은 경우 last_checkin_time+checkin_interval 값이 now 값 보다 작아지게 되므로 이를 활용하여 scheduler 가 죽었는지 판별할 수 있다. checkin_interval 은 quartz property 값에서 지정할 수 있다.

## install

docker run -d \
-p 3306:3306 \
-e MYSQL_ROOT_PASSWORD=root \
-e MYSQL_DATABASE=demo \
--name=mysql \
mysql:8.0.32