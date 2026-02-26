package com.duri.durifront;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DuriFrontApplication {

    public static void main(String[] args) {
        SpringApplication.run(DuriFrontApplication.class, args);
    }

}
