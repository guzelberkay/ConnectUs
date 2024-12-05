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
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;



    public Boolean save(ProjectSaveRequestDTO dto) {
        // Token'dan authId çıkar
        Long authId = extractAuthIdFromToken(dto.token());
        // Auth ID'nin geçerli olup olmadığını kontrol et
        authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));
        // DTO'dan Project nesnesi oluştur
        Project project = new Project();
        project.setEmployer(dto.employer());
        project.setTitle(dto.title());
        project.setLocation(dto.location());
        project.setDate(dto.date());
        project.setDescription(dto.description());
        // Projeyi kaydet
        projectRepository.save(project);

        return true; // Kaydetme başarılıysa true döner
    }

    public Boolean delete(ProjectDeleteRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());

        authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));
        // Silinmek istenen proje bulunur
        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new GeneralException(ErrorType.PROJECT_NOT_FOUND));
        projectRepository.delete(project);

        return true;
    }

    public List<Project> findAll() {
        List<Project> services = projectRepository.findAll();
        return services;
    }

    public Project findProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new GeneralException(ErrorType.PROJECT_NOT_FOUND));
        return project;
    }

    private Long extractAuthIdFromToken(String token) {
        return jwtTokenManager.getAuthIdFromToken(token)
                .orElseThrow(() -> new GeneralException(ErrorType.TOKEN_INVALID));
    }
}
