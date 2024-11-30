package com.connectus.services;

import com.connectus.Model.*;
import com.connectus.exception.ErrorType;
import com.connectus.repository.AuthRepository;
import com.connectus.utility.JwtTokenManager;
import com.connectus.utility.PasswordEncoder;
import com.connectus.dto.request.*;
import com.connectus.entity.Auth;
import com.connectus.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.connectus.exception.ErrorType.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    private void checkEmailExist(String email) {
        if (authRepository.existsByEmail(email)) {
            throw new GeneralException(EMAIL_ALREADY_TAKEN);
        }
    }

    private void checkPasswordMatch(String password, String rePassword) {
        if (!password.equals(rePassword)) {
            throw new GeneralException(PASSWORD_MISMATCH);
        }
    }

    public String login(LoginRequestDTO dto) {
        Auth auth = authRepository.findOptionalByEmail(dto.email())
                .orElseThrow(() -> new GeneralException(EMAIL_OR_PASSWORD_WRONG));

        if (!passwordEncoder.bCryptPasswordEncoder().matches(dto.password(), auth.getPassword())) {
            throw new GeneralException(EMAIL_OR_PASSWORD_WRONG);
        }

        String token = jwtTokenManager.createToken(auth.getId())
                .orElseThrow(() -> new GeneralException(TOKEN_CREATION_FAILED));
        return token;
    }

    public Boolean resetPassword(ResetPasswordRequestDTO dto) {
        String email = jwtTokenManager.getEmailFromToken(dto.token()).orElseThrow(() -> new GeneralException(INVALID_TOKEN));
        Auth auth = authRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new GeneralException(USER_NOT_FOUND));
        if (!dto.newPassword().equals(dto.rePassword())) {
            throw new GeneralException(PASSWORD_MISMATCH);
        }

        String encodedPassword = passwordEncoder.bCryptPasswordEncoder().encode(dto.newPassword());
        auth.setPassword(encodedPassword);
        authRepository.save(auth);
        return true;
    }
    public Boolean loginProfileManagement(LoginProfileManagementDTO dto,String token) {
        String jwtToken = token.replace("Bearer ", "");
        Long authId = jwtTokenManager.getIdFromToken(jwtToken).orElseThrow(() -> new GeneralException(INVALID_TOKEN));
        Auth auth = authRepository.findById(authId).orElseThrow(() -> new GeneralException(USER_NOT_FOUND));

        if (!passwordEncoder.bCryptPasswordEncoder().matches(dto.password(), auth.getPassword())) {
            throw new GeneralException(PASSWORD_WRONG);
        }

        return true;
    }
    public Boolean changeMyPassword(ChangeMyPasswordRequestDTO dto, String token) {
        String jwtToken = token.replace("Bearer ", "");
        Long authId = jwtTokenManager.getIdFromToken(jwtToken).orElseThrow(() -> new GeneralException(INVALID_TOKEN));
        if(!authId.equals(dto.authId())){
            throw new GeneralException(INVALID_TOKEN);
        }
        Auth auth = authRepository.findById(authId).orElseThrow(() -> new GeneralException(USER_NOT_FOUND));
        if(!dto.newPassword().equals(dto.newConfirmPassword())){
            throw new GeneralException(PASSWORD_MISMATCH);
        }

        auth.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(dto.newPassword()));
        authRepository.save(auth);

        return true;
    }

    public Boolean forgetPassword(String toEmail) {
        // E-posta adresine sahip kullanıcıyı bul
        Auth auth = authRepository.findByEmail(toEmail)
                .orElseThrow(() -> new GeneralException(AUTH_NOT_FOUND));

        // Şifre sıfırlama için rastgele bir link oluştur
        String resetLink = emailService.generateRandomLink();  //

        // Şifre sıfırlama URL'si
        String resetUrl = "http://localhost:8080/reset-password?link=" + resetLink;

        try {
            // E-posta ile sıfırlama linkini gönder
            emailService.sendPasswordResetEmail(toEmail, resetLink);
            return true;
        } catch (MessagingException e) {
            // E-posta gönderiminde hata durumunda exception fırlat
            throw new GeneralException(EMAIL_SEND_FAILED);
        }
    }




    public void updateEmailOfAuth(UpdateEmailOfAuth dto) {
        Optional<Auth> auth = authRepository.findById(dto.getAuthId());
        if (auth.isPresent()) {
            auth.get().setEmail(dto.getEmail());
            authRepository.save(auth.get());
        }
    }

    public String findEmailByAuthId(Long authId) {
        return authRepository.findById(authId)
                .map(Auth::getEmail)
                .orElse(null);
    }

    public boolean checkEmailExists(String email) {
        return authRepository.existsByEmailIgnoreCase(email);
    }

    public void changePasswordByAdmin(ChangePasswordFromUserModel changePasswordFromUserModel) {
        Auth auth = authRepository.findById(changePasswordFromUserModel.getAuthId())
                .orElseThrow(() -> new GeneralException(USER_NOT_FOUND));
        auth.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(changePasswordFromUserModel.getNewPassword()));
        authRepository.save(auth);
    }

}
