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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OurServicesService {

    private final OurServicesRepository ourServicesRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;

    private final S3Service s3Service;
    @Value("${aws.s3.buckets.customer}")
    private String bucketName;


    public Boolean save(OurServicesSaveRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        String photoUrl = null;
        if (dto.photo() != null) {
            try {

                String fileKey = UUID.randomUUID().toString() + "_" + dto.photo().getOriginalFilename();

                photoUrl = s3Service.putObject(bucketName, fileKey, dto.photo());
            } catch (IOException e) {
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
                String photoKey = s3Service.extractS3KeyFromUrl(ourServices.getPhoto());
                s3Service.deleteObject(bucketName, photoKey);
            } catch (Exception e) {
                throw new GeneralException(ErrorType.PHOTO_DELETE_FAILED);
            }
        }

        ourServicesRepository.delete(ourServices);
        return true;
    }



    public String getPresignedUrl(String objectName) {
        try {

            String keyName = "photos/" + objectName;  // Örneğin, her fotoğraf 'photos/' klasöründe
            return s3Service.createPresignedGetUrl(bucketName, keyName);  // Bucket adı burada direkt kullanılacak
        } catch (Exception e) {
            throw new RuntimeException("Error generating pre-signed URL for object: " + objectName, e);
        }
    }


    public List<OurServices> findAll() {
        List<OurServices> services = ourServicesRepository.findAll();
        services.forEach(service -> {
            if (service.getPhoto() != null) {
                try {
                    byte[] photoBytes = s3Service.getObject(bucketName, service.getPhoto());
                    // Burada photoBytes'ı kullanabilirsiniz, örneğin bir dosya olarak kaydedebilirsiniz
                    // Ama URL'yi set etmek için getPresignedUrl kullanmaya devam etmelisiniz
                    String presignedUrl = getPresignedUrl(service.getPhoto());
                    service.setPhoto(presignedUrl);
                } catch (Exception e) {
                    service.setPhoto("default-error-url.jpg");
                }
            }
        });
        return services;
    }



    public OurServices findServiceById(Long ourServiceId) {
        OurServices ourServices = ourServicesRepository.findById(ourServiceId)
                .orElseThrow(() -> new GeneralException(ErrorType.OURSERVICES_NOT_FOUND));

        if (ourServices.getPhoto() != null) {
            String presignedUrl = getPresignedUrl(ourServices.getPhoto());
            ourServices.setPhoto(presignedUrl);
        }

        return ourServices;
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
