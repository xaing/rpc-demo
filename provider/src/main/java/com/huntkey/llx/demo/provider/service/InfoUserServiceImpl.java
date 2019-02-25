package com.huntkey.llx.demo.provider.service;

import com.alibaba.fastjson.JSONObject;
import com.huntkey.llx.demo.core.annotation.RpcService;
import com.huntkey.llx.demo.core.entity.InfoUser;
import com.huntkey.llx.demo.core.service.InfoUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lulx on 2019/2/23 0023 上午 10:23
 */
@RpcService
public class InfoUserServiceImpl implements InfoUserService {

    private static final Logger logger = LoggerFactory.getLogger(InfoUserServiceImpl.class);

    /**
     * 当做数据库，存储用户信息
     */
    Map<String, InfoUser> infoUserMap = new HashMap<String, InfoUser>();

    @Override
    public List<InfoUser> insertInfoUser(InfoUser infoUser) {
        logger.info("新增用户信息:{}", JSONObject.toJSONString(infoUser));
        infoUserMap.put(infoUser.getId(), infoUser);
        return getInfoUserList();
    }

    private List<InfoUser> getInfoUserList() {
        List<InfoUser> userList = new ArrayList<>(infoUserMap.values());
        logger.info("返回用户信息记录数:{}", userList.size());
        return userList;
    }

    @Override
    public InfoUser getInfoUserById(String id) {
        InfoUser infoUser = infoUserMap.get(id);
        logger.info("查询用户ID:{}", id);
        return infoUser;
    }

    @Override
    public void deleteInfoUserById(String id) {
        logger.info("删除用户信息:{}", JSONObject.toJSONString(infoUserMap.remove(id)));
    }

    @Override
    public String getNameById(String id) {
        logger.info("根据ID查询用户名称:{}", id);
        return infoUserMap.get(id).getName();
    }

    @Override
    public Map<String, InfoUser> getAllUser() {
        logger.info("查询所有用户信息{}", infoUserMap.keySet().size());
        return infoUserMap;
    }
}
