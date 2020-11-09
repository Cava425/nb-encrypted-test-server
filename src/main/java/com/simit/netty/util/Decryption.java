package com.simit.netty.util;

import com.alibaba.fastjson.JSON;
import com.simit.netty.Datagram;

import com.simit.netty.entity.exception.DecryptionException;
import net.people2000.soaps.HttpsUtls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Map;

@Component
public class Decryption {

    private static final Logger logger = LoggerFactory.getLogger(Decryption.class);

    @Value("${server.decrypt-url}")
    private String decryptUrl;

    @Value("${server.encrypt-url}")
    private String encryptUrl;

    public Datagram decrypt(Datagram datagram) {

        String decData = "data=" + datagram.getData();
        try {
            Map result = (Map) JSON.parse(req(decData, decryptUrl));
            if (Integer.parseInt(result.get("nErrorCode").toString()) < 0) {
                throw new DecryptionException("解密失败");
            } else {
                datagram.setPugid(result.get("pugid").toString());
                datagram.setData(result.get("denData").toString().toUpperCase());
                logger.info("解密后数据: " + datagram.toString().toUpperCase());
            }
        } catch (Exception e) {
            throw new DecryptionException(e);
        }

        return datagram;
    }

    public Datagram encrypt(Datagram datagram){
        String encData = "puid=" + datagram.getPugid() + "&" + "data=" + datagram.getData();
        try {
            Map result = (Map) JSON.parse(req(encData, encryptUrl));
            if (Integer.parseInt(result.get("nErrorCode").toString()) < 0) {
                throw new DecryptionException("加密失败");
            } else {
                datagram.setData(result.get("denData").toString().toUpperCase());
                logger.info("加密后数据: " + datagram.toString().toUpperCase());
            }
        } catch (Exception e) {
            throw new DecryptionException(e);
        }

        return datagram;
    }

    /**
     * 发送请求
     *
     * @param data
     * @param host
     */
    private String req(String data, String host) throws Exception {
        URL url = null;
        String A = "";

        url = new URL(new URL(host), "", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                URL target = new URL(u.toString());
                URLConnection connection1 = target.openConnection();
                connection1.setConnectTimeout(500000);
                connection1.setReadTimeout(500000);
                return connection1;
            }
        });

        HttpURLConnection http = null;

        HttpsUtls.trustAllHttpsCert();
        http = (HttpURLConnection) url.openConnection();

        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.connect();
        OutputStream os = http.getOutputStream();
        os.write(data.getBytes());
        os.flush();
        InputStream is = http.getInputStream();
        int k = 0;
        while ((k = is.read()) != -1) {
            A += (char) k;
        }

        return A;
    }
}
