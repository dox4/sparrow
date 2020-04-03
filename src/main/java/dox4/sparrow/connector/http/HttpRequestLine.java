package dox4.sparrow.connector.http;

/**
 * @date 2020/4/2
 * @description TODO
 */

public final class HttpRequestLine {
    public static final int INITIAL_METHOD_SIZE = 8;
    public static final int INITIAL_URI_SIZE = 16;
    public static final int INITIAL_PROTOCOL_SIZE = 8;
    public static final int MAX_METHOD_SIZE = 8;
    public static final int MAX_URI_SIZE = 8;
    public static final int MAX_PROTOCOL_SIZE = 8;

    public char[] method;
    public int methodEnd;
    public char[] uri;
    public int uriEnd;

}
