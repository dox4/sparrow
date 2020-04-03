package dox4.sparrow.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @date 2020/4/2
 * @description 连接器模块 - 连接器
 */
public class HttpConnector implements Runnable {
    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    boolean stopped = false;

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        int port = 10219;

        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (!stopped) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            HttpProcessor processor = new HttpProcessor(this);
            processor.process(socket);
        }

    }
}
