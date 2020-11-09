package com.simit.netty.entity.exception;

/**
 * @Author: ys xu
 * @Date: 2020/11/4 16:32
 */
public class DecryptionException extends BaseException{
    public DecryptionException() {
        super();
    }

    public DecryptionException(String message) {
        super(message);
    }

    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecryptionException(Throwable cause) {
        super(cause);
    }
}
