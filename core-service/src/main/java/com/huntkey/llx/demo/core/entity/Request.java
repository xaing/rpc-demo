package com.huntkey.llx.demo.core.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by lulx on 2019/2/23 0023 上午 10:20
 */
@Setter
@Getter
@ToString
public class Request {

    private String id;
    /**
     * 类名
     */
    private String className;

    /**
     * 函数名称
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数列表
     */
    private Object[] parameters;
}
