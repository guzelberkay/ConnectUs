package com.connectus.services;

import com.connectus.dto.request.OurServicesSaveRequestDTO;
import com.connectus.dto.request.OurServicesDeleteRequestDTO;
import com.connectus.dto.response.OurServicesResponseDTO;
import com.connectus.entity.Auth;
import com.connectus.entity.OurServices;
import com.connectus.exception.ErrorType;
import com.connectus.exception.GeneralException;
import com.connectus.repository.AuthRepository;
import com.connectus.repository.OurServicesRepository;
import com.connectus.utility.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OurServicesService {

    private final OurServicesRepository ourServicesRepository;
    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;

    public Boolean save(OurServicesSaveRequestDTO dto) throws IOException {
        Long authId = extractAuthIdFromToken(dto.token());
        Auth auth = authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        // Create a new OurServices entity and set values
        OurServices ourServices = new OurServices();
        ourServices.setTitle(dto.title());
        ourServices.setDescription(dto.description());
        String originalFilename = dto.photo().getOriginalFilename();
        String contentType = dto.photo().getContentType();
        byte[] fileData = dto.photo().getBytes();

        ourServices.setName(originalFilename);
        ourServices.setType(contentType);
        ourServices.setFile(fileData);

        // Save entity to database
        ourServicesRepository.save(ourServices);

        // Return the metadata or file URL
        return true; // You could also return the ID or file URL here
    }





    public Boolean delete(OurServicesDeleteRequestDTO dto) {
        Long authId = extractAuthIdFromToken(dto.token());
        authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        OurServices ourServices = ourServicesRepository.findById(dto.ourServicesId())
                .orElseThrow(() -> new GeneralException(ErrorType.OURSERVICES_NOT_FOUND));

        ourServicesRepository.delete(ourServices);
        return true;
    }


    public byte[] getOneImage(Long id) {
        ourServicesRepository.findById(id).
                orElseThrow(() -> new IllegalStateException("image with " + id + " doesn't exist"));
        return ourServicesRepository.findById(id).get().getFile();
    }
    public List<OurServicesResponseDTO> findAll() {
        return ourServicesRepository.findAll().stream()
                .map(service -> new OurServicesResponseDTO(
                        service.getId(),
                        service.getTitle(),
                        service.getDescription(),
                        "https://connectus-27o3.onrender.com/dev/v1/ourservice/" + service.getId() // Fotoğraf için erişim URL'si

                ))
                .collect(Collectors.toList());
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
