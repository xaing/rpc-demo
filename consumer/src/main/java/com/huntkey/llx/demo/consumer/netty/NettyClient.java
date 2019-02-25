package com.huntkey.llx.demo.consumer.netty;

import com.alibaba.fastjson.JSONArray;
import com.huntkey.llx.demo.consumer.config.ConnectManage;
import com.huntkey.llx.demo.core.constant.Constants;
import com.huntkey.llx.demo.core.entity.Request;
import com.huntkey.llx.demo.core.entity.Response;
import com.huntkey.llx.demo.core.tool.JsonDecoder;
import com.huntkey.llx.demo.core.tool.JsonEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.SocketAddress;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by lulx on 2019/2/23 0023 下午 14:54
 */
@Component
public class NettyClient {

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private EventLoopGroup group = new NioEventLoopGroup(1);
    private Bootstrap bootstrap = new Bootstrap();

    @Autowired
    NettyClientHandler clientHandler;

    @Autowired
    ConnectManage connectManage;

    public NettyClient() {
        bootstrap.group(group).
                channel(NioSocketChannel.class).
                option(ChannelOption.TCP_NODELAY, true).
                option(ChannelOption.SO_KEEPALIVE, true).
                handler(new ChannelInitializer<SocketChannel>() {
                    //创建NIOSocketChannel成功后，在进行初始化时，将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0
                                , 0, 30));
                        pipeline.addLast(new JsonEncoder());
                        pipeline.addLast(new JsonDecoder());
                        pipeline.addLast("handler", clientHandler);
                    }
                });
    }

    @PreDestroy
    public void destroy() {
        log.info("RPC客户端退出,释放资源!");
        group.shutdownGracefully();
    }


    public Object send(Request request) throws InterruptedException {
        Channel channel = connectManage.chooseChannel();
        if (channel != null && channel.isActive()) {
            SynchronousQueue<Object> queue = clientHandler.sendRequest(request, channel);
            Object result = queue.take();
            return JSONArray.toJSONString(result);
        } else {
            Response res = new Response();
            res.setCode(Constants.RESPONSE_ERROR_CODE);
            res.setError_msg("未正确连接到服务器.请检查相关配置信息!");
            return JSONArray.toJSONString(res);
        }
    }

    public Channel doConnect(SocketAddress address) throws InterruptedException {
        ChannelFuture future = bootstrap.connect(address);
        Channel channel = future.sync().channel();
        return channel;
    }
}
