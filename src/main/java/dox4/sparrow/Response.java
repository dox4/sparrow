package dox4.sparrow;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author chengzj
 * @date 2020/3/12
 * @description HTTP response class
 */
public class Response {
    private Request request;
    private OutputStream output;
    final static String NOT_FOUND = "404.html";
    final static int BUFFER_SIZE = 1024;

    Response(Request request, OutputStream output) {
        this.request = request;
        this.output = output;
    }

    public void send() {
        String uri = request.getUri();
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        String msg;
        InputStream is = getClass().getClassLoader().getResourceAsStream(uri);
        if (is == null) {
            is = getClass().getClassLoader().getResourceAsStream(NOT_FOUND);
            msg = "HTTP1.1 404 File Not Found\r\n";
        } else {
            msg = "HTTP/1.1 200 OK\r\n";
        }

        msg += "Content-Type: text/html\r\n" +
                "Content-Length: ";
        int length;

        StringBuilder sb = new StringBuilder();
        try {
            assert is != null;
            while ((length = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, length));
            }
            String content = sb.toString();
            byte[] bytes = content.getBytes();
            msg += bytes.length + "\r\n\r\n";
            msg += content;
            output.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
