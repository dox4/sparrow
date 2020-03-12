package dox4.sparrow;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dox4
 * @date 2020/3/12
 * @description HTTP request class
 */
public class Request {
    static final int BUFFER_SIZE = 1024;
    static final char SPACE = ' ';

    private String method;
    private String uri;
    private String version;
    private Map<String, String> headers;
    private String rawRequest;

    public Request() {
        headers = new HashMap<>();
    }

    public void parse(InputStream is) {
        byte[] buffer = new byte[BUFFER_SIZE];
        StringBuilder sb = new StringBuilder();
        int length;
        try {
            length = is.read(buffer);
            for (int i = 0; i < length; i++) {
                sb.append((char)buffer[i]);
            }
//            length = is.read(buffer);
//            System.out.println(length);
//            while ((length = is.read(buffer)) != 0) {
//                for (int i = 0; i < length; i++) {
//                    sb.append((char) buffer[i]);
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String s = sb.toString();
        rawRequest = s;
        final String[] lines = s.split("\\r\\n");
        final String first = lines[0];
        final String[] s3 = first.split(" ");
        assert s3.length == 3;
        method = s3[0];
        uri = s3[1];
        version = s3[2];

        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().length() > 0) {
                String[] kv = lines[i].split(": ");
                headers.put(kv[0], kv[1]);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getRawRequest() {
        return rawRequest;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                '}';
    }
}
