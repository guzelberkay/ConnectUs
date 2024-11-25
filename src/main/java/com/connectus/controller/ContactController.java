package com.connectus.controller;

import com.connectus.dto.request.*;
import com.connectus.dto.response.ResponseDTO;
import com.connectus.entity.Contact;
import com.connectus.entity.Project;
import com.connectus.services.ContactService;
import com.connectus.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.connectus.constants.EndPoints.*;

@RestController
@RequestMapping(CONTACT)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT,RequestMethod.DELETE})
public class ContactController {
    private final ContactService contactService;

    @PostMapping(SAVE)
    @Operation(summary = "Save a new contact with addresses and phones", description = "Saves a contact with multiple addresses and phones.")
    public ResponseEntity<ResponseDTO<Boolean>> save(@RequestBody ContactSaveRequestDTO dto) {
        Boolean success = contactService.save(dto);
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(success)
                .code(200)
                .message("Successfully registered")
                .build());
    }

    @GetMapping(FINDALL)
    @Operation(summary = "Find all contacts", description = "Retrieves all contacts along with their addresses and phones.")
    public ResponseEntity<List<Contact>> findAll() {
        List<Contact> contacts = contactService.findAll();
        return ResponseEntity.ok(contacts);
    }

    @PutMapping(UPDATE)
    @Operation(summary = "Update contact with addresses and phones", description = "Updates the contact and its related addresses and phones.")
    public ResponseEntity<ResponseDTO<Boolean>> update(@RequestBody ContactUpdateDTO dto) {
        Boolean success = contactService.update(dto);
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(success)
                .code(200)
                .message("Successfully updated")
                .build());
    }


    @DeleteMapping(DELETE)
    @Operation(summary = "Delete contact's address or phone", description = "Deletes a contact's related addresses or phones based on the provided IDs.")
    public ResponseEntity<ResponseDTO<Boolean>> delete(@RequestBody DeleteRequestDTO dto) {
        Boolean success = contactService.delete(dto);

        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(success)
                .code(200)
                .message(success ? "Successfully deleted" : "No addresses or phones found to delete")
                .build());
    }



}
