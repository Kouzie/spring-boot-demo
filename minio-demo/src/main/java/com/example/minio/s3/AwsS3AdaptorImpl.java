package com.example.minio.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.minio.common.FileAdaptor;
import com.example.minio.common.PutFileException;
import com.example.minio.common.RemoveFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;

@Profile("s3")
@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3AdaptorImpl implements FileAdaptor {

    @Value("${object.storage.url}")
    private String storageUrl;
    @Value("${object.storage.access.key}")
    private String accessKey;
    @Value("${object.storage.secret.key}")
    private String secretKey;
    @Value("${object.storage.bucket}")
    private String bucketName;

    private AmazonS3 s3Client;

    @PostConstruct
    private void init() {
        log.info("aws s3 adaptor init invoked, bucket:{}", bucketName);
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSignerOverride("AWSS3V4SignerType");
        s3Client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(storageUrl, Regions.US_EAST_1.name()))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
        } else {
            log.info("aws s3 bucket:{} already exist", bucketName);
        }
    }

    @Override
    public String putFile(String path, InputStream inputStream, String contentType) throws PutFileException {
        String objectName = UUID.randomUUID().toString();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path + objectName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            PutObjectResult putObjectResult = s3Client.putObject(putObjectRequest);
            log.info(putObjectResult.toString());
            log.info("put file result: {}", putObjectResult);
            return path + objectName;
        } catch (Exception e) {
            log.error("put file error invoked, type:{}, message:{}", e.getClass().getSimpleName(), e.getMessage());
            throw new PutFileException(e.getMessage());
        }
    }

    @Override
    public Boolean removeFile(String path) throws RemoveFileException {
        try {
            s3Client.deleteObject(bucketName, path);
        } catch (Exception e) {
            log.error("remove file error invoked, type:{}, message:{}", e.getClass().getSimpleName(), e.getMessage());
            throw new RemoveFileException(e.getMessage());
        }
        return true;
    }
}
