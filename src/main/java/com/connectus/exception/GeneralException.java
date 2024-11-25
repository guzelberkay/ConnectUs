package com.connectus.exception;

import lombok.Getter;


@Getter
public class GeneralException extends RuntimeException{
     private ErrorType errorType;
    public GeneralException(ErrorType errorType){
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
