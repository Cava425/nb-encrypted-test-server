package com.simit.netty;

import java.net.InetSocketAddress;

/**
 * @Author: ys xu
 * @Date: 2020/9/25 16:28
 */

public class Datagram implements Cloneable {
    // 字段域
    private static final String start = "15";
    private static final String end = "14";

    private String type;
    private String id;
    private String controlCode;
    private String length;
    private String commandCode;
    private String data;
    private String cs;

    // 报文一些额外变量
    private String randNum;

    private boolean isCiphertext;
    private boolean isActive;
    private boolean isSuccessful;
    private String pugid;



    // 保存燃气表地址，下发通用包或结束报文时使用
    private InetSocketAddress address;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public static String getStart() {
        return start;
    }

    public static String getEnd() {
        return end;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getControlCode() {
        return controlCode;
    }

    public void setControlCode(String controlCode) {
        this.controlCode = controlCode;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(String commandCode) {
        this.commandCode = commandCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    public boolean isCiphertext() {
        return isCiphertext;
    }

    public void setCiphertext(boolean ciphertext) {
        this.isCiphertext = ciphertext;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public String getPugid() {
        return pugid;
    }

    public void setPugid(String pugid) {
        this.pugid = pugid;
    }

    public String getRandNum() {
        return randNum;
    }

    public void setRandNum(String randNum) {
        this.randNum = randNum;
    }


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString(){
        return start + type + id + controlCode + length + commandCode + data + cs + end;
    }
}
