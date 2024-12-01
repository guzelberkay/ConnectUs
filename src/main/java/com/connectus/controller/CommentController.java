package com.connectus.controller;

import com.connectus.dto.request.CommentDeleteRequestDTO;
import com.connectus.dto.request.CommentSaveRequestDTO;
import com.connectus.dto.response.CommentResponseDTO;
import com.connectus.dto.response.ResponseDTO;
import com.connectus.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.connectus.constants.EndPoints.*;

@RestController
@RequestMapping(COMMENT)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT,RequestMethod.DELETE})
public class CommentController {


    private final CommentService commentService;

    @PostMapping(SAVE)
    @Operation(
            summary = "Comment saving process",
            description = "This endpoint is responsible for saving a new comment. It accepts a CommentSaveRequestDTO object in the request body and returns a ResponseEntity containing the success status of the operation."
    )
    public ResponseEntity<ResponseDTO<Boolean>> saveComment(@RequestBody CommentSaveRequestDTO dto) {
        boolean isSaved = commentService.save(dto);
        ResponseDTO<Boolean> response = ResponseDTO.<Boolean>builder()
                .data(isSaved)
                .code(200)
                .message("The comment was saved peacefully.")
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(APPROVE)
    @Operation(
            summary = "Approve a comment",
            description = "Approves a comment that is in 'PENDING' status."
    )
    public ResponseEntity<ResponseDTO<Boolean>> approveComment(@RequestBody String token,Long commentId) {
        boolean isApproved = commentService.approveComment(token, commentId);
        ResponseDTO<Boolean> response = ResponseDTO.<Boolean>builder()
                .data(isApproved)
                .code(200)
                .message("The comment was approved successfully.")
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(DELETE)
    @Operation(
            summary = "Comment deleting process",
            description = "This endpoint handles the deletion of a specific comment. It accepts a CommentDeleteRequestDTO object in the request body, identifies the comment to be deleted, and removes it from the database. The operation returns a success status indicating whether the deletion was completed successfully."
    )
    public ResponseEntity<ResponseDTO<Boolean>> deleteComment(@RequestBody CommentDeleteRequestDTO dto) {
        boolean isDeleted = commentService.delete(dto);
        ResponseDTO<Boolean> response = ResponseDTO.<Boolean>builder()
                .data(isDeleted)
                .code(200)
                .message("The comment was deleted peacefully.")
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping(FINDALL)
    @Operation(
            summary = "Get all comments for admin",
            description = "Fetches a list of all comments from the database for the admin to manage (approve, reject, or delete comments)."
    )
    public ResponseEntity<ResponseDTO<List<CommentResponseDTO>>> getAllComments(@RequestBody String token) {
        List<CommentResponseDTO> comments = commentService.getAllComments(token);
        if (comments == null) {
            comments = new ArrayList<>();
        }


        return ResponseEntity.ok(ResponseDTO.<List<CommentResponseDTO>>builder()
                .data(comments)
                .code(200)  // HTTP status code
                .message("Comments retrieved successfully")
                .build());
    }

    @GetMapping(FIND_ALL_BY_PROJECT_ID  )
    @Operation(
            summary = "Get comments for a specific project",
            description = "Fetches a list of all comments associated with a given projectId."
    )
    public ResponseEntity<ResponseDTO<List<CommentResponseDTO>>> getCommentsByProjectId(@RequestBody Long projectId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByProjectId(projectId);

        return ResponseEntity.ok(ResponseDTO.<List<CommentResponseDTO>>builder()
                .data(comments)
                .code(200)
                .message("Comments for project " + projectId + " found successfully.")
                .build());
    }









}
