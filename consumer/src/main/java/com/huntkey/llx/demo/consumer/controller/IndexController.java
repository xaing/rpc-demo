package com.huntkey.llx.demo.consumer.controller;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.llx.demo.core.entity.InfoUser;
import com.huntkey.llx.demo.core.service.InfoUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lulx on 2019/2/23 0023 下午 14:28
 */
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private InfoUserService userService;

    @GetMapping("getById")
    @ResponseBody
    public InfoUser getById(@RequestParam String id) {
        logger.info("根据ID查询用户信息:{}", id);
        return userService.getInfoUserById(id);
    }

    @PostMapping("insertInfoUser")
    @ResponseBody
    public List<InfoUser> insertInfoUser(@RequestBody InfoUser infoUser) {
        logger.info("根据ID查询用户信息:{}", infoUser);
        return userService.insertInfoUser(infoUser);
    }

    @GetMapping("getAllUser")
    @ResponseBody
    public Map<String, InfoUser> getAllUser() throws InterruptedException {

        long start = System.currentTimeMillis();
        int thread_count = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(thread_count);
        for (int i = 0; i < thread_count; i++) {
            new Thread(() -> {
                Map<String, InfoUser> allUser = userService.getAllUser();
                logger.info("查询所有用户信息：{}", JSONObject.toJSONString(allUser));
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        logger.info("线程数：{},执行时间:{}", thread_count, (end - start));
        return null;
    }
}
