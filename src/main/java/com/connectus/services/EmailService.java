package com.connectus.services;

import com.connectus.Model.MailModel;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendMail(MailModel mailModel) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");  // Explicitly set UTF-8 encoding
            helper.setTo(mailModel.getEmail());
            helper.setSubject("Aktivasyon İşlemleri");

            String htmlContent = "<html>" +
                    "<head>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; color: #333333; background-color: #f9f9f9; padding: 20px; }" +
                    "h3 { color: #2C3E50; }" +
                    ".content-container { background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }" +
                    ".code-box { background-color: #f0f8ff; padding: 15px; font-size: 18px; font-weight: bold; color: #34495e; border-radius: 5px; margin-top: 20px; }" +
                    ".footer { color: #7f8c8d; font-size: 12px; margin-top: 30px; }" +
                    ".footer em { font-style: italic; }" +
                    "hr { border: 1px solid #ddd; margin: 20px 0; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='content-container'>" +
                    "<p>Merhaba,</p>" +
                    "<p>Hesabınızı etkinleştirmek için aşağıdaki aktivasyon kodunu kullanabilirsiniz:</p>" +
                    "<div class='code-box'>" +
                    "<strong>" + mailModel.getCode() + "</strong>" +
                    "</div>" +
                    "<hr>" +
                    "<p><strong>Gizlilik Bildirgesi:</strong></p>" +
                    "<p>Bu e-posta, yalnızca belirlenen alıcıya yönelik olarak gönderilmiştir ve içerdiği bilgiler gizli olabilir. Eğer yanlışlıkla bu e-postayı aldıysanız, lütfen gönderene bildiriniz ve mesajı siliniz. E-postadaki bilgilerin yetkisiz kişilerle paylaşılması yasaktır ve gizlilik politikasına aykırıdır.</p>" +
                    "<p><em>Şirket Adı, tüm kullanıcı verilerini gizli tutmayı taahhüt eder. E-posta ile gönderilen bilgiler yalnızca belirtilen amaç için kullanılacaktır ve üçüncü şahıslarla paylaşılmayacaktır.</em></p>" +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>Teşekkür ederiz" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true);  // Set the email body as HTML content
            helper.setFrom(fromEmail);

            javaMailSender.send(message);
        } catch (MessagingException | jakarta.mail.MessagingException e) {
            // Handle error (log it or throw a custom exception)
            e.printStackTrace();
        }
    }
}
