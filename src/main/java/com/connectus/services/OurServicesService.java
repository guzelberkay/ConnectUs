package com.connectus.services;

import com.connectus.dto.request.OurServicesDeleteRequestDTO;
import com.connectus.dto.request.OurServicesSaveRequestDTO;
import com.connectus.dto.request.OurServicesUpdateRequestDTO;
import com.connectus.entity.Auth;
import com.connectus.entity.OurServices;
import com.connectus.exception.ErrorType;
import com.connectus.exception.GeneralException;
import com.connectus.repository.AuthRepository;
import com.connectus.repository.OurServicesRepository;
import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class OurServicesService {
    private final OurServicesRepository ourServicesRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;

    public Boolean save(OurServicesSaveRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());

        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        OurServices ourServices = OurServices.builder()
                .title(dto.title())
                .description(dto.description())
                .photo(dto.photo())
                .build();

        ourServicesRepository.save(ourServices);

        return true;
    }

    public Boolean delete(OurServicesDeleteRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));


        OurServices ourServices = ourServicesRepository.findById(dto.ourServicesId())
                .orElseThrow(() -> new GeneralException(ErrorType.OURSERVICES_NOT_FOUND));


        ourServicesRepository.delete(ourServices);
        return true;
    }

    public Boolean update(OurServicesUpdateRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));


        OurServices ourServices = ourServicesRepository.findById(dto.ourServicesId())
                .orElseThrow(() -> new GeneralException(ErrorType.OURSERVICES_NOT_FOUND));


        if (dto.title() != null) {
            ourServices.setTitle(dto.title());
        }
        if (dto.description() != null) {
            ourServices.setDescription(dto.description());
        }
        if (dto.photo() != null) {
            ourServices.setPhoto(dto.photo());
        }
        ourServicesRepository.save(ourServices);

        return true;
    }

    public List<OurServices> findAll() {
        return ourServicesRepository.findAll();
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
