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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;
    private final MinioService minioService;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public Boolean save(ProjectSaveRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());

        authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        String photoUrl = null;
        if (dto.photo() != null) {
            try {
                photoUrl = minioService.uploadPhoto(dto.photo());
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
                minioService.deletePhoto(project.getPhoto());
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
                    minioService.deletePhoto(project.getPhoto());
                }

                String newPhotoUrl = minioService.uploadPhoto(dto.photo());
                project.setPhoto(newPhotoUrl);
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_UPDATE_FAILED);
            }
        }

        projectRepository.save(project);
        return true;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public String getUserFromToken(Long authId) {
        Optional<Long> optionalAuthId = jwtTokenManager.getAuthIdFromToken(authId.toString());
        if (optionalAuthId.isPresent()) {
            return String.valueOf(jwtTokenManager.createToken(optionalAuthId.get()));
        } else {
            throw new GeneralException(ErrorType.TOKEN_INVALID);
        }
    }

    private Long extractAuthIdFromToken(String token) {
        return jwtTokenManager.getAuthIdFromToken(token)
                .orElseThrow(() -> new GeneralException(ErrorType.TOKEN_INVALID));
    }
}
