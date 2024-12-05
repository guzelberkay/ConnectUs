package com.connectus.services;

import com.connectus.dto.request.*;
import com.connectus.entity.OurServices;
import com.connectus.entity.Project;
import com.connectus.exception.GeneralException;
import com.connectus.exception.ErrorType;
import com.connectus.repository.AuthRepository;
import com.connectus.repository.ProjectRepository;
import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;
    private final S3Service s3Service;
    private final S3Client s3Client;


    @Value("${aws.s3.buckets.customer}")
    private String bucketName;

    public Boolean save(ProjectSaveRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());

        authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        String photoUrl = null;
        if (dto.photo() != null) {
            try {
                String fileKey = UUID.randomUUID().toString() + "_" + dto.photo().getOriginalFilename();
                photoUrl = s3Service.putObject(bucketName, fileKey, dto.photo());
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_UPLOAD_FAILED);
            }
        }

        Project project = Project.builder()
                .title(dto.title())
                .description(dto.description())
                .photo(photoUrl)
                .build();

        projectRepository.save(project);
        return true;
    }

    public Boolean delete(ProjectDeleteRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());

        authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new GeneralException(ErrorType.PROJECT_NOT_FOUND));

        if (project.getPhoto() != null) {
            try {
                String photoKey = s3Service.extractS3KeyFromUrl(project.getPhoto());
                s3Service.deleteObject(bucketName, photoKey);
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_DELETE_FAILED);
            }
        }

        projectRepository.delete(project);
        return true;
    }
    public List<Project> findAll() {
        List<Project> services = projectRepository.findAll();
        services.forEach(service -> {
            if (service.getPhoto() != null) {
                try {
                    String presignedUrl = s3Service.createPresignedGetUrl(bucketName, service.getPhoto());
                    service.setPhoto(presignedUrl);
                } catch (Exception e) {
                    service.setPhoto("default-error-url.jpg"); // Hata durumunda varsayılan bir görsel URL'si
                }
            }
        });
        return services;
    }

    public Project findProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new GeneralException(ErrorType.PROJECT_NOT_FOUND));

        if (project.getPhoto() != null) {
            String presignedUrl = s3Service.createPresignedGetUrl(bucketName, project.getPhoto());
            project.setPhoto(presignedUrl);
        }

        return project;
    }

    private Long extractAuthIdFromToken(String token) {
        return jwtTokenManager.getAuthIdFromToken(token)
                .orElseThrow(() -> new GeneralException(ErrorType.TOKEN_INVALID));
    }
}
