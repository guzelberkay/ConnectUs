package com.connectus.services;

import com.connectus.entity.Auth;
import com.connectus.repository.AuthRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Varsayılan kullanıcı bilgileri
        String email = "guzelberkay@outlook.com";
        String password = "Herekeliyim59.";

        // Şifreyi hash'le
        String encodedPassword = passwordEncoder.encode(password);

        // Auth nesnesi oluştur
        Auth newUser = new Auth();
        newUser.setEmail(email);
        newUser.setPassword(encodedPassword);

        // Kullanıcıyı veritabanına kaydet
        if (!authRepository.existsByEmail(email)) {
            authRepository.save(newUser);
            System.out.println("Varsayılan kullanıcı başarıyla oluşturuldu: " + email);
        } else {
            System.out.println("Kullanıcı zaten mevcut: " + email);
        }
    }
}
