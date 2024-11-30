package com.connectus.services;
import com.connectus.entity.Auth;
import com.connectus.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.UUID;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AuthRepository authRepository;

    // Şifre sıfırlama bağlantısı gönderme
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        // Şifre sıfırlama URL'si
        String resetUrl = "http://localhost:8080/reset-password?link=" + resetLink;

        String subject = "Şifre Sıfırlama Talebi";
        String body = "<p>Şifrenizi sıfırlamak için aşağıdaki bağlantıya tıklayın:</p>" +
                "<p><a href='" + resetUrl + "'>Şifremi Sıfırla</a></p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Gönderen kişi
            helper.setFrom("java.teamworks@gmail.com");

            // Alıcıyı belirt
            helper.setTo(toEmail);

            helper.setSubject(subject);
            helper.setText(body, true);  // HTML mesaj

            // E-posta gönder
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("E-posta gönderiminde bir hata oluştu.", e);
        }
    }

    // Rastgele bir şifre sıfırlama linki oluştur
    public String generateRandomLink() {
        return UUID.randomUUID().toString();  // Rastgele link oluşturmak için UUID kullanıyoruz
    }
}
