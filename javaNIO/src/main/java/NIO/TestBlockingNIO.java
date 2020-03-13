package NIO;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一,使用NIO完成网络通信的三个核心
 * <p>
 * 1,通道(Channel):负责连接
 * java.nio.channels.Channel接口
 * |--SelectableChannel
 * |--SocketChannel
 * |--ServerSocketChannel
 * |--DatagramChannel
 * <p>
 * |--Pipe.sinkChannel
 * |--Pipe.SourceChannel
 * <p>
 * 2,缓冲区buffer:负责数据的存取
 * <p>
 * 3,选择器selector:是selectableChannel的多路复用器.用于监控SelectableChannel的IO状况
 */

//阻塞式
public class TestBlockingNIO {
    //客户端
    @Test
    public void client() throws IOException {
        //获取通道
        SocketChannel socket = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        //获取本地IO通道
        FileChannel open1 = FileChannel.open(Paths.get("QQ20200227114643.png"), StandardOpenOption.READ);

        ByteBuffer allocate = ByteBuffer.allocate(1024);
        while (open1.read(allocate) != -1) {
            allocate.flip();//将缓冲区切换为读取状态
            socket.write(allocate);
            allocate.clear();
        }
        open1.close();
        socket.close();
    }

    @Test
    public void server() throws IOException {
        //获取通道
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        FileChannel file = FileChannel.open(Paths.get("谁扔的炮仗2.png"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        //绑定连接
        serverSocket.bind(new InetSocketAddress(9898));
        //获取客户连接的通道
        SocketChannel socket = serverSocket.accept();

        ByteBuffer allocate = ByteBuffer.allocate(1024);
        while (socket.read(allocate) != -1) {
            allocate.flip();
            file.write(allocate);
            allocate.clear();
        }
        file.close();
        socket.close();
        serverSocket.close();

    }
}
