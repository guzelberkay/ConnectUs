package com.connectus.exception;

import lombok.Getter;


@Getter
public class ProjectServiceException extends RuntimeException{
     private ErrorType errorType;
    public ProjectServiceException(ErrorType errorType){
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
