package NIO;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * DatagramChannel
 */
public class TestNonBlockingNIO2 {

    @Test
    public void test01() {
        //IDEA无法使用System.in输入
        //首先，打开IDEA安装根目录下的bin文件夹，找到idea.exe.vmoptions和idea64.exe.vmoptions这两个文件
        //对这两个文件进行编辑，在后面添加一段参数-Deditable.java.test.console=true
        Scanner scanner = new Scanner(System.in);
        String next = scanner.next();
        System.out.println(next);
           /* LocalDateTime now = LocalDateTime.now();//jdk1.8引入的工具类
            System.out.println((now.toString()+"\n"+next).getBytes());*/
    }

    @Test
    public void client() throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String next = scanner.next();
            byteBuffer.put((new Date().toString() + "\n" + next).getBytes());
            byteBuffer.flip();
            channel.send(byteBuffer, new InetSocketAddress("127.0.0.1", 9898));
            byteBuffer.clear();
        }
        channel.close();
    }

    @Test
    public void receive() throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);//切换非阻塞
        datagramChannel.bind(new InetSocketAddress(9898));

        //获取选择器
        Selector selector = Selector.open();
        //selector中注册就绪事件监听
        datagramChannel.register(selector, SelectionKey.OP_READ);

        while (selector.select() > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                if (next.isReadable()) {
                    datagramChannel.receive(byteBuffer);
                    byteBuffer.flip();//切换到读取状态
                    System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit()));
                    byteBuffer.clear();
                }
            }
            iterator.remove();
        }
        datagramChannel.close();
    }
}

