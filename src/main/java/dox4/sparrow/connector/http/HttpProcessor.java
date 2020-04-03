package dox4.sparrow.connector.http;

import dox4.sparrow.connector.SocketInputStream;
import dox4.sparrow.core.ServletProcessor;
import dox4.sparrow.core.StaticResourceProcessor;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

/**
 * @date 2020/4/2
 * @description 连接器模块 - 连接器支持类
 */
public class HttpProcessor {
    private HttpConnector connector;

    public HttpProcessor(HttpConnector httpConnector) {
        this.connector = httpConnector;
    }

    public void process(Socket socket) {
        SocketInputStream sis;
        OutputStream os;

        HttpRequest request;
        HttpResponse response;

        try {
            sis = new SocketInputStream(socket.getInputStream(), 2048);
            os = socket.getOutputStream();
            request = new HttpRequest(sis);
            response = new HttpResponse(os);
            response.setRequest(request);

            response.setHeader("ServerName", "dox4 example server");

            //            parseRequestLine(sis, os);
            String method = sis.readRequestMethod();
            request.setMethod(method);
            String uri = sis.readRequestUri();
            String queryString = null;
            int indexMark;
            if ((indexMark = uri.indexOf('?')) != -1) {
                queryString = uri.substring(indexMark + 1);
                uri = uri.substring(0, indexMark);
            }
            request.setQueryString(queryString);
            // Checking for an absolute URI (with the HTTP protocol)
            if (!uri.startsWith("/")) {
                indexMark = uri.indexOf("://");
                // Parsing out protocol and host name
                if (indexMark != -1) {
                    indexMark = uri.indexOf('/', indexMark + 3);
                    if (indexMark == -1) {
                        uri = "";
                    } else {
                        uri = uri.substring(indexMark);
                    }
                }
            }
            String match = ";jsessionid=";
            int semicolon = uri.indexOf(match);
            if (semicolon >= 0) {
                String rest = uri.substring(semicolon + match.length());
                int semicolon2 = rest.indexOf(';');
                if (semicolon2 >= 0) {
                    request.setRequestedSessionId(rest.substring(0, semicolon2));
                    rest = rest.substring(semicolon2);
                } else {
                    request.setRequestedSessionId(rest);
                    rest = "";
                }
                request.setRequestedSessionURL(true);
                uri = uri.substring(0, semicolon) + rest;
            } else {
                request.setRequestedSessionId(null);
                request.setRequestedSessionURL(false);
            }
            // Normalize URI (using String operations at the moment)
            String normalizedUri = normalize(uri);

            // Set the corresponding request properties
            if (normalizedUri != null) {
                request.setRequestURI(normalizedUri);
            } else {
                request.setRequestURI(uri);
            }

            if (normalizedUri == null) {
                throw new ServletException("Invalid URI: " + uri + "'");
            }

            request.setRequestURI(uri);
            String protocol = sis.readRequestProtocol();
            request.setProtocol(protocol);
            Map<String, String> headers = sis.readHeaders();
            request.addHeaders(headers);

            if (request.getRequestURI().startsWith("/servlet/")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            } else {
                StaticResourceProcessor processor = new StaticResourceProcessor();
                processor.process(request, response);
            }
            socket.close();
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

    private String normalize(String path) {
        if (path == null) {
            return null;
        }
        // Create a place for the normalized path
        String normalized = path;

        // Normalize "/%7E" and "/%7e" at the beginning to "/~"
        if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e")) {
            normalized = "/~" + normalized.substring(4);
        }

        // Prevent encoding '%', '/', '.' and '\', which are special reserved
        // characters
        if ((normalized.contains("%25"))
                || (normalized.contains("%2F"))
                || (normalized.contains("%2E"))
                || (normalized.contains("%5C"))
                || (normalized.contains("%2f"))
                || (normalized.contains("%2e"))
                || (normalized.contains("%5c"))) {
            return null;
        }

        if ("/.".equals(normalized)) {
            return "/";
        }

        // Normalize the slashes and add leading slash if necessary
        if (normalized.indexOf('\\') >= 0) {
            normalized = normalized.replace('\\', '/');
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) +
                    normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0) {
                break;
            }
            if (index == 0) {
                return null;
            }
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2) +
                    normalized.substring(index + 3);
        }

        // Declare occurrences of "/..." (three or more dots) to be invalid
        // (on some Windows platforms this walks the directory tree!!!)
        if (normalized.contains("/...")) {
            return null;
        }

        // Return the normalized path that we have completed
        return normalized;

    }
//
//    private void parseHeaders(SocketInputStream sis) throws IOException {
//        Map<String, Object> headers = sis.readHeaders();
//
//    }

//    private void parseRequestLine(SocketInputStream sis, OutputStream os) throws IOException {
//        String method = sis.readRequestMethod();
//        request.
//        String
//    }
}
