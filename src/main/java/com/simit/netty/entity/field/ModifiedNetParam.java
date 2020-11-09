package com.simit.netty.entity.field;

import com.simit.netty.util.Converter;

/**
 * @Author: ys xu
 * @Date: 2020/11/5 13:44
 */
public class ModifiedNetParam {

    private String ip;
    private String port;
    private String filledRandNum;
    private String reserve1;
    private String reserve2;

    public ModifiedNetParam(String ip, String port, String filledRandNum){
        this.ip = ip;
        this.port = port;
        this.filledRandNum = filledRandNum;
        this.reserve1 = "FFFFFFFF";
        this.reserve2 = "FFFFFFFF";
    }

    public String getModifiedNetParam(){
        return String.format("%-64s", Converter.stringToHexString(ip)).replace(" ", "0")
                + String.format("%4s", Integer.toHexString(Integer.parseInt(port, 10)).toUpperCase())
                + filledRandNum + reserve1 + reserve2;
    }
}
