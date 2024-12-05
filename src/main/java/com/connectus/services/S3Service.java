package com.connectus.services;


import com.connectus.dto.response.UrlResponseDTO;
import com.connectus.exception.ErrorType;
import com.connectus.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    public String putObject(String bucketName, String key, MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.putObject(objectRequest, RequestBody.fromBytes(fileBytes));

        return s3Client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()).toExternalForm();
    }


    public byte[] getObject(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest)) {
            return response.readAllBytes();
        } catch (SdkException | IOException e) {
            throw new RuntimeException("Failed to retrieve object from S3", e);
        }
    }


    public void deleteObject(String bucketName, String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (Exception e) {

        }
    }

    public String extractS3KeyFromUrl(String url) {

        try {
            URL photoUrl = new URL(url);
            String path = photoUrl.getPath();
            return path.startsWith("/") ? path.substring(1) : path;  // Remove leading slash
        } catch (MalformedURLException e) {
            throw new GeneralException(ErrorType.PHOTO_DELETE_FAILED);
        }
    }

    public String createPresignedGetUrl(String bucketName, String keyName) {
        try (S3Presigner presigner = S3Presigner.create()) {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofDays(7))  // The URL will expire in 1 day .
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toExternalForm();
        }
    }
        public UrlResponseDTO getPresignedUrl (String fileName){
            String key = "photos/%s".formatted(fileName);
            String presignedGetUrl = createPresignedGetUrl("connect-us-storage-bucket", key);
            return new UrlResponseDTO(presignedGetUrl);
        }

}