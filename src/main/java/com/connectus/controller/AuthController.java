package com.connectus.controller;

import com.connectus.exception.GeneralException;
import com.connectus.services.AuthService;
import com.connectus.dto.request.*;
import com.connectus.dto.response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.connectus.constants.EndPoints.*;

@RestController
@RequestMapping(AUTH)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT,RequestMethod.DELETE})
public class AuthController {
    private final AuthService authService;

    @PostMapping(LOGIN)
    @Operation(
            summary = "Login a user",
            description = "Logs in a user with the provided credentials. The credentials must be provided in the request body."
    )
    public ResponseEntity<ResponseDTO<String>> login(@RequestBody LoginRequestDTO dto){

        return ResponseEntity.ok(ResponseDTO.<String>builder()
                .code(200)
                .message("Succesfully logged in")
                .data(authService.login(dto))
                .build());
    }
    @PostMapping(FORGETPASSWORD)
    @Operation(
            summary = "Forget password",
            description = "Forgets the password of a user with the provided email. The email must be provided in the request body.")
    public ResponseEntity<String> forgetPassword(@RequestParam String email) {
        try {
            String responseMessage = authService.forgetPassword(email);
            return ResponseEntity.ok(responseMessage);
        } catch (GeneralException e) {
            return ResponseEntity.status(e.getErrorType().getHttpStatus())
                    .body(e.getErrorType().getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Bir hata oluştu. Lütfen tekrar deneyin.");
        }
    }

    @PostMapping (RESETPASSWORD)
    @Operation(
            summary = "Reset password",
            description = "Resets the password of a user with the provided email and reset token. The email and reset token must be provided in the request body.")
    public ResponseEntity<ResponseDTO<Boolean>> resetPassword(@RequestBody ResetPasswordRequestDTO dto){
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder()
                .data(authService.resetPassword(dto)).code(200).message("Password reset successfully").build());
    }


    @PostMapping("/login-profile-management")
    public ResponseEntity<ResponseDTO<Boolean>> loginProfileManagement(@RequestBody LoginProfileManagementDTO dto, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder().code(200).data(authService.loginProfileManagement(dto,token)).message("Login Profile Management Approved").build());
    }

    @PutMapping("/change-my-password")
    public ResponseEntity<ResponseDTO<Boolean>> changeMyPassword(@RequestBody ChangeMyPasswordRequestDTO dto, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(ResponseDTO.<Boolean>builder().code(200).message("Password changed").data(authService.changeMyPassword(dto,token)).build());
    }



}