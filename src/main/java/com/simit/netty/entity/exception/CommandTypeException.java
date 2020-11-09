package com.simit.netty.entity.exception;

/**
 * @Author: ys xu
 * @Date: 2020/11/4 15:54
 */
public class CommandTypeException extends BaseException{
    public CommandTypeException() {
        super();
    }

    public CommandTypeException(String message) {
        super(message);
    }

    public CommandTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandTypeException(Throwable cause) {
        super(cause);
    }
}
