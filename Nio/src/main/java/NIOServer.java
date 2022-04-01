import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @auther wy
 * @create 2022/3/31 14:09
 */
@Slf4j
public class NIOServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        //创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);       //设置非阻塞

        //注册到selector
        SelectionKey sscKey = ssc.register(selector, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);  //关注accept事件

        //2.监听端口
        ssc.bind(new InetSocketAddress(8080));

        while (true){
            //监听事件是否发生，没有事件阻塞
            selector.select();
            //发生事件，获取发生事件的集合
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while(iter.hasNext()){
                SelectionKey key = iter.next();
                log.debug("key:{}", key );
                //区分事件类型
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("accept {}",sc);

                    //处理完成一定要删除集合元素
                    iter.remove();
                }else if (key.isReadable()){
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);
                        //断开会产生一次读事件，返回值是-1
                        if (read == -1) {
                            key.cancel();
                        }else{
                            buffer.flip();
                            ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity()*2);
                            newBuffer.put(buffer);
                            key.attach(newBuffer);
                            log.debug("read {}", Charset.defaultCharset().decode(buffer));
                        }

                        iter.remove();
                    }catch (IOException io){
                        //客户端异常断开，触发异常处理
                        key.cancel();
                    }
                }


            }
        }
    }
}
