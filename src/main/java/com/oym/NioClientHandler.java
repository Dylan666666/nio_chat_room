package com.oym;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 客户端线程类，专门接收服务器端响应信息
 * @Author: Mr_OO
 * @Date: 2020/5/2 21:27
 */
public class NioClientHandler implements Runnable {
    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            for (;;) {
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    continue;
                }
                //获取可用channel的集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    //selectionKey实例
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    //移除Set中的当前selectionKey
                    iterator.remove();

                    //根据就绪状态，调用对应方法处理逻辑
                    //如果是可读事件
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
            
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector)
            throws IOException {
        //要从 selectionKey中获取已经就绪的channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建 buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //循环读取服务器响应信息
        String response = "";
        while (socketChannel.read(byteBuffer) > 0) {
            //切换buffer为读模式
            byteBuffer.flip();
            //读取Buffer中的内容
            response += Charset.forName("UTF-8").decode(byteBuffer);
        }
        //将 channel再次注册到Selector上，监听他的可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //将服务器响应的信息打印给本地
        if (response.length() > 0) {
            System.out.println("==>>" + response);
        }
    }
}
