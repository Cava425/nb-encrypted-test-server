package com.simit.netty.entity.field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: ys xu
 * @Date: 2020/11/5 12:37
 */

public class EndMessage {

    private String dataTime;
    private String manageWord;
    private String readInterval;
    private String uploadTime;
    private String uploadPeriod;
    private String reserve;
    private LocalDateTime localDateTime;

    public EndMessage() {
    }

    public EndMessage(String manageWord, String uploadPeriod) {
        this.localDateTime = LocalDateTime.now();
        this.dataTime = localDateTime.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        this.manageWord = manageWord;
        this.readInterval = "FFFF";
        this.uploadTime = "FF" + localDateTime.
                plusMinutes(Integer.valueOf(uploadPeriod, 16))
                .format(DateTimeFormatter.ofPattern("HHmmss"));
        this.uploadPeriod = uploadPeriod;
        this.reserve = "FFFF";
    }

    public String getEndMessage(){
        return dataTime + manageWord + readInterval
                + uploadTime + uploadPeriod + reserve;
    }
}
