package com.connectus.config;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

            System.out.println("SecurityFilterChain çalıştı galiba...");

            httpSecurity
                    .authorizeHttpRequests(authorize -> authorize
                            .anyRequest().permitAll()  // Tüm istekleri açık hale getir
                    )

                    .csrf(csrf -> csrf.disable()) ;  // CSRF'yi devre dışı bırak
             // Form tabanlı giriş'i devre dışı bırak
            return httpSecurity.build();

        }
    }

