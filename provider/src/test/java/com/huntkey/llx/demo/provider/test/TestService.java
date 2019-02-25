package com.huntkey.llx.demo.provider.test;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lulx on 2019/2/25 0025 下午 14:21
 */
public class TestService {
    private static final Logger log = LoggerFactory.getLogger(TestService.class);

    public static void main(String[] args) {
        String s = JSON.parseObject(JSON.toJSONString("id22"), String.class);
        System.out.println(s);
    }
}
