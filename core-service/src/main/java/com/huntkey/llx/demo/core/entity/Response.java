package com.huntkey.llx.demo.core.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by lulx on 2019/2/23 0023 上午 10:21
 */
@ToString
@Getter
@Setter
public class Response {
    private String requestId;
    private int code;
    private String error_msg;
    private Object data;
}
