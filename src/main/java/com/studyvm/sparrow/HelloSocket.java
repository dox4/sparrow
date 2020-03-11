package com.studyvm.sparrow;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * this is a server if it COULD BE that can only accept one time request
 * and print the request as raw string.
 * only proved that that the HTTP message is based on clear text.
 * and you get the request, you can do everything.
 */
public class HelloSocket {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
            Socket socket = server.accept();
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            byte[] bytes = new byte[1024];
            int length = input.read(bytes);
            System.out.println("accept length: " + length);
            char[] chars = new char[length + 1];
            for (int i = 0; i < length; i++) {
                chars[i] = (char) bytes[i];
            }
            System.out.println("// === HTTP MESSAGE === ##");
            System.out.println(String.valueOf(chars));
            System.out.println("## === HTTP MESSAGE === //");
            output.write(0x65);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
