package com.huntkey.llx.demo.provider.netty;

import com.huntkey.llx.demo.core.annotation.RpcService;
import com.huntkey.llx.demo.core.tool.JsonDecoder;
import com.huntkey.llx.demo.core.tool.JsonEncoder;
import com.huntkey.llx.demo.provider.zk.ZkServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lulx on 2019/2/23 0023 上午 10:43
 */
@Component
public class NettyServer implements ApplicationContextAware, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private Map<String, Object> serviceMap = new HashMap<String, Object>();

    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4);

    @Value("${rpc.server.host}")
    private String serverHost;

    @Value("${rpc.server.port}")
    private int serverPort;

    @Autowired
    private ZkServer zkServer;

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    private void start() {

        final NettyServerHandler handler = new NettyServerHandler(serviceMap);

        new Thread(() -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {

                            /**
                             * /创建NIOSocketChannel成功后，在进行初始化时，
                             * 将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件
                             * @param socketChannel
                             * @throws Exception
                             */
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(new IdleStateHandler(0
                                        , 0
                                        , 60));
                                pipeline.addLast(new JsonEncoder());
                                pipeline.addLast(new JsonDecoder());
                                pipeline.addLast(handler);
                            }
                        });
                ChannelFuture cf = bootstrap.bind(serverHost, serverPort).sync();
                log.info("RPC 服务器启动.监听端口:" + serverPort);
                zkServer.register(serverHost + ":" + serverPort);
                //等待服务端监听端口关闭
                cf.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);

            }
        }).start();
    }

    /**
     * 通过@RpcService注解获取所有的服务类，缓存提供消费者调用
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);
        new ArrayList<>(beansWithAnnotation.values()).stream()
                .forEach(item -> {
                    Class<?> aClass = item.getClass();
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (Class<?> inter : interfaces) {
                        String interfaceName = inter.getName();
                        log.info("加载服务类: {}", interfaceName);
                        serviceMap.put(interfaceName, item);
                    }
                });
        log.info("已加载全部服务接口:{}", serviceMap);
    }
}
