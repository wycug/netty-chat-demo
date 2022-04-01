package cn.itcast.server.handler;

import cn.itcast.message.GroupChatRequestMessage;
import cn.itcast.message.GroupChatResponseMessage;
import cn.itcast.server.session.GroupSessionFactory;
import cn.itcast.server.session.SessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * @auther wy
 * @create 2022/4/1 15:59
 */
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        try {
            Set<String> members = GroupSessionFactory.getGroupSession().getMembers(groupName);
            String content = msg.getContent();
            Iterator<String> iterator = members.iterator();
            while (iterator.hasNext()){
                SessionFactory.getSession().getChannel(iterator.next()).writeAndFlush(new GroupChatResponseMessage(true, content));
            }
            ctx.writeAndFlush(new GroupChatResponseMessage(true, "发生成功"));

        }catch (Exception e){
            ctx.writeAndFlush(new GroupChatResponseMessage(false, groupName+"讨论组不存在"));
        }

    }
}
