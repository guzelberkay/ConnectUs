package com.connectus.services;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    @Autowired
    public S3Service(S3Client s3Client, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;

        // Check if the bucket exists, if not, create it
        try {
            if (!s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build()).sdkHttpResponse().isSuccessful()) {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("AWS S3 bucket check error", e);
        }
    }

    // Upload photo
    public String uploadPhoto(MultipartFile file) throws Exception {
        String objectKey = "photos/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();  // Unique name
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
            throw new RuntimeException("Delete photo error", e);
        }
    }

    // Update photo (upload new photo and delete the old one)
    public String updatePhoto(MultipartFile newFile, String oldObjectKey) throws Exception {
        deletePhoto(oldObjectKey);
        return uploadPhoto(newFile);
    }

    // Get photo
    public InputStream getPhoto(String objectKey) throws Exception {
        try {
            // Get the object from S3
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            // Get the object content and response
            ResponseInputStream<GetObjectResponse> responseStream = s3Client.getObject(getObjectRequest);
            // Return the InputStream of the object content
            return responseStream;
        } catch (Exception e) {
            throw new RuntimeException("Get photo error", e);
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
}
