package com.simit;

import com.simit.netty.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NBTestServerApplication implements CommandLineRunner {

    @Autowired
    private NettyServer server;

    @Value("${netty.port}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(NBTestServerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        server.start(port);
    }
}
