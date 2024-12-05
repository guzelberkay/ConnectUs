package com.connectus.services;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    @Autowired
    public S3Service(S3Client s3Client, S3Presigner s3Presigner, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;

        // Ensure the bucket exists
        try {
            if (!s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build()).sdkHttpResponse().isSuccessful()) {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error checking or creating AWS S3 bucket", e);
        }
    }

    // Upload photo with unique name
    public String uploadPhoto(MultipartFile file) throws Exception {
        String objectKey = "photos/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        try (InputStream is = file.getInputStream()) {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(is, file.getSize()));
        } catch (Exception e) {
            throw new RuntimeException("File upload error", e);
        }
        return objectKey;
    }

    // Delete photo
    public void deletePhoto(String objectKey) throws Exception {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting photo", e);
        }
    }

    // Generate pre-signed URL
    public String generatePresignedUrl(String objectKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            // Generate presigned URL valid for 1 hour
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest -> presignRequest
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofHours(1))
            );

            return presignedRequest.url().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating pre-signed URL", e);
        }
    }

    // List all photos
    public List<S3Object> listPhotos() throws Exception {
        try {
            ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build());
            return response.contents().stream()
                    .filter(item -> item.key().startsWith("photos/"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error listing S3 objects", e);
        }
    }

    // Get photo (returns InputStream)
    public InputStream getPhoto(String objectKey) throws Exception {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            ResponseInputStream<GetObjectResponse> responseStream = s3Client.getObject(getObjectRequest);
            return responseStream;
        } catch (Exception e) {
            throw new RuntimeException("Error getting photo", e);
        }
    }
}
