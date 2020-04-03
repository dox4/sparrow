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
    private InputStream input;

    public SocketInputStream(InputStream inputStream, int buffSize) {
        this.input = inputStream;
        this.buf = new byte[buffSize];
    }

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

    /**
     * @description fill buffer
     */
    private void fill() throws IOException {
        int n = input.read(buf, 0, buf.length);
        count = Math.max(n, 0);
        pos = 0;
    }

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_OPTION = "OPTION";

    private static final int METHOD_MAX_LENGTH = 8;
    private static final int URI_MAX_LENGTH = 8192;
    private static final int PROTOCOL_MAX_LENGTH = 8;

//    private boolean matchMethod(final String method, byte[] buffer) {
//        for (int i = 0; i < method.length(); i++) {
//            if (method.charAt(i) != buf[pos + i]) {
//                return false;
//            }
//        }
//        return true;
//    }

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

    private boolean contains(final int ch, int... args) {
        for (int c : args) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }

    private void skip(int ...args) throws IOException {
        int ch;
        do {
            ch = read();
        } while (contains(ch, args));
        pos--;
    }

    private void expect(int ch) throws IOException {
        int chr;
        if ((chr =read()) != ch) {
            throw new IOException("expect `" + (char)ch + "', but got `<" + chr + ">'");
        }
    }

    private static final int HEADER_NAME_MAX_VALUE = 128;
    private String readHeaderName() throws IOException {
        int ch, index = 0;
        byte[] buffer = new byte[HEADER_NAME_MAX_VALUE];
        while ((ch = read()) != CR) {
            buffer[index++] = (byte) ch;
            if (index == PROTOCOL_MAX_LENGTH) {
                throw new IOException("HEADER NAME is too long.");
            }
        }
        pos--;
        return new String(buffer);
    }

    public Map<String, Object> readHeaders() throws IOException {
        Map<String, Object> headers = new HashMap<>();
        skip(CR, LF);

        while (true) {
            String name = readHeaderName();
            skip(SP, HT);
            expect(COLON);
            skip(SP, HT);
            String value = readHeaderValue();
        }

    }

    private String readHeaderValue() {
        return null;
    }
}
