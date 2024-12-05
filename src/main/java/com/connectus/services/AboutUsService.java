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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AboutUsService {
    private final AuthRepository authRepository;
    private final AboutUsRepository aboutUsRepository;
    private final JwtTokenManager jwtTokenManager;

    @Transactional
    public Boolean saveOrUpdate(AboutUsRequestDTO dto) {

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

        aboutUsRepository.save(aboutUs);
        return true;
    }

    public AboutUs find() {
        return aboutUsRepository.findAll().get(0);

    }

}
