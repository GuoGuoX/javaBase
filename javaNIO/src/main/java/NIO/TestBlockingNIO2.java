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

public class TestBlockingNIO2 {

    @Test
    public void client() throws IOException {
        SocketChannel socket = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        FileChannel fileChannel = FileChannel.open(Paths.get("QQ20200227114643.png"), StandardOpenOption.READ);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (fileChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            socket.write(byteBuffer);
            byteBuffer.clear();
        }
        //如果不加这句就会造成客户端阻塞,因为接收端返回了数据，SocketChannel管道中始终有数据.
        socket.shutdownOutput();//告诉服务接收端数据传输完毕

        int leng = 0;
        while ((leng = socket.read(byteBuffer)) != -1) {
            byteBuffer.flip();
            System.out.println(new String(byteBuffer.array(), 0, leng));
            byteBuffer.clear();
        }


        fileChannel.close();
        socket.close();
    }

    @Test
    public void server() throws IOException {
        ServerSocketChannel open = ServerSocketChannel.open();
        FileChannel fileChannel = FileChannel.open(Paths.get("谁扔的炮仗3.png"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        open.bind(new InetSocketAddress(9898));
        SocketChannel accept = open.accept();

        ByteBuffer allocate = ByteBuffer.allocate(1024);

        while (accept.read(allocate) != -1) {
            allocate.flip();
            fileChannel.write(allocate);
            allocate.clear();
        }
        //发送给客户端
        allocate.put("数据接收完成".getBytes());
        allocate.flip();
        accept.write(allocate);
        System.out.println("server端数据接收完成");
        fileChannel.close();
        accept.close();
        open.close();


    }
}
