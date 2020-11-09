package com.simit.entity;

import java.time.LocalDateTime;

/**
 * @Author: ys xu
 * @Date: 2020/10/11 21:05
 */
public class AutoResult {

    private Long id;
    private String gasId;
    private String batchId;
    private String originData;
    private String commandCode;
    private String commandName;
    private String controlCode;
    private Boolean successfulCode;
    private Long createTime;


    public AutoResult(String gasId, String batchId, String originData, String commandCode, String commandName, String controlCode, Boolean successfulCode, Long createTime) {
        this.gasId = gasId;
        this.batchId = batchId;
        this.originData = originData;
        this.commandCode = commandCode;
        this.commandName = commandName;
        this.controlCode = controlCode;
        this.successfulCode = successfulCode;
        this.createTime = createTime;
    }

    public AutoResult(Long id, String gasId, String batchId, String originData, String commandCode, String commandName, String controlCode, Boolean successfulCode, Long createTime) {
        this.id = id;
        this.gasId = gasId;
        this.batchId = batchId;
        this.originData = originData;
        this.commandCode = commandCode;
        this.commandName = commandName;
        this.controlCode = controlCode;
        this.successfulCode = successfulCode;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGasId() {
        return gasId;
    }

    public void setGasId(String gasId) {
        this.gasId = gasId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getOriginData() {
        return originData;
    }

    public void setOriginData(String originData) {
        this.originData = originData;
    }

    public String getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(String commandCode) {
        this.commandCode = commandCode;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getControlCode() {
        return controlCode;
    }

    public void setControlCode(String controlCode) {
        this.controlCode = controlCode;
    }

    public Boolean getSuccessfulCode() {
        return successfulCode;
    }

    public void setSuccessfulCode(Boolean successfulCode) {
        this.successfulCode = successfulCode;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "AutoResult{" +
                "id=" + id +
                ", gasId='" + gasId + '\'' +
                ", batchId='" + batchId + '\'' +
                ", originData='" + originData + '\'' +
                ", commandCode='" + commandCode + '\'' +
                ", commandName='" + commandName + '\'' +
                ", controlCode='" + controlCode + '\'' +
                ", successfulCode='" + successfulCode + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
