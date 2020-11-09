package com.simit.netty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @Author: ys xu
 * @Date: 2020/9/25 16:30
 */
public class Converter {

    private static final Logger logger = LoggerFactory.getLogger(Converter.class);

    public static String calculateCS(String data){
        if (data == null || data.equals("")) {
            return "00";
        }

        int total = 0;
        int len = data.length();
        if (len % 2 != 0) {
            return "00";
        }

        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }

        total = total % 256;
        String CS = String.format("%2s", Integer.toHexString(total)).replace(" ", "0");

        return CS.toUpperCase();
    }

    /**
     * 字节转换为 16进制字符串
     *
     * @param bytes
     * @return
     */
    public static String bytes2HexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            hex = String.format("%2s", hex).replace(" ", "0");

            stringBuilder.append(hex.toUpperCase());
        }
        return stringBuilder.toString();
    }

    /**
     * 16进制 字符串转换为字节
     *
     * @param hex
     * @return
     */
    public static byte[] hex2Bytes(String hex){
        if(hex == null || hex.equals("")){
            return null;
        }

        int length = hex.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length / 2; i++) {
            int temp = Integer.parseInt(hex.substring(2 * i, 2 * (i + 1)), 16);
            bytes[i] = (byte)temp;
        }
        return bytes;
    }

    /**
     * 16进制字符串转换为 2进制字符串
     * @param hex
     * @return
     */
    public static String hex2Binary(String hex){

        if(hex == null || hex.equals("")){
            return null;
        }

        int length = hex.length();
        String r = "";
        for (int i = 0; i < length / 2; i++) {
            int temp = Integer.parseInt(hex.substring(2 * i, 2 * (i + 1)), 16);
            r = r +  String.format("%8s", Integer.toBinaryString(temp)).replace(" ", "0");
        }

        return r;
    }

    /**
     * 字符串转换为 16进制字符串 "0" --- 字符 '0' 的ASCII码是 48转换成16进制字符串为 "30"
     *
     * @param str
     * @return
     */
    public static String stringToHexString(String str) {
        StringBuilder hex = new StringBuilder();
        char[] chars = str.toCharArray();
        for (char c : chars) {
            hex.append(Integer.toHexString(c).toUpperCase());
        }
        return hex.toString();
    }

    /**
     *十进制IP地址转换为16进制字符串 实例：192.168.1.153 to C0A80199
     * @param ip 点分十进制IP 192.168.1.153
     * @return
     */
    public static String IPSplit(String ip){
        String[] strings = ip.split("\\.");
        if(strings == null || strings.length != 4){
            logger.info("IP异常: " + Arrays.toString(strings));
            return "";
        }
        StringBuilder builder = new StringBuilder();
        String s = null;
        for (int i = 0; i < strings.length; i++) {
            try {
                s = String.format("%2s",Integer.toHexString(Integer.valueOf(strings[i]))).replace(" ", "0");
                builder.append(s);
            } catch (NumberFormatException e){
                logger.info("IP转换异常: " + strings[i]);
                logger.info(e.toString());
            }
        }
        return builder.toString().toUpperCase();
    }

}
