package com.connectus.services;

import com.connectus.dto.request.AboutUsRequestDTO;
import com.connectus.entity.AboutUs;
import com.connectus.entity.Auth;
import com.connectus.exception.GeneralException;
import com.connectus.exception.ErrorType;
import com.connectus.repository.AboutUsRepository;
import com.connectus.repository.AuthRepository;
import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AboutUsService {
    private final AuthRepository authRepository;
    private final AboutUsRepository aboutUsRepository;
    private final JwtTokenManager jwtTokenManager;


    public Boolean save(AboutUsRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());


        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        AboutUs aboutUs = AboutUs.builder()
                .content(dto.content())
                .build();

        aboutUsRepository.save(aboutUs);
        return true;
    }

    public Boolean delete(AboutUsRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));


        AboutUs aboutUs = aboutUsRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorType.PROJECT_NOT_FOUND));

        aboutUsRepository.delete(aboutUs);
        return true;
    }

    public Boolean update(AboutUsRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());


        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));


        AboutUs aboutUs = aboutUsRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Hakkımızda bilgisi bulunamadı!"));


        if (dto.content() != null) {
            aboutUs.setContent(dto.content());
        }

        aboutUsRepository.save(aboutUs);
        return true;
    }
    public AboutUs find() {
        return aboutUsRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Hakkımızda bilgisi bulunamadı!"));
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
