package com.connectus.controller;

import com.connectus.dto.request.ProjectDeleteRequestDTO;
import com.connectus.dto.request.ProjectSaveRequestDTO;
import com.connectus.dto.request.ProjectUpdateRequestDTO;
import com.connectus.dto.response.ResponseDTO;
import com.connectus.entity.Project;
import com.connectus.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.connectus.constants.EndPoints.*;

@RestController
@RequestMapping(PROJECT)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT,RequestMethod.DELETE})
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping(SAVE)
    @Operation(summary = "Save a new project",
            description = "This method is used to register a new project in the system. Project details must be provided in the request body.")
    public ResponseEntity<ResponseDTO<Boolean>> save(@RequestBody ProjectSaveRequestDTO dto){
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(projectService.save(dto))
                .code(200)
                .message("Succesfully registered")
                .build());
    }

    @DeleteMapping(DELETE)
    @Operation(
            summary = "Delete a project",
            description = "Deletes a project from the system based on the provided project ID. " +
                    "This action will remove the project from the database permanently, and it cannot be undone."
    )
    public ResponseEntity<ResponseDTO<Boolean>> delete(@RequestBody ProjectDeleteRequestDTO dto) {
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(projectService.delete(dto))
                .code(200)
                .message("Project deleted successfully")
                .build());
    }

    @PutMapping(UPDATE)
    @Operation(
            summary = "Update an existing project",
            description = "Updates an existing project in the system. The project ID must be provided, " +
                    "and the fields to be updated (title, description, photo) can be supplied in the request body. " +
                    "If any field is not provided, it will remain unchanged."
    )
    public ResponseEntity<ResponseDTO<Boolean>> update(@RequestBody ProjectUpdateRequestDTO dto) {
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(projectService.update(dto))
                .code(200)
                .message("Project updated successfully")
                .build());
    }

    @GetMapping(FINDALL)
    @Operation(
            summary = "Retrieve All Projects",
            description = "Returns a list of all projects. This endpoint fetches all the projects stored in the system and returns them as a list of `Project` objects."
    )
    public ResponseEntity<List<Project>> findAll() {
        List<Project> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }



}
