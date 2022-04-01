package cn.itcast.server.handler;

import cn.itcast.message.GroupChatResponseMessage;
import cn.itcast.message.GroupCreateRequestMessage;
import cn.itcast.message.GroupCreateResponseMessage;
import cn.itcast.server.session.Group;
import cn.itcast.server.session.GroupSessionFactory;
import cn.itcast.server.session.SessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

/**
 * @auther wy
 * @create 2022/4/1 16:00
 */
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {

        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        Group group = GroupSessionFactory.getGroupSession().createGroup(groupName, members);
        if(group.getMembers().equals(members)){

            for (String member : GroupSessionFactory.getGroupSession().getMembers(groupName)) {
                SessionFactory.getSession().getChannel(member).writeAndFlush(new GroupCreateResponseMessage(true, "您被拉入讨论组："+groupName));
            }
            ctx.channel().writeAndFlush(new GroupCreateResponseMessage(true, groupName+"讨论组创建成功"));
        }else {
            ctx.channel().writeAndFlush(new GroupCreateResponseMessage(false, groupName+"讨论组已存在"));
        }

    }
}
