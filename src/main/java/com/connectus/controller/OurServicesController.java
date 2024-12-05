    package com.connectus.controller;

    import com.connectus.dto.request.OurServicesSaveRequestDTO;
    import com.connectus.dto.request.OurServicesDeleteRequestDTO;
    import com.connectus.dto.response.OurServicesResponseDTO;
    import com.connectus.dto.response.ResponseDTO;
    import com.connectus.entity.OurServices;
    import com.connectus.exception.ErrorType;
    import com.connectus.exception.GeneralException;
    import com.connectus.repository.OurServicesRepository;
    import com.connectus.services.OurServicesService;
    import io.swagger.v3.oas.annotations.Operation;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.*;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.util.List;

    import static com.connectus.constants.EndPoints.*;

    @RestController
    @RequestMapping(OURSERVICES)
    @RequiredArgsConstructor
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
    public class OurServicesController {

        private final OurServicesService ourServicesService;
        private final OurServicesRepository ourServicesRepository;

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "Save a new service",
                description = "This method is used to register a new service in the system. Service details must be provided in the request body.")
        public ResponseEntity<ResponseDTO<Boolean>> save(
                @RequestParam("photo") MultipartFile photo,
                @RequestPart("title") String title,
                @RequestPart("description") String description,
                @RequestPart("token") String token) throws IOException {

            // Create DTO from multipart and request body
            OurServicesSaveRequestDTO dto = new OurServicesSaveRequestDTO(photo, title, description, token);

            // Call service to save
            return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                    .data(ourServicesService.save(dto))
                    .code(200)
                    .message("Successfully registered")
                    .build());
        }

        @DeleteMapping(DELETE)
        @Operation(summary = "Delete a service",
                description = "Deletes a service from the system based on the provided services ID.")
        public ResponseEntity<ResponseDTO<Boolean>> delete(@RequestBody OurServicesDeleteRequestDTO dto) {
            return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                    .data(ourServicesService.delete(dto))
                    .code(200)
                    .message("Service deleted successfully")
                    .build());
        }
        @GetMapping("/{id}")
        public HttpEntity<byte[]> getImage(@PathVariable Long id){
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<byte[]>(ourServicesService.getOneImage(id), httpHeaders, HttpStatus.OK);
        }

        @GetMapping(FINDALL)
        public List<OurServicesResponseDTO> getDocument() {
            return ourServicesService.findAll();
        }
    }
