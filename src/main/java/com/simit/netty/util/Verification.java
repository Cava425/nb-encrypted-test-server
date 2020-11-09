package com.simit.netty.util;

import com.simit.netty.entity.exception.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: ys xu
 * @Date: 2020/11/4 15:48
 */
public class Verification {

    private static final Set types = new HashSet<String>(){
        {
            add("00");
            add("01");
            add("02");
            add("03");
            add("20");
            add("21");
            add("22");
            add("23");
        }
    };

    public static void verify(String body){
        if(body == null || body.length() == 0){
            throw new EmptyContentException("数据包内容为空");
        }else if(!verifyLength(body)){
            throw new DatagramLengthException("数据包长度异常");
        }else if(!verifyChecksum(body)){
            throw new DatagramValidationException("数据包校验码异常");
        }else if(!types.contains(body.substring(2, 4))){
            throw new GasMeterTypeException("燃气表类型异常");
        }else if(!body.substring(2, 4).equals(body.substring(4, 6))){
            throw new DeviceIdException("燃气表设备号异常");
        }
    }

    private static boolean verifyLength(String body){
        if(body.length() <=0 || body.length() >= 1024) return false;
        String length = String.format("%4s", Integer.toHexString(1 + body.substring(30, body.length() - 4).length() / 2).toUpperCase()).replace(" ", "0");
        if(length.equals(body.substring(24, 28))){
            return true;
        }
        return false;
    }

    private static boolean verifyChecksum(String body){
        String start = body.substring(0, 2);
        String end = body.substring(body.length() - 2);
        if("15".equals(start) && "14".equals(end)){
            String CS = Converter.calculateCS(body.substring(0, body.length() - 4));
            return body.substring(body.length() - 4, body.length() - 2).equals(CS);
        }
        return false;
    }

}
