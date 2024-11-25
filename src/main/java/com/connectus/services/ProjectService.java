package com.connectus.services;

import com.connectus.dto.request.ProjectDeleteRequestDTO;
import com.connectus.dto.request.ProjectSaveRequestDTO;
import com.connectus.dto.request.ProjectUpdateRequestDTO;
import com.connectus.entity.Auth;
import com.connectus.entity.Project;
import com.connectus.exception.GeneralException;
import com.connectus.exception.ErrorType;
import com.connectus.repository.AuthRepository;
import com.connectus.repository.ProjectRepository;
import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;

    public Boolean save(ProjectSaveRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());

        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Project project = Project.builder()
                .title(dto.title())
                .description(dto.description())
                .photo(dto.photo())
                .build();

        projectRepository.save(project);

        return true;
    }

    public Boolean delete(ProjectDeleteRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));


        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new GeneralException(ErrorType.PROJECT_NOT_FOUND));


        projectRepository.delete(project);
        return true;
    }

    public Boolean update(ProjectUpdateRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
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
            project.setPhoto(dto.photo());
        }
        projectRepository.save(project);

        return true;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    private Long extractAuthIdFromToken(String token) {
        Optional<Long> authIdOptional = jwtTokenManager.getAuthIdFromToken(token);
        if (authIdOptional.isPresent()) {
            return authIdOptional.get();
        } else {
            throw new RuntimeException("AuthId could not be extracted from token");
        }
    }
}
