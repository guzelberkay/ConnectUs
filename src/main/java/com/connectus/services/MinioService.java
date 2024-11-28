package com.connectus.services;

import io.minio.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public MinioService(MinioClient minioClient, @Value("${minio.bucket-name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;

        // Bucket var mı diye kontrol et
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Minio bucket kontrol hatası", e);
        }
    }

    // Fotoğraf yükleme
    public String uploadPhoto(MultipartFile file) throws Exception {
        String objectName = "photos/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();  // Eşsiz isim
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Dosya yükleme hatası", e);
        }
        return objectName;
    }

    // Fotoğraf silme
    public void deletePhoto(String objectName) throws Exception {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            throw new RuntimeException("Fotoğraf silme hatası", e);
        }
    }

    // Fotoğraf güncelleme (yeni fotoğraf yükleyip eskiyi silme)
    public String updatePhoto(MultipartFile newFile, String oldObjectName) throws Exception {
        deletePhoto(oldObjectName);
        return uploadPhoto(newFile);
    }

    // Fotoğrafı alma
    public InputStream getPhoto(String objectName) throws Exception {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            throw new RuntimeException("Fotoğraf alma hatası", e);
        }
    }

    // Tüm fotoğrafları listeleme
    public List<Item> listPhotos() throws Exception {
        List<Item> itemList = new ArrayList<>();
        try {
            Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
            for (Result<Item> result : items) {
                try {
                    Item item = result.get();
                    itemList.add(item);
                } catch (Exception e) {
                    // Hata logu ekleyebilirsiniz, fakat döngü devam etsin
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Minio listesi alınırken hata", e);
        }
        return itemList;
    }
}
