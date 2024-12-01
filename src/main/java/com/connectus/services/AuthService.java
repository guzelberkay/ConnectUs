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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Boolean resetPassword(ResetPasswordRequestDTO dto) {
        // Şifre sıfırlama kodunu kullanıcıyla eşleştiriyoruz
        Auth auth = authRepository.findOptionalByCode(dto.code())
                .orElseThrow(() -> new GeneralException(ErrorType.USER_NOT_FOUND));

        // Şifreler uyuşmuyorsa hata fırlatıyoruz
        if (!dto.newPassword().equals(dto.rePassword())) {
            throw new GeneralException(ErrorType.PASSWORD_MISMATCH);
        }

        // Reset kodunun süresini kontrol ediyoruz
        CodeGenerator.ResetCode resetCode = new CodeGenerator.ResetCode(auth.getCode(), auth.getCodeTimestamp());
        if (resetCode.isExpired()) {
            throw new GeneralException(ErrorType.EXPIRED_RESET_CODE);  // Token süresi geçmişse hata ver
        }

        // Yeni şifreyi şifreleyip kaydediyoruz
        String encodedPassword = passwordEncoder.bCryptPasswordEncoder().encode(dto.newPassword());
        auth.setPassword(encodedPassword);
        authRepository.save(auth);

        return true; // Şifre başarıyla sıfırlandı
    }





    public String forgetPassword(String email) {
        // Şifre sıfırlama kodunu üret
        CodeGenerator.ResetCode resetCode = CodeGenerator.generateResetPasswordCode();
        System.out.println("Generated Reset Code: " + resetCode.getCode()); // Kodun doğru üretildiğini logla

        // MailModel'ü oluştur
        MailModel mailModel = MailModel.builder()
                .code(resetCode.getCode())  // Reset kodu
                .email(email)  // E-posta
                .build();

        // Kullanıcıyı veritabanından bul
        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND)); // Eğer kullanıcı bulunmazsa hata fırlat

        System.out.println(auth);

        // Kod süresi dolmuşsa, veritabanındaki kodu sil
        if (resetCode.isExpired()) {
            // Silme işlemi yapılabilir
            auth.setCode(null);
            auth.setCodeTimestamp(0);
            authRepository.save(auth);  // Veritabanında güncelleme yap

            // Kullanıcıya bilgi verme
            throw new GeneralException(EXPIRED_RESET_CODE); // Kodun süresi dolmuş, hata fırlat
        }

        // Kullanıcıyı güncelle
        auth.setCode(mailModel.getCode());  // Kod bilgisi
        auth.setCodeTimestamp(System.currentTimeMillis());  // Zaman damgası

        try {
            // Veritabanını kaydet
            authRepository.save(auth);
            System.out.println("Auth record updated successfully with reset code."); // Veritabanı kaydının başarılı olduğunu logla
        } catch (Exception e) {
            e.printStackTrace();  // Veritabanı hatası varsa yazdır
            throw new GeneralException(ErrorType.INTERNAL_SERVER_ERROR); // Hata durumunda genel hata fırlat
        }

        // E-posta gönderimi
        try {
            emailService.sendMail(mailModel);
            System.out.println("Reset code email sent to: " + email); // E-posta gönderiminin başarılı olduğunu logla
        } catch (Exception e) {
            e.printStackTrace();  // E-posta gönderimi hatası
            throw new GeneralException(ErrorType.EMAIL_SEND_FAILED); // E-posta hatası durumunda hata fırlat
        }

        // Şifre sıfırlama kodu gönderildi mesajı
        return "Şifreme yenileme kodunuz " + email + " adresine gönderildi.";
    }

    @Scheduled(fixedRate = 60000)  // 1 dakikada bir çalışır
    public void cleanExpiredCodes() {
        List<Auth> auths = authRepository.findAll();
        for (Auth auth : auths) {
            if (auth.getCode() != null && new CodeGenerator.ResetCode(auth.getCode(), auth.getCodeTimestamp()).isExpired()) {
                authRepository.delete(auth);  // Süresi dolmuş kodu sil
            }
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
    public Auth getAuthFromToken(String token) {
        Long authId = extractAuthIdFromToken(token);
        return authRepository.findById(authId)
                .orElseThrow(() -> new GeneralException(ErrorType.AUTH_NOT_FOUND));
    }



    private Long extractAuthIdFromToken(String token) {
        Optional<Long> authIdOptional = jwtTokenManager.getAuthIdFromToken(token);
        if (authIdOptional.isPresent()) {
            return authIdOptional.get();
        } else {
            throw new RuntimeException("AuthId could not be extracted from token");
        }
    }

}
