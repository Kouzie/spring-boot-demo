# minio demo project 

> <https://min.io/>  
> <https://docs.min.io/minio/baremetal/sdk/java/minio-java.html>

AWS S3 라이브러리 완벽 호환 Object Storage   

## install minio

```shell
docker run -d --name minio \
    -p 9001:9001 \
    -p 9000:9000 \
    -v ~/mnt:/data \
    -e "MINIO_ACCESS_KEY=admin" \
    -e "MINIO_SECRET_KEY=password" \
    minio/minio:RELEASE.2021-04-22T15-44-28Z.hotfix.cf89776b4 \
    server /data
```

> 버킷 생성 후 Edit Policy 에서 `* - Read Only` 설정 필요

## test

```shell
$ cd test/resource

$ curl -X 'POST' 'http://127.0.0.1:8080/file' \
-H 'accept: application/json' \
-H 'Content-Type: multipart/form-data' \
-F 'file=@sample.png;type=image/png'
```
