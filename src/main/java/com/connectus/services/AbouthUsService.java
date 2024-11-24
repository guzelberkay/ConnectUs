package com.connectus.services;

import com.connectus.dto.request.AbouthUsRequestDTO;
import com.connectus.entity.AbouthUs;
import com.connectus.entity.Auth;
import com.connectus.exception.AuthServiceException;
import com.connectus.exception.ErrorType;
import com.connectus.exception.ProjectServiceException;
import com.connectus.repository.AbouthUsRepository;
import com.connectus.repository.AuthRepository;
import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AbouthUsService {
    private final AuthRepository authRepository;
    private final AbouthUsRepository aboutUsRepository;
    private final JwtTokenManager jwtTokenManager;


    public Boolean save(AbouthUsRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());


        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new AuthServiceException(ErrorType.AUTH_NOT_FOUND));

        AbouthUs aboutUs = AbouthUs.builder()
                .content(dto.content())
                .build();

        aboutUsRepository.save(aboutUs);
        return true;
    }

    public Boolean delete(AbouthUsRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new AuthServiceException(ErrorType.AUTH_NOT_FOUND));


        AbouthUs aboutUs = aboutUsRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ProjectServiceException(ErrorType.PROJECT_NOT_FOUND));

        aboutUsRepository.delete(aboutUs);
        return true;
    }

    public Boolean update(AbouthUsRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());


        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new AuthServiceException(ErrorType.AUTH_NOT_FOUND));


        AbouthUs abouthUs = aboutUsRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ProjectServiceException(ErrorType.PROJECT_NOT_FOUND));


        if (dto.content() != null) {
            abouthUs.setContent(dto.content());
        }

        aboutUsRepository.save(abouthUs);
        return true;
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
