package com.huntkey.llx.demo.provider.netty;

import com.alibaba.fastjson.JSON;
import com.huntkey.llx.demo.core.constant.Constants;
import com.huntkey.llx.demo.core.entity.Request;
import com.huntkey.llx.demo.core.entity.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by lulx on 2019/2/23 0023 上午 10:55
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private final Map<String, Object> serviceMap;

    public NettyServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("客户端连接成功! {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("客户端断开连接! {}", ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Request request = JSON.parseObject(msg.toString(), Request.class);
        if ("heartBeat".equalsIgnoreCase(request.getMethodName())) {
            logger.info("客户端心跳信息: {}", ctx.channel().remoteAddress());
        } else {
            logger.info("RPC客户端请求接口 : {};  方法名: {} "
                    , request.getClassName()
                    , request.getMethodName());
            Response response = new Response();
            response.setRequestId(request.getId());
            try {
                Object result = this.handler(request);
                response.setData(result);
            } catch (Throwable e) {
                response.setCode(Constants.RESPONSE_ERROR_CODE);
                response.setError_msg(e.toString());
                logger.error("RPC Server handle request error", e);
            }
            ctx.writeAndFlush(response);
        }
    }

    /**
     * 通过反射执行本地方法
     *
     * @param request
     * @return
     */
    private Object handler(Request request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = serviceMap.get(className);
        if (null == serviceBean) {
            throw new RuntimeException("未找到服务接口，请检查配置 className : "
                    + className + "methodName : " + request.getMethodName());
        }
        Class<?> aClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();
        Class<?>[] parameterTypes = request.getParameterTypes();

        Method method = aClass.getMethod(methodName, parameterTypes);
        method.setAccessible(Boolean.TRUE);
        return method.invoke(serviceBean, getParameters(parameterTypes, parameters));
    }

    /**
     * 获取参数列表
     *
     * @param parameterTypes
     * @param parameters
     * @return
     */
    private Object[] getParameters(Class<?>[] parameterTypes, Object[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return parameters;
        }
        Object[] newParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            newParameters[i] = JSON.parseObject(JSON.toJSONString(parameters[i]), parameterTypes[i]);
        }
        return newParameters;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                logger.info("客户端已超过60秒未读写数据,关闭连接.{}", ctx.channel().remoteAddress());
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info(cause.getMessage());
        ctx.close();
    }
}
