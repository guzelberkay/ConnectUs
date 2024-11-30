package com.connectus.services;
import com.connectus.Model.MailModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(MailModel mailModel) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(mailModel.getEmail());
        simpleMailMessage.setSubject("Şifre Sıfırlama Kodu");  // Konu
        simpleMailMessage.setText("Şifrenizi sıfırlamak için aşağıdaki kodu kullanın:\n" + mailModel.getCode()); // Mesaj

        // E-posta gönderimi işlemi
        try {
            javaMailSender.send(simpleMailMessage);
        } catch (MessagingException e) {
            // Hata mesajı eklenecek
            throw new RuntimeException("E-posta gönderiminde bir hata oluştu.", e);
        }
    }
}
