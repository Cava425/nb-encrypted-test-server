package com.simit.netty.entity.exception;

/**
 * @Author: ys xu
 * @Date: 2020/11/4 15:36
 */
public class DataFieldContentException extends BaseException{
    public DataFieldContentException() {
        super();
    }

    public DataFieldContentException(String message) {
        super(message);
    }

    public DataFieldContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataFieldContentException(Throwable cause) {
        super(cause);
    }
}
