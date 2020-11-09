package com.simit.netty.entity.exception;

/**
 * @Author: ys xu
 * @Date: 2020/11/4 15:37
 */
public class DatagramValidationException extends BaseException{
    public DatagramValidationException() {
        super();
    }

    public DatagramValidationException(String message) {
        super(message);
    }

    public DatagramValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatagramValidationException(Throwable cause) {
        super(cause);
    }
}
