package NIO;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * 一,通道(Channel):用于源节点与目标节点的连接,在JAVA NIO中负责缓冲区中数据的传输.
 * Channel本身不存储数据,因此需要配合缓冲区进行传输.
 * <p>
 * 二,通道的主要实现类
 * java.nio.channels.Channel接口(java为Channel接口提供额主要实现类如下)
 * |--FileChannel:用于读取写入映射和操作文件的通道 (用于本地文件)
 * |--DatagramChannel:通过UDP读写网络中的数据通道  (用于网络)
 * |--SocketChannel:通过TCP读写网络中的数据.       (用于网络)
 * |--ServerSocketChannel:可以监听新进来的TCP连接, (用于网络)
 * 对每一个新进来的TCP连接都会会创建一个ServerSocketChannel
 * <p>
 * 三,获取通道
 * 1,java针对支持通道的类提供了getChannel()方法,
 * <本地IO操作>
 * FileInputStream/FileOutputStream
 * RandomAccessFile
 * <网络IO操作>
 * Socket
 * SeverSocket
 * DatagramSocket
 * 2，在JDK1.7中的NIO.2针对各个通道提供了静态方法open();
 * 3，在JDK1.7中的NIO.2的Files工具类的newByteChannel();
 * <p>
 * 四,通道之间的传输
 * transferFrom()
 * transferTo()
 * <p>
 * 五,分散(Scatter)与聚集(Gather)
 * 分散读取(Scattering Reads):将通道中的数分散到多个缓冲区中
 * 聚集写入(Gathering Writes):将多个缓冲区中的数据聚集到通道中
 * <p>
 * 六,字符集
 * 解码:字节数组-->字符串
 * 编码:字符串-->字节数组
 */
//利用通道完成文件的复制
public class TestChannel {

    @Test
    public void test06() {
        Charset gbk1 = Charset.forName("GBK");//创建指定编码的字符集
        Charset gbk2 = Charset.forName("GBK");//创建指定编码的字符集
        ByteBuffer wang = gbk1.encode("王老吉");
        CharBuffer decode = gbk2.decode(wang);

        java.lang.String s = new java.lang.String(wang.array());
        char[] array = decode.array();
        System.out.println(s);//���ϼ�   使用GBK的编码,用UTF-8解码
        System.out.println(array);//当对同一条数据使用同一个编码器与解码器就不会产生乱码

    }

    //字符集
    @Test
    public void test05() {
        Charset charset = Charset.defaultCharset();
        System.out.println(charset);//当前使用的字符集UTF-8
        SortedMap<String, Charset> stringCharsetSortedMap = Charset.availableCharsets();//获取JDK支持的字符集
        Set<Map.Entry<String, Charset>> entries = stringCharsetSortedMap.entrySet();
        for (Map.Entry<String, Charset> map : entries) {
            System.out.println(map.getKey() + "=" + map.getValue());
        }
    }

    //分散与聚集
    @Test
    public void test04() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("solrhome.solr.xml集群配置文件 - 副本.txt", "rw");
        RandomAccessFile randomAccessFileOut = new RandomAccessFile("solrhome配置文件 - 副本.txt", "rw");

        FileChannel channel = randomAccessFile.getChannel();
        FileChannel channel1 = randomAccessFileOut.getChannel();

        ByteBuffer byteBuffer1 = ByteBuffer.allocate(10);
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(100);
        ByteBuffer[] byteBuffers = {byteBuffer1, byteBuffer2};
        //分散读取
       /*channel.read(byteBuffers);
        for(ByteBuffer byteBuffer : byteBuffers){
            byteBuffer.flip();
        }
        //聚集写入
       channel1.write(byteBuffers);
*/
        while (channel.read(byteBuffers) != -1) {
            for (ByteBuffer byteBuffer : byteBuffers) {
                byteBuffer.flip();
            }
            channel1.write(byteBuffers);
            for (ByteBuffer byteBuffer : byteBuffers) {
                byteBuffer.clear();
            }

        }
        System.out.println(new java.lang.String(byteBuffers[0].array(), 0, byteBuffers[0].limit()));
        System.out.println("-----------------");
        System.out.println(new java.lang.String(byteBuffers[1].array(), 0, byteBuffers[1].limit()));


        channel.close();
        channel1.close();

    }


    //通道之间的数传输
    @Test
    public void test03() throws IOException {
        FileChannel in = FileChannel.open(Paths.get("QQ20200227114643.png"), StandardOpenOption.READ);
        FileChannel out = FileChannel.open(Paths.get("谁扔的炮仗1.png"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
        //in.transferTo(0,in.size(),out);
        out.transferFrom(in, 0, in.size());
        in.close();
        out.close();
    }


    //使用直接缓冲区MappedByteBuffer与ByteBuffer..allocateDirect();相同都是直接缓冲区
    //内存映射文件MappedByteBuffer
    @Test
    public void test02() throws IOException {
        FileChannel in = FileChannel.open(Paths.get("QQ20200227114643.png"), StandardOpenOption.READ);
        FileChannel out = FileChannel.open(Paths.get("谁扔的炮仗.png"), StandardOpenOption.READ, StandardOpenOption.WRITE);
        byte[] bytes = new byte[1024];
        MappedByteBuffer mapByteBuffer_in = in.map(FileChannel.MapMode.READ_ONLY, 0, in.size());
        MappedByteBuffer mapByteBuffer_out = out.map(FileChannel.MapMode.READ_WRITE, 0, in.size());

        mapByteBuffer_in.get(bytes);
        mapByteBuffer_out.put(bytes);

        in.close();
        out.close();


    }

    //非直接缓冲区
    @Test
    public void test01() throws IOException {
        System.out.println(new File(".").getAbsolutePath());
        FileInputStream fileInputStream = new FileInputStream("QQ20200227114643.png");
        FileOutputStream fileOutputStream = new FileOutputStream("谁扔的炮仗.png");

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        FileChannel channel1 = fileInputStream.getChannel();
        FileChannel channel2 = fileOutputStream.getChannel();
        while (channel1.read(byteBuffer) != -1) {
            byteBuffer.flip();//切换读取模式
            channel2.write(byteBuffer);
            byteBuffer.clear();
        }
        channel2.close();
        channel1.close();
        fileInputStream.close();
        fileOutputStream.close();

    }
}
