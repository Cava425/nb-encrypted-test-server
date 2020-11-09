package com.simit.netty.entity.exception;

/**
 * @Author: ys xu
 * @Date: 2020/11/4 15:31
 */
public class EmptyContentException extends BaseException{
    public EmptyContentException() {
        super();
    }

    public EmptyContentException(String message) {
        super(message);
    }

    public EmptyContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyContentException(Throwable cause) {
        super(cause);
    }
}
