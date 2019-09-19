package com.jdcloud.apigateway.signature;

public enum ErrorCode {

    ARGUMENT_NOT_SUPPORT(40001, "参数{0}不支持"),

    ARGUMENT_NOT_FOUND(40002, "参数{0}不能为空"),
    
    ARGUMENT_INVALID_ARGUMENT(40003, "参数{0}存在错误"),

    OPERATION_FAILED(50000, "操作失败"),
    ;

    private Integer code;

    private String msg;

    ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * @return the httpCode
     */
    public Integer getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }
}
