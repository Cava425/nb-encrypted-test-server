package com.simit.entity;

/**
 * @Author: ys xu
 * @Date: 2020/10/27 15:53
 */
public class GasAddress {

    private Long id;
    private String formalDomain;
    private String encryptDomain;
    private String autoDomain;
    private Long ts;

    public GasAddress(Long id, String formalDomain, String encryptDomain, String autoDomain, Long ts) {
        this.id = id;
        this.formalDomain = formalDomain;
        this.encryptDomain = encryptDomain;
        this.autoDomain = autoDomain;
        this.ts = ts;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormalDomain() {
        return formalDomain;
    }

    public void setFormalDomain(String formalDomain) {
        this.formalDomain = formalDomain;
    }

    public String getEncryptDomain() {
        return encryptDomain;
    }

    public void setEncryptDomain(String encryptDomain) {
        this.encryptDomain = encryptDomain;
    }

    public String getAutoDomain() {
        return autoDomain;
    }

    public void setAutoDomain(String autoDomain) {
        this.autoDomain = autoDomain;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "GasAddress{" +
                "id=" + id +
                ", formalDomain='" + formalDomain + '\'' +
                ", encryptUdpDomain='" + encryptDomain + '\'' +
                ", autoUdpDomain='" + autoDomain + '\'' +
                ", ts=" + ts +
                '}';
    }
}
