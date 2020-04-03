package dox4.sparrow.connector;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @date 2020/4/2
 * @description TODO
 */
public class SocketInputStream extends InputStream {
    public static final int HEADER_LINE_MAX_LENGTH = 8192;
    /**
     * CR.
     */
    private static final byte CR = (byte) '\r';
    /**
     * LF.
     */
    private static final byte LF = (byte) '\n';
    /**
     * SP.
     */
    private static final byte SP = (byte) ' ';
    /**
     * HT.
     */
    private static final byte HT = (byte) '\t';
    /**
     * COLON.
     */
    private static final byte COLON = (byte) ':';
    /**
     * Lower case offset.
     */
    private static final int LC_OFFSET = 'A' - 'a';
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_OPTION = "OPTION";
    private static final int METHOD_MAX_LENGTH = 8;
    private static final int URI_MAX_LENGTH = 8192;
    private static final int PROTOCOL_MAX_LENGTH = 8;
    /**
     * Internal buffer.
     */
    protected byte[] buf;
    /**
     * Last valid byte.
     */
    protected int count;
    /**
     * Position in the buffer.
     */
    protected int pos;
    private InputStream input;

    public SocketInputStream(InputStream inputStream, int buffSize) {
        this.input = inputStream;
        this.buf = new byte[buffSize];
    }

    @Override
    public int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count) {
                return -1;
            }
        }
        return buf[pos++] & 0xFF;
    }

//    private boolean matchMethod(final String method, byte[] buffer) {
//        for (int i = 0; i < method.length(); i++) {
//            if (method.charAt(i) != buf[pos + i]) {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * @description fill buffer
     */
    private void fill() throws IOException {
        int n = input.read(buf, 0, buf.length);
        count = Math.max(n, 0);
        pos = 0;
    }

    /**
     * @return java.lang.String
     * @date 2020/4/3
     * @description this should be call first
     */
    public String readRequestMethod() throws IOException {
        int ch, index = 0;
        byte[] buffer = new byte[METHOD_MAX_LENGTH];

        while ((ch = read()) != SP) {
            buffer[index++] = (byte) ch;
            if (index == METHOD_MAX_LENGTH) {
                throw new IOException("method is too long.");
            }
        }
        return new String(buffer);
    }

    /**
     * @return java.lang.String
     * @date 2020/4/3
     * @description URI
     */
    public String readRequestUri() throws IOException {
        int ch = read(), index = 0;
        assert ch == SP : "NEED A SPACE BETWEEN HTTP METHOD AND URI.";
        byte[] buffer = new byte[URI_MAX_LENGTH];
        while ((ch = read()) != SP) {
            buffer[index++] = (byte) ch;
            if (index == URI_MAX_LENGTH) {
                throw new IOException("uri is too long.");
            }
        }
        return new String(buffer);
    }

    /**
     * @return java.lang.String
     * @date 2020/4/3
     * @description 协议
     */
    public String readRequestProtocol() throws IOException {
        int ch = read(), index = 0;
        assert ch == SP : "NEED A SPACE BETWEEN URI AND PROTOCOL.";
        byte[] buffer = new byte[PROTOCOL_MAX_LENGTH];
        while ((ch = read()) != CR) {
            buffer[index++] = (byte) ch;
            if (index == PROTOCOL_MAX_LENGTH) {
                throw new IOException("uri is too long.");
            }
        }
        pos--;
        return new String(buffer);
    }

//    private boolean contains(final int ch, int... args) {
//        for (int c : args) {
//            if (c == ch) {
//                return true;
//            }
//        }
//        return false;
//    }

//    private void skip(int... args) throws IOException {
//        int ch;
//        do {
//            ch = read();
//        } while (contains(ch, args));
//        pos--;
//    }

//    private static final int HEADER_NAME_MAX_VALUE = 128;
//    private String readHeaderName() throws IOException {
//        int ch, index = 0;
//        byte[] buffer = new byte[HEADER_NAME_MAX_VALUE];
//        while ((ch = read()) != CR) {
//            buffer[index++] = (byte) ch;
//            if (index == PROTOCOL_MAX_LENGTH) {
//                throw new IOException("HEADER NAME is too long.");
//            }
//        }
//        pos--;
//        return new String(buffer);
//    }

//    private void expect(int ch) throws IOException {
//        int chr;
//        if ((chr = read()) != ch) {
//            throw new IOException("expect `" + (char) ch + "', but got `<" + chr + ">'");
//        }
//    }

    public Map<String, String> readHeaders() throws IOException {
        Map<String, String> headers = new HashMap<>();
        // skip the CR LF between request line and headers
        skipOneLine();
        // read the header
        byte[] buffer = new byte[HEADER_LINE_MAX_LENGTH];
        while (true) {
            int ch, index = 0;
            while ((ch = read()) != CR && ch != LF) {
                buffer[index++] = (byte) ch;
            }
            String headerLine = new String(buffer).trim();
            if (headerLine.length() == 0) {
                // length is 0 means this line is the line between headers and request body
                // request body will not be presented in GET request
                return headers;
            } else {
                // split header line into key and value
                // this is not the best way to deal with HTTP header
                // but it is simple
                int colonIndex = headerLine.indexOf(":");
                if (colonIndex == -1) {
                    throw new IOException("header line missing `:': " + headerLine);
                }
                String name = headerLine.substring(0, colonIndex).trim();
                String value = headerLine.substring(colonIndex + 1).trim();
                headers.put(name, value);
            }
        }
    }

    private void skipOneLine() throws IOException {
        int ch = read();
        if (ch == CR) {
            ch = read();
        }
        if (ch != LF) {
            pos--;
        }
    }

//    public static final int HEADER_VALUE_MAX_LENGTH = 4096;
//    private String readHeaderValue() throws IOException {
//        int ch, index = 0;
//        byte[] buffer = new byte[HEADER_VALUE_MAX_LENGTH];
//        do {
//            ch = read();
//            buffer[index++] = (byte) ch;
//            if (index == PROTOCOL_MAX_LENGTH) {
//                throw new IOException("HEADER NAME is too long.");
//            }
//        } while (ch != CR && ch != LF);
//        pos--;
//        return new String(buffer);
//    }
}
