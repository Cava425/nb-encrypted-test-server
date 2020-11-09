package com.simit.netty.entity.exception;

/**
 * @Author: ys xu
 * @Date: 2020/11/4 15:55
 */
public class DatagramLengthException extends BaseException{
    public DatagramLengthException() {
        super();
    }

    public DatagramLengthException(String message) {
        super(message);
    }

    public DatagramLengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatagramLengthException(Throwable cause) {
        super(cause);
    }
}
