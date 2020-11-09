package com.simit.netty.entity.exception;

/**
 * @Author: ys xu
 * @Date: 2020/11/4 16:14
 */
public class DeviceIdException extends BaseException{
    public DeviceIdException() {
        super();
    }

    public DeviceIdException(String message) {
        super(message);
    }

    public DeviceIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceIdException(Throwable cause) {
        super(cause);
    }
}
