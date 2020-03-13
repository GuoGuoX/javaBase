package NIO;

import org.junit.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * 一,缓冲区在Java NIO中负责数据的存取,缓冲区就是数组.用于存储不同数据类型的数据
 * <p>
 * 根据数据类型不同(boolean除外),提供了相应类型的缓冲区
 * ByteBuffer
 * CharBuffer
 * IntBuffer
 * LongBuffer
 * ShortBuffer
 * FloatBuffer
 * DoubleBuffer
 * 上述缓冲区的管理方式几乎一致.
 * <p>
 * 二,缓冲区存取数据的两个核心
 * put():存入数据到缓冲区中
 * get();获取缓冲区中的数据
 * <p>
 * <p>
 * 三,缓冲区的四个核心属性：
 * capacity：容量,表示缓冲区中最大存储数据的容量。一旦声明不能改变。
 * limit：界限，表示缓冲区可以操作数据的大小。（limit后数据不能读写）
 * position：位置，表示缓冲器中正在操作数据的位置
 * <p>
 * make:可以标记position的位置,可以通过reset重置position位置
 * 0 <= reset <= make <= position <= limit <= capacity
 * <p>
 * 五,直接缓冲区与非直接缓冲区
 * 非直接缓冲区:通过allocate()方法分配缓冲区,将缓冲区建立在JVM的内存中
 * 直接缓冲区:通过allocateDirect()方法分配直接缓冲区,将缓冲区建立在物理内存中.提高了效率
 * 注意:非直接缓冲区需要程序<-->JVM内存<-->操作系统内存<-->磁盘文件中间进行传输,相比直接缓冲效率低
 * 直接缓冲区是直接建立在物理内存中,传输效率高,
 * 但是直接缓冲区的特点也很明显,在数据被存到缓冲区后就不归JVM管理,这会造成系统内存溢出.
 */
public class TestBuffer {

    @Test
    public void test04() {
        //获取直接缓冲区
        /*ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        System.out.println(buffer.isDirect());//判断是否是直接缓冲区 true*/
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        System.out.println(buffer.isDirect());//判断是否是直接缓冲区 false
    }

    @Test
    public void test03() {
        String str = "abcdef";
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(str.getBytes());

        buffer.flip();//切换读取模式

        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes, 0, 2);
        System.out.println(new String(bytes, 0, bytes.length));
        System.out.println("buffer的当前可操作的起始位置" + buffer.position());//2

        buffer.mark();//在执行下一次读取操作造成position变化前,进行标记.此时应该为2

        buffer.get(bytes, 2, 2);
        System.out.println(new String(bytes, 0, bytes.length));
        System.out.println("buffer的当前可操作的起始位置" + buffer.position());//4

        buffer.reset();//重置position位置到mark的时候
        System.out.println("buffer的当前可操作的起始位置" + buffer.position());//4
        /**
         * buffer的当前可操作的起始位置4
         * buffer的当前可操作的起始位置2
         */

    }


    @Test
    public void test01() {
        Buffer buffer = ByteBuffer.allocate(1024);//指定缓冲区分配多大的空间
        /**
         * 缓冲区起始位置0
         * 缓冲区界限1024
         * 缓冲器大小1024
         */
        System.out.println("缓冲区起始位置" + buffer.position());//缓冲区起始位置0
        System.out.println("缓冲区界限" + buffer.limit());//缓冲区界限1024
        System.out.println("缓冲区大小" + buffer.capacity());//缓冲区大小1024
    }

    @Test
    public void test02() {
        String str = "abcde";
        //1分配一个缓冲区的大小
        ByteBuffer buffer = ByteBuffer.allocate(1024);//指定缓冲区分配多大的空间
        /**
         * 缓冲区起始位置0
         * 缓冲区界限1024
         * 缓冲器大小1024
         */
        System.out.println("-----------allocate----------");
        System.out.println("缓冲区起始位置" + buffer.position());//缓冲区起始位置0
        System.out.println("缓冲区界限" + buffer.limit());//缓冲区界限1024
        System.out.println("缓冲区大小" + buffer.capacity());//缓冲区大小1024

        //2利用put()存储数据到缓冲区
        buffer.put(str.getBytes());
        System.out.println("-----------put----------");
        System.out.println("缓冲区起始位置" + buffer.position());//缓冲区起始位置5
        System.out.println("缓冲区界限" + buffer.limit());//缓冲区界限1024
        System.out.println("缓冲区大小" + buffer.capacity());//缓冲区大小1024

        //切换读取数据模式
        buffer.flip();
        System.out.println("-----------flip----------");
        System.out.println("缓冲区起始位置" + buffer.position());//缓冲区起始位置0
        System.out.println("缓冲区界限" + buffer.limit());//缓冲区界限1024
        System.out.println("缓冲区大小" + buffer.capacity());//缓冲区大小1024

        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        System.out.println("-----------get----------");
        System.out.println("缓冲区起始位置" + buffer.position());//缓冲区起始位置5
        System.out.println("缓冲区界限" + buffer.limit());//缓冲区界限1024
        System.out.println("缓冲区大小" + buffer.capacity());//缓冲区大小1024
        System.out.println(new String(bytes, 0, bytes.length));//abcde

        //rewind :可重复读
        buffer.rewind();
        System.out.println("-----------rewind----------");
        System.out.println("缓冲区起始位置" + buffer.position());//缓冲区起始位置0 调用rewind()后改为了O
        System.out.println("缓冲区界限" + buffer.limit());//缓冲区界限1024
        System.out.println("缓冲区大小" + buffer.capacity());//缓冲区大小1024
        System.out.println(new String(bytes, 0, bytes.length));//abcde


        buffer.clear();//清空缓冲区,但是缓冲区中的数据依然存在,只是被遗忘了
        System.out.println("-----------clear----------");
        System.out.println("缓冲区起始位置" + buffer.position());//缓冲区起始位置0 调用rewind()后改为了O
        System.out.println("缓冲区界限" + buffer.limit());//缓冲区界限1024
        System.out.println("缓冲区大小" + buffer.capacity());//缓冲区大小1024
        System.out.println(new String(bytes, 0, bytes.length));//abcde
    }
}
