package com.huntkey.llx.demo.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by lulx on 2019/2/22 0022 下午 17:11
 */
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
        System.out.println(ConsumerApplication.class.getName() + " - 启动完成");
    }
}
