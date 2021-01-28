package com.simit.entity;

/**
 * @Author: ys xu
 * @Date: 2020/10/28 9:07
 */
public class EncryptResult {

    private Long id;
    private String gasId;
    private String batchId;
    private Integer samplingFlag;
    private Long exportTs;
    private Integer flag;
    private String originData;
    private Long ts;

    public EncryptResult(String gasId, String batchId, Integer samplingFlag, Long exportTs, Integer flag) {
        this.id = id;
        this.gasId = gasId;
        this.batchId = batchId;
        this.samplingFlag = samplingFlag;
        this.exportTs = exportTs;
        this.flag = flag;
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

    public Integer getSamplingFlag() {
        return samplingFlag;
    }

    public void setSamplingFlag(Integer samplingFlag) {
        this.samplingFlag = samplingFlag;
    }

    public Long getExportTs() {
        return exportTs;
    }

    public void setExportTs(Long exportTs) {
        this.exportTs = exportTs;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getOriginData() {
        return originData;
    }

    public void setOriginData(String originData) {
        this.originData = originData;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "EncryptResult{" +
                "id=" + id +
                ", gasId='" + gasId + '\'' +
                ", batchId='" + batchId + '\'' +
                ", samplingFlag=" + samplingFlag +
                ", exportTs=" + exportTs +
                ", flag=" + flag +
                ", originData='" + originData + '\'' +
                ", ts=" + ts +
                '}';
    }
}
