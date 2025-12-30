package com.servletstack.adapter.out.storage;

import com.servletstack.application.port.out.storage.StorageService;
import com.servletstack.infrastructure.annotations.Provider;
import com.servletstack.infrastructure.config.Properties;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URI;
import java.time.Duration;

@Slf4j
@ApplicationScoped
@Provider("s3")
public class S3Service implements StorageService {

    private String bucketName;
    private S3Presigner presigner;
    private S3Client s3Client;

    @PostConstruct
    public void init() {
        this.bucketName = Properties.get("s3.bucket.name");
        Region region = Region.of(Properties.get("s3.region"));

        this.presigner = S3Presigner.builder().region(region).build();
        this.s3Client = S3Client.builder().region(region).build();
    }

    @Override
    public URI generateUploadUri(String path, String contentType, Duration validity) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(path)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(validity)
                        .putObjectRequest(putRequest)
                        .build();
        try {
            return presigner.presignPutObject(presignRequest).url().toURI();
        } catch (Exception e) {
            log.error("Failed to generate image URL for path {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to generate image URL", e);
        }
    }

    @Override
    public URI generatePresignedUri(String path, Duration validity) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(path)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(validity)
                        .getObjectRequest(getRequest)
                        .build();
        try {
            return presigner.presignGetObject(presignRequest).url().toURI();
        } catch (Exception e) {
            log.error("Failed to generate download URL for path {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to generate download URL", e);
        }
    }

    @Override
    public URI generatePublicUri(String path) {
        try {
            return s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucketName).key(path).build())
                    .toURI();
        } catch (Exception e) {
            log.error("Failed to generate public URL for path {}: {}", path, e.getMessage());
            throw new RuntimeException("Failed to generate public URL", e);
        }
    }

    @Override
    public void deleteFile(String path) {
        log.info("Deleting file from S3. bucket={}, path={}", bucketName, path);

        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(path)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to delete file from S3. bucket={}, path={}, error={}", bucketName, path, e.getMessage());
        }
    }
}
