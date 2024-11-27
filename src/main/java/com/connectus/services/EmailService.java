package com.connectus.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        // Şifre sıfırlama URL'si
        String resetUrl = "http://localhost:8080/reset-password?token=" + resetToken;

        String subject = "Şifre Sıfırlama Talebi";
        String body = "<p>Şifrenizi sıfırlamak için aşağıdaki bağlantıya tıklayın:</p>" +
                "<p><a href='" + resetUrl + "'>Şifremi Sıfırla</a></p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true -> HTML e-posta içeriği

            // Gönderen kişi
            helper.setFrom("java.teamworks@gmail.com");

            // Alıcıyı belirt
            helper.setTo(toEmail);

            helper.setSubject(subject);
            helper.setText(body, true);  // HTML mesaj

            // E-posta gönder
            mailSender.send(message);

        } catch (MessagingException e) {
            // Burada hatayı logluyoruz veya istisna fırlatıyoruz
            e.printStackTrace();  // Hata loglaması yapabilirsiniz
            throw new RuntimeException("E-posta gönderiminde bir hata oluştu.", e);
        }
    }
}
