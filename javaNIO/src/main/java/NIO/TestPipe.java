package NIO;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.util.Date;

/**
 * Pipe工具类能实现线程与线程之间的数据传输
 */
public class TestPipe {

    @Test
    public void test01() throws IOException {
        Pipe open = Pipe.open();
        Pipe.SinkChannel sinkChannel = open.sink();
        sinkChannel.configureBlocking(false);//切换到非阻塞模式
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put((new Date().toString() + "hello").getBytes());
        byteBuffer.flip();//切换到读取模式
        sinkChannel.write(byteBuffer);


        Pipe.SourceChannel source = open.source();
        byteBuffer.flip();
        int read = source.read(byteBuffer);
        System.out.println(new java.lang.String(byteBuffer.array(), 0, read));
        byteBuffer.clear();
        source.close();
        sinkChannel.close();
    }
}
