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

    public Boolean saveOrUpdate(AboutUsRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());

        // Kullanıcının doğrulamasını kontrol et
        authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        // "Hakkımızda" bilgisi kontrolü
        Optional<AboutUs> optionalAboutUs = aboutUsRepository.findFirstByOrderByIdAsc();

        AboutUs aboutUs;
        if (optionalAboutUs.isEmpty()) {
            // Eğer "Hakkımızda" bilgisi yoksa yeni oluştur
            aboutUs = AboutUs.builder()
                    .content(dto.content())
                    .build();
        } else {
            // Mevcut bilgiyi güncelle
            aboutUs = optionalAboutUs.get();
            if (dto.content() != null) {
                aboutUs.setContent(dto.content());
            }
        }

        // Veriyi kaydet
        aboutUsRepository.save(aboutUs);
        return true;
    }

    public AboutUs find() {
        return aboutUsRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Hakkımızda bilgisi bulunamadı!"));
    }

    private Long extractAuthIdFromToken(String token) {
        Optional<Long> authIdOptional = jwtTokenManager.getAuthIdFromToken(token);
        if (authIdOptional.isEmpty()) {
            throw new RuntimeException("AuthId could not be extracted from token");
        }
        return authIdOptional.get();
    }
}
