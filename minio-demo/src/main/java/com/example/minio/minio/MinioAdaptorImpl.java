package com.example.minio.minio;

import com.example.minio.common.FileAdaptor;
import com.example.minio.common.PutFileException;
import com.example.minio.common.RemoveFileException;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;

@Profile("minio")
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioAdaptorImpl implements FileAdaptor {

    @Value("${object.storage.url}")
    private String storageUrl;
    @Value("${object.storage.access.key}")
    private String accessKey;
    @Value("${object.storage.secret.key}")
    private String secretKey;
    @Value("${object.storage.bucket}")
    private String bucketName;

    private MinioClient minioClient;


    @PostConstruct
    public void init() throws Exception {
        log.info("minio adaptor init invoked, bucket:{}", bucketName);
        // Create a minioClient with the MinIO Server name, Port, Access key and Secret key.
        minioClient = MinioClient.builder()
                .endpoint(storageUrl)
                .credentials(accessKey, secretKey)
                .build();

        try {
            // Check if the bucket already exists.
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!isExist) {
                // Make a new bucket called asiatrip to hold a zip file of photos.
                log.info("Bucket not exists.");
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            } else {
                log.info("minio bucket:{} already exist", bucketName);
            }
        } catch (Exception e) {
            log.error("MinioComponent init error invoked, exceptionType:{}", e.getClass().getCanonicalName());
            throw e;
        }
    }

    // "multipart/form-data"
    @Override
    public String putFile(String path, InputStream inputStream, String contentType) throws PutFileException {
        // Upload the zip file to the bucket with putObject
        String objectName = UUID.randomUUID().toString();
        try {
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path + objectName)
                    .stream(inputStream, -1, 209715200)
                    .contentType(contentType)
                    .build());
            log.info("put file result: {}", response.toString());
            return path + objectName;
        } catch (Exception e) {
            log.error("put file error invoked, type:{}, message:{}", e.getClass().getSimpleName(), e.getMessage());
            throw new PutFileException(e.getMessage());
        }
    }

    @Override
    public Boolean removeFile(String path) throws RemoveFileException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path)
                    .build());
            return true;
        } catch (Exception e) {
            log.error("remove file error invoked, type:{}, message:{}", e.getClass().getSimpleName(), e.getMessage());
            throw new RemoveFileException(e.getMessage());
        }
    }
}
