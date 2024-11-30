package com.connectus.services;

import com.connectus.Model.*;
import com.connectus.exception.ErrorType;
import com.connectus.exception.GlobalExceptionHandler;
import com.connectus.repository.AuthRepository;
import com.connectus.utility.CodeGenerator;
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

    public String forgetPassword(String email) {
        MailModel mailModel = MailModel.builder()
                .code(CodeGenerator.generateResetPasswordCode())
                .email(email)
                .build();

        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));

        auth.setCode(mailModel.getCode());

        // Kullanıcıyı veritabanında güncelliyoruz
        try {
            authRepository.save(auth);
        } catch (Exception e) {
            // Veritabanı kaydını yaparken bir hata oluşursa
            throw new GeneralException(ErrorType.INTERNAL_SERVER_ERROR);
        }

        // E-posta gönderme işlemini gerçekleştiriyoruz
        try {
            emailService.sendMail(mailModel);
        } catch (Exception e) {
            // E-posta gönderimi başarısızsa hata fırlatıyoruz
            throw new GeneralException(ErrorType.EMAIL_SEND_FAILED);
        }

        // Kullanıcıya mesaj döndürüyoruz
        return "Şifreme yenileme kodunuz " + email + " adresine gönderildi.";
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
