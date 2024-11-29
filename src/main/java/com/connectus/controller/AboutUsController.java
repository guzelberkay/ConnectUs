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

    private final AboutUsService aboutUsService;

    /**
     * Hakkımızda bilgisi kaydedilir veya güncellenir.
     * @param dto Hakkımızda bilgisi içeren DTO
     * @return İşlemin başarılı olup olmadığına dair bilgi
     */
    @PostMapping(SAVE_OR_UPDATE)
    public ResponseEntity<Boolean> saveOrUpdate(@RequestBody AboutUsRequestDTO dto) {
        try {
            Boolean result = aboutUsService.saveOrUpdate(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(false);
        }
    }

    /**
     * Hakkımızda bilgisini getirir.
     * @return Hakkımızda bilgisi
     */
    @GetMapping("/find")
    public ResponseEntity<AboutUs> find() {
        try {
            AboutUs aboutUs = aboutUsService.find();
            return ResponseEntity.ok(aboutUs);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }



}
