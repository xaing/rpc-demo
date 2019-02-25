package com.huntkey.llx.demo.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by lulx on 2019/2/22 0022 下午 17:11
 */
@SpringBootApplication
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
        System.out.println(ProviderApplication.class.getName() + " - 启动完成");
    }
}
