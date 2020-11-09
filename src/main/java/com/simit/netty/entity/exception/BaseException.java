package com.simit.netty.entity.exception;

/**
 * @Author: ys xu
 * @Date: 2020/11/4 15:29
 */
public class BaseException extends RuntimeException{
    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }
}

