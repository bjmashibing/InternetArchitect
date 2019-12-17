package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息编码器
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == msg ||
            !(msg instanceof GeneratedMessageV3)) {
            super.write(ctx, msg, promise);
            return;
        }

        // 获取消息类
        Class<?> msgClazz = msg.getClass();

        // 获取消息编码
        int msgCode = GameMsgRecognizer.getMsgCodeByMsgClazz(msgClazz);
        if (msgCode <= -1) {
            LOGGER.error(
                "无法识别的消息, msgClazz = {}",
                msgClazz.getName()
            );
            return;
        }

        // 获取消息体字节数组
        byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeShort((short) 0); // 写出消息长度, 目前写出 0 只是为了占位
        byteBuf.writeShort((short) msgCode); // 写出消息编号
        byteBuf.writeBytes(msgBody); // 写出消息体

        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        super.write(ctx, frame, promise);
    }
}
