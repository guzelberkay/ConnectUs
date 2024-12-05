package com.connectus.services;

import com.connectus.dto.request.*;
import com.connectus.entity.Project;
import com.connectus.exception.GeneralException;
import com.connectus.exception.ErrorType;
import com.connectus.repository.AuthRepository;
import com.connectus.repository.ProjectRepository;
import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;
    private final S3Service s3Service;

    @Value("${AWS_BUCKET_NAME}")
    private String bucketName;

    public Boolean save(ProjectSaveRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());

        authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        String photoUrl = null;
        if (dto.photo() != null) {
            try {
                photoUrl = s3Service.uploadPhoto(dto.photo());
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
                s3Service.deletePhoto(project.getPhoto());
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_DELETE_FAILED);
            }
        }

        projectRepository.delete(project);
        return true;
    }

    public Boolean update(ProjectUpdateRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());

        authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new GeneralException(ErrorType.PROJECT_NOT_FOUND));

        if (dto.title() != null) {
            project.setTitle(dto.title());
        }
        if (dto.description() != null) {
            project.setDescription(dto.description());
        }
        if (dto.photo() != null) {
            try {
                if (project.getPhoto() != null) {
                    s3Service.deletePhoto(project.getPhoto());
                }

                String newPhotoUrl = s3Service.uploadPhoto(dto.photo());
                project.setPhoto(newPhotoUrl);
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_UPDATE_FAILED);
            }
        }

        projectRepository.save(project);
        return true;
    }

    public String getPresignedUrl(String objectName) {
        return s3Service.generatePresignedUrl(objectName);
    }

    public List<Project> findAll() {
        List<Project> projects = projectRepository.findAll();
        projects.forEach(project -> {
            if (project.getPhoto() != null) {
                String presignedUrl = getPresignedUrl(project.getPhoto());
                project.setPhoto(presignedUrl);
            }
        });
        return projects;
    }

    public Project findProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new GeneralException(ErrorType.PROJECT_NOT_FOUND));

        if (project.getPhoto() != null) {
            String presignedUrl = getPresignedUrl(project.getPhoto());
            project.setPhoto(presignedUrl);
        }

        return project;
    }

    private Long extractAuthIdFromToken(String token) {
        return jwtTokenManager.getAuthIdFromToken(token)
                .orElseThrow(() -> new GeneralException(ErrorType.TOKEN_INVALID));
    }
}
