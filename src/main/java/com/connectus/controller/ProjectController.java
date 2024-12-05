package com.connectus.controller;
import com.connectus.dto.request.*;
import com.connectus.entity.Project;
import com.connectus.services.ProjectService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static com.connectus.constants.EndPoints.*;

@RestController
@RequestMapping(PROJECT)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class ProjectController {
    private final ProjectService projectService;
        /**
         * Yeni bir proje kaydetme endpoint'i
         */
        @PostMapping(SAVE)
        public ResponseEntity<Boolean> saveProject(@RequestBody ProjectSaveRequestDTO dto) {
            Boolean isSaved = projectService.save(dto);
            return ResponseEntity.ok(isSaved); // Başarı durumunu döner
        }
        /**
         * Bir projeyi silme endpoint'i
         */
        @DeleteMapping(DELETE)
        public ResponseEntity<Boolean> deleteProject(@RequestBody ProjectDeleteRequestDTO dto) {
            Boolean isDeleted = projectService.delete(dto);
            return ResponseEntity.ok(isDeleted); // Silme işleminin durumunu döner
        }
        /**
         * Tüm projeleri listeleme endpoint'i
         */
        @GetMapping(FINDALL)
        public ResponseEntity<List<Project>> findAllProjects() {
            List<Project> projects = projectService.findAll();
            return ResponseEntity.ok(projects); // Tüm projeleri döner
        }
    }
