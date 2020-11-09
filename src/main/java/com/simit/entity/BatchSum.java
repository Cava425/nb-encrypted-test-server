package com.simit.entity;

import java.beans.IntrospectionException;

/**
 * @Author: ys xu
 * @Date: 2020/11/2 16:32
 */
public class BatchSum {

    private Long id;
    private String batchId;
    private Integer samplingFlag;
    private Integer successfulNum;
    private Integer failureNum;
    private Integer totalNum;
    private Long ts;

    public BatchSum(Long id, String batchId, Integer samplingFlag, Integer successfulNum, Integer failureNum, Integer totalNum, Long ts) {
        this.id = id;
        this.batchId = batchId;
        this.samplingFlag = samplingFlag;
        this.successfulNum = successfulNum;
        this.failureNum = failureNum;
        this.totalNum = totalNum;
        this.ts = ts;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Integer getSamplingFlag() {
        return samplingFlag;
    }

    public void setSamplingFlag(Integer samplingFlag) {
        this.samplingFlag = samplingFlag;
    }

    public Integer getSuccessfulNum() {
        return successfulNum;
    }

    public void setSuccessfulNum(Integer successfulNum) {
        this.successfulNum = successfulNum;
    }

    public Integer getFailureNum() {
        return failureNum;
    }

    public void setFailureNum(Integer failureNum) {
        this.failureNum = failureNum;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "BatchSum{" +
                "id=" + id +
                ", batchId='" + batchId + '\'' +
                ", samplingFlag=" + samplingFlag +
                ", successfulNum=" + successfulNum +
                ", failureNum=" + failureNum +
                ", totalNum=" + totalNum +
                ", ts=" + ts +
                '}';
    }
}
