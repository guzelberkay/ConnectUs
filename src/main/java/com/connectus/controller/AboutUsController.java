package com.connectus.controller;

import com.connectus.dto.request.AboutUsRequestDTO;
import com.connectus.dto.response.ResponseDTO;
import com.connectus.entity.AboutUs;
import com.connectus.services.AboutUsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.connectus.constants.EndPoints.*;
@RestController
@RequestMapping(ABOUTUS)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT,RequestMethod.DELETE})
public class AboutUsController {
    private final AboutUsService aboutusService;

    @PostMapping(SAVE)
    @Operation(summary = "Create 'About Us' content", description = "Adds new content to the 'About Us' section.")
    public ResponseEntity<ResponseDTO<Boolean>> save(@RequestBody AboutUsRequestDTO dto) {
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(aboutusService.save(dto))
                .code(200)
                .message("Content created successfully")
                .build());
    }

    @PutMapping(UPDATE )
    @Operation(summary = "Update 'About Us' content", description = "Updates the 'About Us' content with the specified ID.")
    public ResponseEntity<ResponseDTO<Boolean>> update( @RequestBody AboutUsRequestDTO dto) {
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(aboutusService.update(dto))
                .code(200)
                .message("Content updated successfully")
                .build());
    }

    @DeleteMapping(DELETE )
    @Operation(summary = "Delete 'About Us' content", description = "Deletes the 'About Us' content with the specified ID.")
    public ResponseEntity<ResponseDTO<Boolean>> delete(@RequestBody AboutUsRequestDTO dto) {
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(aboutusService.delete(dto))
                .code(200)
                .message("Content deleted successfully")
                .build());
    }
    @GetMapping(FINDALL)
    @Operation(
            summary = "Get 'About Us' content",
            description = "Fetches the 'About Us' content. Since only one entry exists, it retrieves the current content from the database."
    )
    public ResponseEntity<AboutUs> find() {
        AboutUs aboutUs = aboutusService.find(); // Hakkımızda bilgisi, entity olarak alınıyor.
        return ResponseEntity.ok(aboutUs);
    }



}
