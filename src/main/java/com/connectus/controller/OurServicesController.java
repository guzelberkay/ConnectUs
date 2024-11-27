package com.connectus.controller;

import com.connectus.dto.request.*;
import com.connectus.dto.response.ResponseDTO;
import com.connectus.entity.OurServices;
import com.connectus.services.OurServicesService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.connectus.constants.EndPoints.*;
import static com.connectus.constants.EndPoints.FINDALL;
@RestController
@RequestMapping(OURSERVICES)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT,RequestMethod.DELETE})
public class OurServicesController {
    private final OurServicesService ourServicesService;

    @PostMapping(SAVE)
    @Operation(summary = "Save a new services",
            description = "This method is used to register a new services in the system. Services details must be provided in the request body.")
    public ResponseEntity<ResponseDTO<Boolean>> save(@RequestBody OurServicesSaveRequestDTO dto){
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(ourServicesService.save(dto))
                .code(200)
                .message("Succesfully registered")
                .build());
    }

    @DeleteMapping(DELETE)
    @Operation(
            summary = "Delete a services",
            description = "Deletes a services from the system based on the provided services ID. " +
                    "This action will remove the services from the database permanently, and it cannot be undone."
    )
    public ResponseEntity<ResponseDTO<Boolean>> delete(@RequestBody OurServicesDeleteRequestDTO dto) {
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(ourServicesService.delete(dto))
                .code(200)
                .message("Services deleted successfully")
                .build());
    }

    @PutMapping(UPDATE)
    @Operation(
            summary = "Update an existing services",
            description = "Updates an existing services in the system. The services ID must be provided, " +
                    "and the fields to be updated (title, description, photo) can be supplied in the request body. " +
                    "If any field is not provided, it will remain unchanged."
    )
    public ResponseEntity<ResponseDTO<Boolean>> update(@RequestBody OurServicesUpdateRequestDTO dto) {
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(ourServicesService.update(dto))
                .code(200)
                .message("Our Services updated successfully")
                .build());
    }

    @GetMapping(FINDALL)
    @Operation(
            summary = "Retrieve All Services",
            description = "Returns a list of all services. This endpoint fetches all the services stored in the system and returns them as a list of `Services` objects."
    )
    public ResponseEntity<List<OurServices>> findAll() {
        List<OurServices> ourServices = ourServicesService.findAll();
        return ResponseEntity.ok(ourServices);
    }

}
