# Keycloak Docker Setup

이 디렉토리는 Keycloak을 Docker Compose로 실행하기 위한 설정 파일들을 포함합니다.

## 구성 요소

- `docker-compose.yml`: Keycloak과 PostgreSQL을 실행하는 Docker Compose 설정

## 사용 방법

1. Keycloak 시작

```bash
docker-compose up -d
```

2. 초기화 스크립트 실행

```bash
# 스크립트를 바로 실행 (복사 불필요)
docker exec keycloak /bin/bash -c "$(cat ./init-master-client.sh)"
```

## 생성되는 Client 설정

- **Client ID**: `demo-client`
- **Client Type**: Public Client (PKCE 방식)
- **Standard Flow**: 활성화
- **Direct Access Grants**: 활성화
- **Redirect URIs**: `*` (모든 URI 허용)
- **Web Origins**: `+` (모든 Origin 허용)
- **PKCE Code Challenge Method**: S256

## Protocol Mappers

다음 Protocol Mappers가 자동으로 추가됩니다:

1. **realm-roles**: Realm 역할을 `realm_access.roles` claim에 매핑
2. **group-membership**: 그룹 멤버십을 `groups` claim에 매핑

## 주의사항

- PostgreSQL 볼륨은 제거되어 있어 컨테이너 재시작 시 데이터가 초기화됩니다.
- 데이터를 영구 저장하려면 `docker-compose.yml`에 볼륨 설정을 추가하세요.
