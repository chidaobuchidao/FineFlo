package com.inclusivefinance.common;

public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(400, message);
    }

    public int getCode() { return code; }

    public static void notFound(String message) {
        throw new BusinessException(404, message);
    }

    public static void badRequest(String message) {
        throw new BusinessException(400, message);
    }
}
