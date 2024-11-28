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
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class OurServicesService {

    private final OurServicesRepository ourServicesRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;
    private final MinioService minioService;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public Boolean save(OurServicesSaveRequestDTO dto) {

        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));
        String photoUrl = null;
        if (dto.photo() != null) {
            try {
                photoUrl = minioService.uploadPhoto(dto.photo());
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_UPLOAD_FAILED);
            }
        }
        OurServices ourServices = OurServices.builder()
                .title(dto.title())
                .description(dto.description())
                .photo(photoUrl)
                .build();

        ourServicesRepository.save(ourServices);

        return true;
    }

    private static final String SECRET_KEY = "secret";

    public String getUserFromToken(Long authid) {
        String token = String.valueOf(jwtTokenManager.createToken(authid));
        return token;
    }

    public Boolean delete(OurServicesDeleteRequestDTO dto) {


        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));


        OurServices ourServices = ourServicesRepository.findById(dto.ourServicesId())
                .orElseThrow(() -> new GeneralException(ErrorType.OURSERVICES_NOT_FOUND));


        if (ourServices.getPhoto() != null) {
            try {
                minioService.deletePhoto(ourServices.getPhoto());
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_DELETE_FAILED);
            }
        }

        // Delete service from database
        ourServicesRepository.delete(ourServices);

        return true;
    }


    public Boolean update(OurServicesUpdateRequestDTO dto) {

        // Extract user ID from token
        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        // Find the service to update
        OurServices ourServices = ourServicesRepository.findById(dto.ourServicesId())
                .orElseThrow(() -> new GeneralException(ErrorType.OURSERVICES_NOT_FOUND));

        if (dto.title() != null) {
            ourServices.setTitle(dto.title());
        }
        if (dto.description() != null) {
            ourServices.setDescription(dto.description());
        }
        if (dto.photo() != null) {
            try {
                if (ourServices.getPhoto() != null) {
                    minioService.deletePhoto(ourServices.getPhoto());
                }

                String newPhotoUrl = minioService.uploadPhoto(dto.photo());
                ourServices.setPhoto(newPhotoUrl);
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_UPDATE_FAILED);
            }
        }


        ourServicesRepository.save(ourServices);

        return true;
    }

    public List<OurServices> findAll() {
        List<OurServices> services = ourServicesRepository.findAll();
        return services;
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