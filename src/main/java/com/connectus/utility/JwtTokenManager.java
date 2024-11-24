package com.connectus.utility;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.connectus.exception.AuthServiceException;
import com.connectus.exception.ErrorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.connectus.exception.ErrorType.TOKEN_FORMAT_NOT_ACCEPTABLE;
import static com.connectus.exception.ErrorType.TOKEN_VERIFY_FAILED;

@Service
public class JwtTokenManager {
    @Value("${auth.secret.secret-key}")
    String secretKey;
    @Value("${auth.secret.issuer}")
    String issuer;
    private final Long EXDATE = 1000L * 60 * 60 ;

    public Optional<String> createToken (Long authId){
        String token;
        try{
            token = JWT.create().withAudience()
                    .withClaim("authId", authId)
                    .withIssuer(issuer)
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXDATE))
                    .sign(Algorithm.HMAC512(secretKey));
            return Optional.of(token);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    public Optional<Long> validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            if(decodedJWT == null)
                return Optional.empty();
            Long authId = decodedJWT.getClaim("authId").asLong();
            return Optional.of(authId);
        }catch (Exception e){
            return Optional.empty();
        }
    }
    public Optional<Long> getAuthIdFromToken(String token){
        DecodedJWT decodedJWT = null;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
            decodedJWT = verifier.verify(token);

            if(decodedJWT==null){
                return Optional.empty();
            } else {
                return Optional.of(decodedJWT.getClaim("authId").asLong());
            }

        } catch (IllegalArgumentException e) {
            throw new AuthServiceException(TOKEN_FORMAT_NOT_ACCEPTABLE);
        } catch (JWTVerificationException e) {
            throw new AuthServiceException(TOKEN_VERIFY_FAILED);
        }
    }

    public Optional<Long> getIdFromToken(String token){
        try {
            Algorithm algorithm=Algorithm.HMAC512(secretKey);
            JWTVerifier verifier=JWT.require(algorithm).withIssuer(issuer).build();
            DecodedJWT decodedJWT= verifier.verify(token);

            if (decodedJWT==null){
                throw new AuthServiceException(ErrorType.INVALID_TOKEN);
            }

            Long id=decodedJWT.getClaim("authId").asLong();
            return Optional.of(id);

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new AuthServiceException(ErrorType.INVALID_TOKEN);
        }
    }
    //  Şifre Sıfırlama İçin Email İle Token Oluşturma
    public Optional<String> createPasswordResetToken(String email){
        String token;
        try {
            token = JWT.create()
                    .withClaim("email", email) // Email'i token içinde saklıyoruz
                    .withIssuer(issuer)
                    .withIssuedAt(new Date()) // Şu anki tarih
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXDATE)) // Token geçerlilik süresi
                    .sign(Algorithm.HMAC512(secretKey)); // HMAC512 algoritması ile imzalama
            return Optional.of(token);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
  //  Token'dan Email'i Almak (Şifre Sıfırlama İçin)

    public Optional<String> getEmailFromToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            if (decodedJWT == null) {
                throw new AuthServiceException(ErrorType.INVALID_TOKEN);
            }

            String email = decodedJWT.getClaim("email").asString(); // Token'dan email bilgisi çıkarılıyor
            return Optional.ofNullable(email);
        } catch (Exception e) {
            throw new AuthServiceException(ErrorType.INVALID_TOKEN);
        }
    }


}