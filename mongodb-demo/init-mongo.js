// Replica Set 초기화
rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "localhost:27017" } // 필요에 따라 호스트 및 포트를 수정하세요.
  ]
});

// Primary 노드 선출 및 write 가능 여부 대기
function waitForWritablePrimary() {
  while (true) {
    let status = rs.status();
    // primary node (1) status 인지 확인 및 writeable 가능한지 확인
    if (status.myState === 1 && db.runCommand({ hello: 1 }).isWritablePrimary) {
      print("Replica Set initialized and Primary node is writable. Proceeding...");
      break;
    } else {
      print("Waiting for Primary to be writable...");
      sleep(1000); // 1초 대기
    }
  }
}
waitForWritablePrimary();


// `testdb` 데이터베이스 선택
print("Start creating the database...");
db = db.getSiblingDB("testdb");

// 사용자 생성
db.createUser({
  user: "testuser",
  pwd: "testpass",
  roles: [
    { role: "readWrite", db: "testdb" } // testdb에 readWrite 권한 부여
  ]
});
print("User 'testuser' created successfully.");

// 초기 데이터 삽입 (옵션)
db.testCollection.insertOne({
  message: "Replica Set initialized successfully!"
});
print("Initial document inserted into 'testCollection'.");
