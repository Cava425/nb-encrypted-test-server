package com.simit.entity;

/**
 * @Author: ys xu
 * @Date: 2020/12/23 21:17
 */
public class BatchRecord {
    private String deviceId;
    private String batchId;
    private String samplingFlag;
    private String isTested;
    private Long ts;
    private String simNumber;

    public BatchRecord(String deviceId, String batchId, String samplingFlag, String isTested) {
        this.deviceId = deviceId;
        this.batchId = batchId;
        this.samplingFlag = samplingFlag;
        this.isTested = isTested;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getSamplingFlag() {
        return samplingFlag;
    }

    public void setSamplingFlag(String samplingFlag) {
        this.samplingFlag = samplingFlag;
    }

    public String getIsTested() {
        return isTested;
    }

    public void setIsTested(String isTested) {
        this.isTested = isTested;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }
}
