package com.connectus.services;

import com.connectus.dto.request.PhoneRequestDTO;
import com.connectus.dto.request.PhoneUpdateRequestDTO;
import com.connectus.entity.Auth;
import com.connectus.entity.Phone;
import com.connectus.exception.ErrorType;
import com.connectus.exception.GeneralException;
import com.connectus.repository.AuthRepository;
import com.connectus.repository.PhoneRepository;
import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneService {

    private final AuthRepository authRepository;
    private final PhoneRepository phoneRepository;
    private final JwtTokenManager jwtTokenManager;

    public Boolean save(PhoneRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Phone phone = Phone.builder()
                .description(dto.description())
                .value(dto.value())
                .build();
        phoneRepository.save(phone);
        return true;
    }

    public Boolean delete(String token, Long id) {
        Long authId = extractAuthIdFromToken(token);
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Phone phone = phoneRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorType.CONTACT_NOT_FOUND));

            phoneRepository.delete(phone);
            return true;
    }

    public Boolean update(PhoneUpdateRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        Phone phone = phoneRepository.findById(dto.phoneId())
                .orElseThrow(() -> new GeneralException(ErrorType.CONTACT_NOT_FOUND));

            phone.setDescription(dto.description());
            phone.setValue(dto.value());
            phoneRepository.save(phone);
            return true;
    }

    public List<Phone> findAll() {
        return phoneRepository.findAll();
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
