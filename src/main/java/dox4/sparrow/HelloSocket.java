package dox4.sparrow;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author dox4
 * @date 2020/3/12
 * @description a single loop server which can only deal with ono request at one time.
 */
public class HelloSocket {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
            while (true) {
                Socket socket = server.accept();
                InputStream input = socket.getInputStream();
                Request request = new Request();
                request.parse(input);
                System.out.println("// === HTTP MESSAGE === ##");
                System.out.println(request.getRawRequest());
                System.out.println("## === HTTP MESSAGE === //");
                System.out.println(request.toString());
                Response response = new Response(request, socket.getOutputStream());
                response.send();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
