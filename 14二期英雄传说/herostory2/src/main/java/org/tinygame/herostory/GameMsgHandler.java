package org.tinygame.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的消息处理器
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 客户端信道数组, 一定要使用 static, 否则无法实现群发
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 用户字典
     */
    static private final Map<Integer, User> _userMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        _channelGroup.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        _channelGroup.remove(ctx.channel());

        // 先拿到用户 Id
        Integer userId = (Integer)ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (null == userId) {
            return;
        }

        _userMap.remove(userId);

        GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        resultBuilder.setQuitUserId(userId);

        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
        _channelGroup.writeAndFlush(newResult);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到客户端消息, msgClazz = " + msg.getClass().getName() + ", msg = " + msg);

        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            // 从指令对象中获取用户 Id 和英雄形象
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            int userId = cmd.getUserId();
            String heroAvatar = cmd.getHeroAvatar();

            GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
            resultBuilder.setUserId(userId);
            resultBuilder.setHeroAvatar(heroAvatar);

            // 将用户加入字典
            User newUser = new User();
            newUser.userId = userId;
            newUser.heroAvatar = heroAvatar;
            _userMap.put(newUser.userId, newUser);

            // 将用户 Id 附着到 Channel
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

            // 构建结果并发送
            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
            _channelGroup.writeAndFlush(newResult);
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

            for (User currUser : _userMap.values()) {
                if (null == currUser) {
                    continue;
                }

                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfoBuilder.setUserId(currUser.userId);
                userInfoBuilder.setHeroAvatar(currUser.heroAvatar);
                resultBuilder.addUserInfo(userInfoBuilder);
            }

            GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            Integer userId = (Integer)ctx.channel().attr(AttributeKey.valueOf("userId")).get();

            if (null == userId) {
                return;
            }

            GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd)msg;

            GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
            resultBuilder.setMoveUserId(userId);
            resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
            resultBuilder.setMoveToPosY(cmd.getMoveToPosY());

            GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
            _channelGroup.writeAndFlush(newResult);
        }
    }
}
