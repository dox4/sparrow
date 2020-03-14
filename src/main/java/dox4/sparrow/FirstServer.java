package dox4.sparrow;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author dox
 */
public class FirstServer {
    public static void main(String[] args) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
            while (true) {
                Socket socket = server.accept();
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                SimpleRequest request = new SimpleRequest(input);
                SimpleResponse response = new SimpleResponse(output);
                switch (request.getRequestURI()) {
                    case "/A1":
                        new A1Servlet().service(request, response);
                        break;
                    case "/A2":
                        new A2Servlet().service(request, response);
                        break;
                    default:
                        output.write("Your request path is no servlet configured.".getBytes());
                        break;
                }
                output.flush();
                socket.close();
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
