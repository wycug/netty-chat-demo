import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @auther wy
 * @create 2022/3/30 21:47
 */

public class HelloServer {
    public static void main(String[] args) {
        //1.启动器 负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2.boosEventLoop ,WorkerEventLoop
                .group(new NioEventLoopGroup())
                //3.选择服务器的ServerSocketChannel 实现
                .channel(NioServerSocketChannel.class)
                //4.boss负责连接work（child）负责读写
                .childHandler(
                    //5.channel 代表和客户端进行读写通道的初始化，负责添加别的handler
                    new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            //6.添加具体的handler
                            ch.pipeline().addLast(new StringDecoder());//将bytebuf转换成字符串

                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){   //自定义handler
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println("read");
                                    System.out.println(msg);
                                }
                            });


                        }
                })
                //绑定端口
                .bind(8081);
    }
}
