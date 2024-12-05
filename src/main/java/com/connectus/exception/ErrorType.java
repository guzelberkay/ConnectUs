package com.connectus.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorType {

    INTERNAL_SERVER_ERROR(1000, "An unexpected error occurred on the server. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST_ERROR(1001, "The provided parameters are incorrect. Please correct them and try again.", HttpStatus.BAD_REQUEST),
    ERROR_DUPLICATE_USERNAME(1002, "This username is already taken. Please choose a different one and try again.", HttpStatus.BAD_REQUEST),
    ERROR_CREATE_TOKEN(1003, "Token creation error. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_INVALID_LOGIN_PARAMETER(1004, "Incorrect username or password. Please correct them and try again.", HttpStatus.BAD_REQUEST),
    ERROR_EMAIL_ISCOMPANY(1005, "The provided email address must be a company email. Please correct it and try again.", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1006, "The provided token is invalid. Please try again.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(1007, "User not found. Please try again.", HttpStatus.NOT_FOUND),
    USER_IS_NOT_ACTIVE(1008, "User is not active.", HttpStatus.NOT_FOUND),
    PASSWORD_NOT_MATCH(1009, "Passwords do not match. Please correct them and try again.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(1010, "This username is already taken. Please choose a different one and try again.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(1011, "Passwords do not match. Please correct them and try again.", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_TAKEN(1012, "This email address is already taken. Please use a different one and try again.", HttpStatus.BAD_REQUEST),
    TOKEN_CREATION_FAILED(1013, "Token creation failed. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_UNVERIFIED(1014, "Email is not verified. Please verify it and try again.", HttpStatus.BAD_REQUEST),
    INVALID_LOGIN_PARAMETER(1015, "Incorrect username or password. Please correct them and try again.", HttpStatus.BAD_REQUEST),
    USER_IS_ACTIVE(1016, "User is active.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_DELETED(1017, "User already deleted.", HttpStatus.BAD_REQUEST),
    EMAIL_OR_PASSWORD_WRONG(1018, "Email or password is incorrect. Please try again.", HttpStatus.BAD_REQUEST),
    PASSWORD_WRONG(1019, "Password is incorrect. Please try again.", HttpStatus.BAD_REQUEST),
    TOKEN_FORMAT_NOT_ACCEPTABLE(1020, "Token format is not acceptable. Please check and try again.", HttpStatus.BAD_REQUEST),
    TOKEN_VERIFY_FAILED(1021, "Token verification failed. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE),
    AUTH_NOT_FOUND(1022, "Authentication not found. Please try again.", HttpStatus.NOT_FOUND),
    PROJECT_NOT_FOUND(1023, "Project not found. Please try again.", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(1024, "Comment not found. Please try again.", HttpStatus.NOT_FOUND),
    CONTACT_NOT_FOUND(1025, "Contact not found. Please try again.", HttpStatus.NOT_FOUND),
    COMMENT_ALREADY_APPROVED_OR_REJECTED(1026, "The comment has already been approved or rejected.", HttpStatus.BAD_REQUEST),
    OURSERVICES_NOT_FOUND(1027, "Our Service not found. Please try again.", HttpStatus.NOT_FOUND),
    PHOTO_UPLOAD_FAILED(1028, "Failed to upload photo. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR),
    PHOTO_DELETE_FAILED(1029, "Failed to delete photo from MinIO. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR),
    PHOTO_UPDATE_FAILED(1030, "Failed to update photo. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_INVALID(1031, "The provided token is invalid. Please try again.", HttpStatus.UNAUTHORIZED),
    EMAIL_SEND_FAILED(1032, "Email sending failed. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR),
    PHOTO_URL_GENERATION_FAILED(1033, "Error generating photo URL", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PHOTO_FORMAT(1033,"Invalid photo format", HttpStatus.INTERNAL_SERVER_ERROR),
    EXPIRED_RESET_CODE(1033,"Reset code has expired", HttpStatus.INTERNAL_SERVER_ERROR);


    private Integer code;
    private String message;
    private HttpStatus httpStatus;
}
