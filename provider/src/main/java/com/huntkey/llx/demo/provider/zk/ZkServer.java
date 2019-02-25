package com.huntkey.llx.demo.provider.zk;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by lulx on 2019/2/23 0023 上午 11:25
 */
@Component
public class ZkServer {

    private static final Logger log = LoggerFactory.getLogger(ZkServer.class);

    @Value("${registry.address}")
    String registryAddress;

    @Value("${server.port}")
    String serverPort;

    @PostConstruct
    public void test() {
        log.info("serverPort : {}", serverPort);
        log.info("registryAddress : {}", registryAddress);
    }

    private static final String ZK_REGISTRY_PATH = "/rpc";

    private static ZkClient ZK_CLIENT;

    @PostConstruct
    public void ZkServer() {
        ZK_CLIENT = connectServer();
        addRootNode();
    }

    public void register(String data) {
        if (data == null) {
            return;
        }
        if (ZK_CLIENT == null) {
            ZK_CLIENT = connectServer();
        }
        createNode(data);
    }

    /**
     * 添加根目录
     */
    private void addRootNode() {
        boolean exists = ZK_CLIENT.exists(ZK_REGISTRY_PATH);
        if (!exists) {
            ZK_CLIENT.createPersistent(ZK_REGISTRY_PATH);
            log.info("创建zookeeper主节点 {}", ZK_REGISTRY_PATH);
        }
    }

    /**
     * 创建临时顺序子节点
     *
     * @param data
     */
    private void createNode(String data) {
        String path = ZK_CLIENT.create(ZK_REGISTRY_PATH + "/provider"
                , data, ZooDefs.Ids.OPEN_ACL_UNSAFE
                , CreateMode.EPHEMERAL_SEQUENTIAL);
        log.info("创建zookeeper数据节点 ({} => {})", path, data);
    }

    private ZkClient connectServer() {
        ZkClient client = new ZkClient(registryAddress
                , 20 * 1000
                , 20 * 1000);
        return client;
    }
}
