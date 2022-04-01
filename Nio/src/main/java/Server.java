

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;



/**
 * @auther wy
 * @create 2022/3/31 11:16
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);       //设置非阻塞
        //2.监听端口
        ssc.bind(new InetSocketAddress(8080));
        List<SocketChannel> channels = new ArrayList<>();

        while (true){
            //建立与客户端连接
//            log.debug("connecting...");
            SocketChannel sc = ssc.accept();
            if(sc!=null){
                log.debug("connected... {}", sc);
                sc.configureBlocking(false);
                channels.add(sc);

            }
            for(SocketChannel channel:channels){

//                log.debug("before read.... {}", sc);
                int read = channel.read(buffer);
                if(read>0){
                    buffer.flip();
                    String str = Charset.defaultCharset().decode(buffer).toString();
                    log.debug("{} {}",str,sc);
                    buffer.clear();
                }
//                log.debug("after read.... {}", sc);
            }
        }
    }
}
