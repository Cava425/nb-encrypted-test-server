package com.simit.netty.entity.exception;

/**
 * @Author: ys xu
 * @Date: 2020/11/4 15:53
 */
public class GasMeterTypeException extends BaseException {
    public GasMeterTypeException() {
        super();
    }

    public GasMeterTypeException(String message) {
        super(message);
    }

    public GasMeterTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GasMeterTypeException(Throwable cause) {
        super(cause);
    }
}
