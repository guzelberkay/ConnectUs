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

        // Check if user exists
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        // Upload photo to MinIO and get the URL or file name
        String photoUrl = null;
        if (dto.photo() != null) {
            try {
                photoUrl = minioService.uploadPhoto(dto.photo());
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_UPLOAD_FAILED);
            }
        }

        // Save service data to database
        OurServices ourServices = OurServices.builder()
                .title(dto.title())
                .description(dto.description())
                .photo(photoUrl)
                .build();

        ourServicesRepository.save(ourServices);

        return true;
    }

    private static final String SECRET_KEY = "secret";  // JWT için gizli anahtar

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

        // Update fields if provided
        if (dto.title() != null) {
            ourServices.setTitle(dto.title());
        }
        if (dto.description() != null) {
            ourServices.setDescription(dto.description());
        }
        if (dto.photo() != null) {
            try {
                // Delete the old photo from MinIO if it's being replaced
                if (ourServices.getPhoto() != null) {
                    minioService.deletePhoto(ourServices.getPhoto()); // Handle exception in deletePhoto
                }

                // Upload new photo to MinIO and set the URL
                String newPhotoUrl = minioService.uploadPhoto(dto.photo()); // Handle exception in uploadPhoto
                ourServices.setPhoto(newPhotoUrl);
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_UPDATE_FAILED);
            }
        }

        // Save updated service data to the database
        ourServicesRepository.save(ourServices);

        return true;
    }

    public List<OurServices> findAll() {
        // Retrieve all services from the database
        List<OurServices> services = ourServicesRepository.findAll();

        // Optionally, you can process the list to convert photo URLs if needed

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