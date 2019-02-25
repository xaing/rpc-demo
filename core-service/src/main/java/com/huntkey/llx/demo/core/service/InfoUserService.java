package com.huntkey.llx.demo.core.service;

import com.huntkey.llx.demo.core.entity.InfoUser;

import java.util.List;
import java.util.Map;

/**
 * Created by lulx on 2019/2/23 0023 上午 10:16
 */
public interface InfoUserService {

    List<InfoUser> insertInfoUser(InfoUser infoUser);

    InfoUser getInfoUserById(String id);

    void deleteInfoUserById(String id);

    String getNameById(String id);

    Map<String, InfoUser> getAllUser();
}
