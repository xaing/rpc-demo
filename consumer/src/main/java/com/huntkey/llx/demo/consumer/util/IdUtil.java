package com.huntkey.llx.demo.consumer.util;

/**
 * Created by lulx on 2019/2/23 0023 下午 14:52
 */
public class IdUtil {

    private final static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    public static String getId() {
        return String.valueOf(idWorker.nextId());
    }
}
