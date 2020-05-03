package com.oym;

import java.io.IOException;

/**
 * @Author: Mr_OO
 * @Date: 2020/5/2 23:08
 */
public class BClient {
    public static void main(String[] args) throws IOException {
        NioClient nioClient = new NioClient();
        nioClient.start("小黑");
    }
}
