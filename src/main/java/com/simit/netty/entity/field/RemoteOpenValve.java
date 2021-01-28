package com.simit.netty.entity.field;

/**
 * @Author: ys xu
 * @Date: 2020/11/6 16:11
 */
public class RemoteOpenValve {

    private String openValve;
    private String filledRandNum;

    public RemoteOpenValve(String filledRandNum){
        this.openValve = "55";
        this.filledRandNum = filledRandNum;
    }

    public String getRemoteCloseValve(){
        return openValve + filledRandNum;
    }
}
