package com.connectus.controller;

import com.connectus.dto.request.AbouthUsRequestDTO;
import com.connectus.dto.response.ResponseDTO;
import com.connectus.services.AbouthUsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.connectus.constants.EndPoints.*;

@RequestMapping(ABOUTHUS)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT,RequestMethod.DELETE})
public class AbouthUsControler {
    private final AbouthUsService aboutusService;

    @PostMapping(SAVE)
    @Operation(summary = "Create 'About Us' content", description = "Adds new content to the 'About Us' section.")
    public ResponseEntity<ResponseDTO<Boolean>> save(@RequestBody AbouthUsRequestDTO dto) {
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(aboutusService.save(dto))
                .code(200)
                .message("Content created successfully")
                .build());
    }

    @PutMapping(UPDATE )
    @Operation(summary = "Update 'About Us' content", description = "Updates the 'About Us' content with the specified ID.")
    public ResponseEntity<ResponseDTO<Boolean>> update( @RequestBody AbouthUsRequestDTO dto) {
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(aboutusService.update(dto))
                .code(200)
                .message("Content updated successfully")
                .build());
    }

    @DeleteMapping(DELETE )
    @Operation(summary = "Delete 'About Us' content", description = "Deletes the 'About Us' content with the specified ID.")
    public ResponseEntity<ResponseDTO<Boolean>> delete(@RequestBody AbouthUsRequestDTO dto) {
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(aboutusService.delete(dto))
                .code(200)
                .message("Content deleted successfully")
                .build());
    }

}
