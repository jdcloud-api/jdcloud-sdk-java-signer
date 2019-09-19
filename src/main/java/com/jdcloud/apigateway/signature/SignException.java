package com.jdcloud.apigateway.signature;

import java.text.MessageFormat;


public class SignException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    Integer code;
    String message;

    public SignException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public SignException(ErrorCode errorCode, String... param) {
        this.code = errorCode.getCode();
        this.message = MessageFormat.format(errorCode.getMsg(), param);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
