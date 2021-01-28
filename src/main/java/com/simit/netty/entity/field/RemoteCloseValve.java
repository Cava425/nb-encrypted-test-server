package com.simit.netty.entity.field;

/**
 * @Author: ys xu
 * @Date: 2020/11/6 16:10
 */
public class RemoteCloseValve {

    private String closeValve;
    private String filledRandNum;

    public RemoteCloseValve(String filledRandNum){
        this.closeValve = "AA";
        this.filledRandNum = filledRandNum;
    }

    public String getRemoteCloseValve(){
        return closeValve + filledRandNum;
    }
}
