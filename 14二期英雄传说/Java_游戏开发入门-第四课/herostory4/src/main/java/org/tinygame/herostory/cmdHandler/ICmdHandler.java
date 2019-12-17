package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * 指令处理器接口
 *
 * @param <TCmd>
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    /**
     * 处理指令
     *
     * @param ctx 客户端信道处理器上下文
     * @param cmd 指令
     */
    void handle(ChannelHandlerContext ctx, TCmd cmd);
}
