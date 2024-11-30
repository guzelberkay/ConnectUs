package com.connectus.utility;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CodeGenerator {

    private static final long EXPIRATION_TIME = TimeUnit.SECONDS.toMillis(300);

    // Kod ve zaman damgası tutan sınıf
    public static class ResetCode {
        private String code;
        private long timestamp;

        public ResetCode(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }

        // 'code' değişkenine dışarıdan erişim sağlamak için getter metodu ekliyoruz
        public String getCode() {
            return code;
        }

        // Kodun süresi dolmuş mu kontrol et
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > EXPIRATION_TIME;
        }
    }

    // Şifre sıfırlama kodu üretme
    public static ResetCode generateResetPasswordCode() {
        String code = generateCode();  // Kod üret
        long timestamp = System.currentTimeMillis();  // Kod üretildiği zaman

        // Üretilen kodu ve zaman damgasını logla
        System.out.println("Generated reset password code: " + code + " at timestamp: " + timestamp);

        return new ResetCode(code, timestamp);  // Kod ve zaman damgası döndür
    }

    // Genel kod üretme fonksiyonu
    private static String generateCode() {
        String uuid = UUID.randomUUID().toString();
        return uuid.split("-")[0];  // UUID'nin ilk bölümünü alıyoruz
    }
}

