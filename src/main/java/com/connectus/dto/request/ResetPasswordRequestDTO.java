package com.connectus.dto.request;


public record ResetPasswordRequestDTO(
        String code,
        String newPassword,
        String rePassword)
{
}
