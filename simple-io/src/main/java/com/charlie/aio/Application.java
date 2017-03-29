package com.charlie.aio;

import com.charlie.aio.client.AioClient;
import com.charlie.aio.server.AioServer;

import java.util.Scanner;

/**
 * Created by dhy on 17-3-29.
 *
 */
public class Application {
    public static void main(String[] args) throws Exception {
        // 运行服务器
        AioServer.start();
        // 避免客户端先于服务器执行
        Thread.sleep(100);
        // 运行客户端
        AioClient.start();
        System.out.println("请输入请求信息：");
        while(AioClient.sendMsg(new Scanner(System.in).nextLine())) {
            // ignore
        }
        System.exit(1);
    }
}
