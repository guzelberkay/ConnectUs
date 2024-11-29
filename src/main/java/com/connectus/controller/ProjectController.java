package com.connectus.controller;
import com.connectus.dto.request.*;
import com.connectus.dto.response.ResponseDTO;
import com.connectus.entity.Project;
import com.connectus.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.connectus.constants.EndPoints.*;

@RestController
@RequestMapping(PROJECT)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping(SAVE)
    @Operation(
            summary = "Save a new service",
            description = "This method is used to register a new service in the system. Service details must be provided in the request body.")
    public ResponseEntity<ResponseDTO<Boolean>> save(
            @RequestPart("photo") MultipartFile photo,
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart("token") String token) {

        ProjectSaveRequestDTO dto = new ProjectSaveRequestDTO(photo, title, description, token);
        Boolean result = projectService.save(dto);

        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(result)
                .code(200)
                .message("Successfully registered")
                .build());
    }

    @DeleteMapping(DELETE)
    @Operation(
            summary = "Delete a service",
            description = "Deletes a service from the system based on the provided service ID. " +
                    "This action will remove the service from the database permanently, and it cannot be undone.")
    public ResponseEntity<ResponseDTO<Boolean>> delete(@RequestBody ProjectDeleteRequestDTO dto) {
        Boolean result = projectService.delete(dto);
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(result)
                .code(200)
                .message("Service deleted successfully")
                .build());
    }

    @PutMapping(UPDATE)
    @Operation(
            summary = "Update an existing service",
            description = "Updates an existing service in the system. The service ID must be provided, " +
                    "and the fields to be updated (title, description, photo) can be supplied in the request body. " +
                    "If any field is not provided, it will remain unchanged.")
    public ResponseEntity<ResponseDTO<Boolean>> update(@RequestBody ProjectUpdateRequestDTO dto) {
        Boolean result = projectService.update(dto);
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(result)
                .code(200)
                .message("Service updated successfully")
                .build());
    }
    @GetMapping(FIND_ALL_BY_PROJECT_ID)
    @Operation(
            summary = "Find Project by ID",
            description = "Fetches a specific project by its unique ID. If the project has a photo, a pre-signed URL will be included in the response."
    )
    public ResponseEntity<ResponseDTO<Project>> findById(@PathVariable Long id) {
        Project project = projectService.findById(id);
        return ResponseEntity.ok(ResponseDTO.<Project>builder()
                .data(project)
                .code(200)
                .message("Project retrieved successfully")
                .build());
    }


    @GetMapping(FINDALL)
    @Operation(
            summary = "Retrieve All Services",
            description = "Returns a list of all services. This endpoint fetches all the services stored in the system and returns them as a list of `Project` objects.")
    public ResponseEntity<List<Project>> findAll() {
        List<Project> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/get-user-info")
    public ResponseEntity<ResponseDTO<String>> getUserInfo(@RequestParam Long authId) {
        String userInfo = projectService.getUserFromToken(authId);

        return ResponseEntity.ok(ResponseDTO.<String>builder()
                .data(userInfo)
                .message("User info retrieved successfully")
                .build());
    }
}

