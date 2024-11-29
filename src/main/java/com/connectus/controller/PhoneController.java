package com.connectus.controller;

import com.connectus.dto.request.PhoneRequestDTO;
import com.connectus.dto.request.PhoneUpdateRequestDTO;
import com.connectus.dto.response.ResponseDTO;
import com.connectus.entity.Phone;
import com.connectus.services.PhoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.connectus.constants.EndPoints.*;

@RestController
@RequestMapping(PHONE)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class PhoneController {

    private final PhoneService phoneService;

    @PostMapping(SAVE)
    public ResponseEntity<ResponseDTO<Boolean>> save(@RequestBody PhoneRequestDTO dto) {
        try {
            Boolean success = phoneService.save(dto);
            return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                    .data(success)
                    .code(200)
                    .message("Phone successfully registered")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<Boolean>builder()
                            .data(false)
                            .code(400)
                            .message("Error saving phone: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping(FINDALL)

    public ResponseEntity<List<Phone>> findAll() {
        try {
            List<Phone> phones = phoneService.findAll();
            return ResponseEntity.ok(phones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping(UPDATE)
    public ResponseEntity<ResponseDTO<Boolean>> update(@RequestBody PhoneUpdateRequestDTO dto) {
        try {
            Boolean success = phoneService.update(dto);
            return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                    .data(success)
                    .code(200)
                    .message("Phone successfully updated")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<Boolean>builder()
                            .data(false)
                            .code(400)
                            .message("Error updating phone: " + e.getMessage())
                            .build()
            );
        }
    }

    @DeleteMapping(DELETE)
    public ResponseEntity<ResponseDTO<Boolean>> delete(@RequestParam String token, @RequestParam Long id) {
        try {
            Boolean success = phoneService.delete(token, id);
            return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                    .data(success)
                    .code(200)
                    .message(success ? "Phone successfully deleted" : "Phone not found")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<Boolean>builder()
                            .data(false)
                            .code(400)
                            .message("Error deleting phone: " + e.getMessage())
                            .build()
            );
        }
    }
}
