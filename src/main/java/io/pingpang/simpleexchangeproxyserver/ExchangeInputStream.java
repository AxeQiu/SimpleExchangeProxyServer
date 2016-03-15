/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import mx4j.tools.adaptor.http.HttpConstants;
import mx4j.tools.adaptor.http.HttpException;
import mx4j.tools.adaptor.http.HttpInputStream;

/**
 * ExchangeInputStream支持HTTP GET POST OPTIONS 方法, 并且, 额外支持RPC_IN_DATA
 * RPC_OUT_DATA动词 ExchangeInputStream的readLine()方法, readRequest(),
 * readHeaders()方法会阻断当前线程, 并且不设超时 通常, 不用显示调用readHeaders()方法,
 * readRequest()方法会隐式调用readHeaders()方法 method, version, queryString,
 * path在readRequest()方法完成后可读 另外, 该类限制HTTP HEADER最大长度为8KB, 超过8KB会导致 http bad
 * request 错误(400)
 *
 * @author qiuyue
 */
public class ExchangeInputStream extends HttpInputStream {

    protected String method;

    protected int headerLength;
    
    protected int contentLength;

    protected final Map<String, String> attributes = new HashMap<>();
    
    protected byte[] content = null;
    
    protected final Map headers = new HashMap();
    
    protected int owaUserPositionBegin;
    
    protected int owaUserLength;
    
    protected final SortedSet<Chunk> chunkedContents = new TreeSet<>();

    public ExchangeInputStream(InputStream in) {
        super(in);
        chunkedContents.clear();
    }

    @Override
    protected void parseMethod(String method) throws HttpException {
        switch (method) {
            case HttpConstants.METHOD_GET:
                this.method = HttpConstants.METHOD_GET;
                break;
            case HttpConstants.METHOD_POST:
                this.method = HttpConstants.METHOD_POST;
                break;
            case "OPTIONS":
                this.method = "OPTIONS";
                break;
            case "HEAD":
                this.method = "HEAD";
                break;
            case "RPC_IN_DATA":
                this.method = "RPC_IN_DATA";
                break;
            case "RPC_OUT_DATA":
                this.method = "RPC_OUT_DATA";
                break;
            default:
                throw new HttpException(HttpConstants.STATUS_NOT_IMPLEMENTED, method);
        }
    }

    /**
     * 将query string转换为Map形式
     *
     * @param queryString
     * @throws HttpException
     */
    protected void parseAttributes(String queryString) throws HttpException {
        attributes.clear();
        if (queryString.isEmpty()) {
            return;
        }
        String[] tokens = queryString.split("&");
        for (String token : tokens) {
            String[] parts = token.split("=");
            if (parts.length > 2) {
                throw new HttpException(HttpConstants.STATUS_BAD_REQUEST, token);
            }
            if (parts.length != 2) {
                continue;
            }
            String key = parts[0].toLowerCase();
            String val = parts[1].toLowerCase();
            if (attributes.containsKey(key)) {
                throw new HttpException(HttpConstants.STATUS_BAD_REQUEST, token);
            }
            attributes.put(key, val);
        }
    }

    /**
     * 将BufferedInputStream的内容parse为Http Request Header 此方法不转换Http Body
     *
     * @throws IOException
     */
    @Override
    public void readRequest() throws IOException {
        /**
         * Override. Max length of header is 8KByte
         */
        mark(8192);

        String request = readLine();
        if (request == null) {
            throw new HttpException(HttpConstants.STATUS_BAD_REQUEST, "End of request");
        }
        /*Test*/
        //System.out.println(request);
        /*Test*/
        // Parses the request
        StringTokenizer parts = new StringTokenizer(request);
        try {
            parseMethod(parts.nextToken());
            parseRequest(parts.nextToken());
            /**
             * Override.
             */
            parseAttributes(getQueryString());
        } catch (NoSuchElementException ex) {
            throw new HttpException(HttpConstants.STATUS_BAD_REQUEST, request);
        }
        if (parts.hasMoreTokens()) {
            parseVersion(parts.nextToken());
        } else {
            /**
             * Override.
             */
            //version = 0.9f;
        }
        /**
         * Override.
         */
        //if (version >= 1.0f) {
        if (1.0f <= getVersion()) {
            readHeaders();
            /**
             * Override.
             */
            //parseVariables();
        }

        /**
         * Override.
         */
        headerLength = pos - markpos;
        reset();
    }

    @Override
    public String readLine() throws IOException {
        StringBuilder line = new StringBuilder(64);
        line.delete(0, line.length());
        int c;
        while (((c = read()) != -1) && (c != '\n') && (c != '\r')) {
            line.append((char) c);
        }
        if ((c == '\r') && ((c = read()) != '\n') && (c != -1)) {
            --pos;
        }
        if ((c == -1) && (line.length() == 0)) {
            return null;
        } else {
            return line.toString();
        }
    }

