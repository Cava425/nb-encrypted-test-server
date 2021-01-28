package com.simit.netty.entity.field;

/**
 * @Author: ys xu
 * @Date: 2020/11/5 13:32
 */
public class RemoteCloseValveEnable {
    private String controlWord;
    private String startTime;
    private String holdingTime;
    private String filledRandNum;

    public RemoteCloseValveEnable(String filledRandNum){
        this.controlWord = "01";
        this.startTime = "0000";
        this.holdingTime = "0030";
        this.filledRandNum = filledRandNum;
    }

    public String getRemoteCloseValveEnable(){
        return controlWord + startTime + holdingTime + filledRandNum;
    }
}
