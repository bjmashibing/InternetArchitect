package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 广播员
 */
public final class Broadcaster {
    /**
     * 客户端信道数组, 一定要使用 static, 否则无法实现群发
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化类默认构造器
     */
    private Broadcaster() {
    }

    /**
     * 添加信道
     *
     * @param newChannel 客户端信道
     */
    static public void addChannel(Channel newChannel) {
        if (null != newChannel) {
            _channelGroup.add(newChannel);
        }
    }

    /**
     * 移除信道
     *
     * @param targetChannel 客户端信道
     */
    static public void removeChannel(Channel targetChannel) {
        if (null != targetChannel) {
            _channelGroup.remove(targetChannel);
        }
    }

    /**
     * 关播消息
     *
     * @param msg 消息对象
     */
    static public void broadcast(Object msg) {
        if (null == msg) {
            return;
        }

        _channelGroup.writeAndFlush(msg);
    }
}
