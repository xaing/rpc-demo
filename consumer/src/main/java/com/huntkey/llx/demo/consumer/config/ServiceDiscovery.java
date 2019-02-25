package com.huntkey.llx.demo.consumer.config;

import com.alibaba.fastjson.JSONObject;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lulx on 2019/2/23 0023 下午 15:52
 */
@Component
public class ServiceDiscovery {

    private static final Logger log = LoggerFactory.getLogger(ServiceDiscovery.class);

    // 服务地址列表
    private volatile List<String> addressList = new ArrayList<>();
    private static final String ZK_REGISTRY_PATH = "/rpc";
    private ZkClient zkClient;

    @Value("${registry.address}")
    private String registryAddress;

    @Autowired
    ConnectManage connectManage;

    @PostConstruct
    public void init() {
        zkClient = connectServer();
        if (zkClient != null) {
            watchNode();
        }
    }

    private ZkClient connectServer() {
        ZkClient client = new ZkClient(registryAddress, 20000, 20000);
        return client;
    }

    private void watchNode() {
        List<String> nodeList = zkClient.subscribeChildChanges(ZK_REGISTRY_PATH, (s, nodes) -> {
            log.info("监听到子节点数据变化{}", JSONObject.toJSONString(nodes));
            addressList.clear();
            getNodeData(nodes);
            updateConnectedServer();
        });
        getNodeData(nodeList);
        log.info("已发现服务列表...{}", JSONObject.toJSONString(addressList));
        updateConnectedServer();
    }

    private void getNodeData(List<String> nodes) {
        log.info("/rpc子节点数据为:{}", JSONObject.toJSONString(nodes));
        for (String node : nodes) {
            String address = zkClient.readData(ZK_REGISTRY_PATH + "/" + node);
            addressList.add(address);
        }
    }

    private void updateConnectedServer() {
        connectManage.updateConnectServer(addressList);
    }
}
