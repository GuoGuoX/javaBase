package NIO;

import java.lang.String;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * 当调用register将通道注册选择器时,
 * 选择器对通道的监听事件,需要通过第二个参数ops指定。
 * <p>
 * SelectionKey可以监听的事件类型,(可使用SelectionKey的四个常量表示):
 * |--读:SelectionKey.OP_READ   (1)
 * |--写:SelectionKey.OP_WRITE  （4）
 * |--连接:SelectionKey.OP_CONNECT  (8)
 * |--接收:SelectionKey.OP_ACCEPT （16）
 * <p>
 * 若注册时不止监听一种状态，可使用“位或”操作符连接
 * 例：int integer = SelectionKey.OP_READ|SelectionKey.OP_WRITE
 */
public class TestNonBlockingNIO {

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
        SocketChannel socket = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        //切换非阻塞连接
        socket.configureBlocking(false);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String next = scanner.next();
            LocalDateTime now = LocalDateTime.now();//jdk1.8引入的工具类
            byteBuffer.put((now.toString() + "\n" + next).getBytes());
            byteBuffer.flip();
            socket.write(byteBuffer);
            byteBuffer.clear();
        }
        System.out.println("客户端发送出去了！！" + new String(byteBuffer.array(), 0, byteBuffer.limit()));

        socket.close();
    }

    @Test
    public void server() {
        try {
            //获取socket服务端通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //切换非阻塞连接
            serverSocketChannel.configureBlocking(false);

            //绑定连接
            serverSocketChannel.bind(new InetSocketAddress(9898));

            //获取选择器，用于监听客户端状态
            Selector selector = Selector.open();
            //服务端注册监听那些事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            //轮询选择器上已经"准备就绪"的事件
            while (selector.select() > 0) {
                //当准备就绪时就开始判断客户端状态，否则继续while轮询直到有准备就绪的客户端发起连接
                //获取当前选择器中所有已经注册的“选择器”的已就绪的监听事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    //判断当前的selectorKey具体是什么事件准备就绪
                    //如果是接收已就绪，则进入
                    if (next.isAcceptable()) {

                        //若接收已就绪则获取socketChannel,获取客户端连接
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);//切换到非阻塞式

                        //将该通道注册到选择器上
                        socketChannel.register(selector, SelectionKey.OP_READ);

                    } else if (next.isReadable()) {
                        //如果是可读事件准备就绪则进入
                        //当时可读事件时获取客户端连接
                        SocketChannel channel = (SocketChannel) next.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(100);

                        Integer leng = null;
                        while ((leng = channel.read(byteBuffer)) > 0) {
                            System.out.print(leng);
                            byteBuffer.flip();//切换为可读
                            System.out.println("socketServer端接收成功：" + new String(byteBuffer.array(), 0, leng));
                            byteBuffer.clear();
                        }

                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

