package com.connectus.services;

import com.connectus.dto.request.AdressRequestDTO;
import com.connectus.dto.request.AdressUpdateRequestDTO;

import com.connectus.entity.Adress;
import com.connectus.entity.Auth;

import com.connectus.exception.ErrorType;
import com.connectus.exception.GeneralException;
import com.connectus.repository.AdressRepository;
import com.connectus.repository.AuthRepository;

import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdressService {

    private final AuthRepository authRepository;
    private final AdressRepository adressRepository;
    private final JwtTokenManager jwtTokenManager;

    public Boolean save(AdressRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Adress adress = Adress.builder()
                .description(dto.description())
                .value(dto.value())
                .build();
        adressRepository.save(adress);
        return true;
    }

    public Boolean delete(String token, Long id) {
        Long authId = extractAuthIdFromToken(token);
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Adress adress = adressRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorType.CONTACT_NOT_FOUND));

        adressRepository.delete(adress);
            return true;
    }

    public Boolean update(AdressUpdateRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Adress adress = adressRepository.findById(dto.adressId())
                .orElseThrow(() -> new GeneralException(ErrorType.CONTACT_NOT_FOUND));

        adress.setDescription(dto.description());
        adress.setValue(dto.value());
        adressRepository.save(adress);
            return true;
    }

    public List<Adress> findAll() {
        return adressRepository.findAll();
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