    /**
     * 获取Http Verb
     *
     * @return
     */
    @Override
    public String getMethod() {
        return method;
    }
    
    @Override
    protected void readHeaders() throws IOException {
        headers.clear();
        String header;
        while (((header = readLine()) != null) && !header.equals("")) {
            int colonIdx = header.indexOf(':');
            if (colonIdx != -1) {
                String name = header.substring(0, colonIdx);
                String value = header.substring(colonIdx + 1);
                /**
                 * Override.
                 */
                headers.put(name, value.trim());
            }
        }
    }
    
    @Override
    public String getHeader(String name) {
        return (String)headers.get(name);
    }
    
    @Override
    public Map getHeaders() {
        return headers;
    }

    /**
     * 获取头信息长度
     *
     * @return
     */
    public int getHeaderLength() {
        return headerLength;
    }
    
    /**
     * 获取消息体长度
     * @return 
     */
    public int getContentLength() {
        return contentLength;
    }

    /**
     * 请求为ActiveSync POST的情况下, 获取query string中的device id值
     * 
     * @return
     */
    public String getDeviceId() {
        String deviceid = attributes.get("deviceid");
        return deviceid;
    }

    /**
     * 请求为ActiveSync POST的情况下, 获取query string中的USER值
     *
     * @return
     */
    public String getActiveSyncUser() {
        String user = attributes.get("user");
        if (user != null ) {
            if (user.contains("\\")) {
                int p = user.indexOf("\\");
                if (p + 1 < user.length()) {
                    user = user.substring(p + 1, user.length());
                }
            } else if (user.contains("@")) {
                user = user.split("@")[0];
            } else if (user.contains("%")) {
                user = user.split("%")[0];
            }
        }
        return user;
    }

    /**
     * 请求为ActiveSync POST的情况下, 获取query string中的CMD值
     * 所返回的值一律被转换为小写字符
     * @return
     */
    public String getCmd() {
        String cmd = attributes.get("cmd");
        return cmd;
    }
    
    /**
     * 请求为ActiveSync POST的情况下, 获取query string中的device type值
     * @return 
     */
    public String getDeviceType() {
        String type = attributes.get("devicetype");
        return type;
    }
    
    /**
     * 请求为ActiveSync的情况下, 从请求头获取USER信息
     * @return 
     * @throws mx4j.tools.adaptor.http.HttpException 
     */
    public String getActiveSyncUserInAuth() throws HttpException {
        try {
            String value = getHeader("Authorization");
            String encoded = value.split(" ")[1];
            byte[] decoded = Base64.getDecoder().decode(encoded.getBytes("ISO8859-1"));
            String auth = new String(decoded);
            String username = auth.split(":")[0];
            String user;
            if (username.contains("\\")) {
                user = username.substring(auth.indexOf("\\") + 1, auth.indexOf(":"));
            } else if (username.contains("@")) {
                user = username.substring(0, auth.indexOf("@"));
            } else {
                user = username;
            }
            return user;
        } catch (RuntimeException | UnsupportedEncodingException e) {
            throw new HttpException(HttpConstants.STATUS_BAD_REQUEST, "Cannot resolve header: Authorization: " + e.getMessage());
        }
    }
    
    /**
     * 读取请求体
     * @throws IOException 
     */
    public void readContent() throws IOException {
        if (content != null) {
            return;
        }
        try {
            String value = String.valueOf(getHeaders().get("Content-Length"));
            if (value == null || value.equalsIgnoreCase("null")) {
                return;
            }
            contentLength = Integer.parseInt(value);
            if (contentLength <= 0) {
                return;
            }
            
            int total = headerLength + contentLength;
            mark(total);
            byte[] bs = new byte[total];
            int readed = 0;
            int begin = 0;
            int rest = total;
            while ((readed = this.read(bs, begin, rest)) != rest && readed != -1) {
                begin = readed;
                rest = total - readed;
            }
            reset();
            content = Arrays.copyOfRange(bs, headerLength, total);
        } catch (RuntimeException e) {
            throw new HttpException(HttpConstants.STATUS_BAD_REQUEST, e.getMessage());
        }
    }
    
    /**
     * 返回请求体
     * @return 
     */
    public byte[] getContent() {
        return content;
    }
    
    public boolean isChunked() {
        return 
                headers.containsKey("Transfer-Encoding") &&
                ((String)headers.get("Transfer-Encoding")).equalsIgnoreCase("chunked");
    }
    
}
