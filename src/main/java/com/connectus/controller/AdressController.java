package com.connectus.controller;

import com.connectus.dto.request.AdressRequestDTO;
import com.connectus.dto.request.AdressUpdateRequestDTO;
import com.connectus.dto.request.PhoneRequestDTO;
import com.connectus.dto.request.PhoneUpdateRequestDTO;
import com.connectus.dto.response.ResponseDTO;
import com.connectus.entity.Adress;
import com.connectus.entity.Phone;
import com.connectus.services.AdressService;
import com.connectus.services.PhoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.connectus.constants.EndPoints.*;
import static com.connectus.constants.EndPoints.DELETE;

@RestController
@RequestMapping(ADRESS)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class AdressController {

    private final AdressService adressService;

    @PostMapping(SAVE)
    public ResponseEntity<ResponseDTO<Boolean>> save(@RequestBody AdressRequestDTO dto) {
        try {
            Boolean success = adressService.save(dto);
            return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                    .data(success)
                    .code(200)
                    .message("Adress successfully registered")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<Boolean>builder()
                            .data(false)
                            .code(400)
                            .message("Error saving adress: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping(FINDALL)

    public ResponseEntity<List<Adress>> findAll() {
        try {
            List<Adress> adress = adressService.findAll();
            return ResponseEntity.ok(adress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping(UPDATE)
    public ResponseEntity<ResponseDTO<Boolean>> update(@RequestBody AdressUpdateRequestDTO dto) {
        try {
            Boolean success = adressService.update(dto);
            return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                    .data(success)
                    .code(200)
                    .message("Adress successfully updated")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<Boolean>builder()
                            .data(false)
                            .code(400)
                            .message("Error updating adress: " + e.getMessage())
                            .build()
            );
        }
    }

    @DeleteMapping(DELETE)
    public ResponseEntity<ResponseDTO<Boolean>> delete(@RequestParam String token, @RequestParam Long id) {
        try {
            Boolean success = adressService.delete(token, id);
            return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                    .data(success)
                    .code(200)
                    .message(success ? "Adress successfully deleted" : "Adress not found")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseDTO.<Boolean>builder()
                            .data(false)
                            .code(400)
                            .message("Error deleting adress: " + e.getMessage())
                            .build()
            );
        }
    }
}