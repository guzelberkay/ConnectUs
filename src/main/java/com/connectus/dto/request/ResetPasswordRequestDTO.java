package com.connectus.dto.request;


public record ResetPasswordRequestDTO(
        String token,
        String newPassword,
        String rePassword)
{
}
