/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.StringTokenizer;
import mx4j.tools.adaptor.http.HttpConstants;
import mx4j.tools.adaptor.http.HttpException;

/**
 *
 * @author qiuyue
 */
public class ExchangeResponseInputStream extends ExchangeInputStream {
    
    protected int statusCode;
    
    protected String reasonPhrase;

    public ExchangeResponseInputStream(InputStream in) {
        super(in);
    }
    
    protected void parseStatusCode(String code) {
        statusCode = Integer.parseInt(code);
    }
    
    protected void parseReasonPhrase(String reason) {
        reasonPhrase = reason;
    }
    
    /**
     * 读取响应头
     * @throws IOException 
     */
    @Override
    public void readRequest() throws IOException {
        mark(8192);
        String request = readLine();
        if (request == null) {
            throw new HttpException(HttpConstants.STATUS_BAD_REQUEST, "End of request");
        }
        // Parses the request
        StringTokenizer parts = new StringTokenizer(request);
        parseVersion(parts.nextToken());
        parseStatusCode(parts.nextToken());
        if (parts.hasMoreTokens()) {
            parseReasonPhrase(parts.nextToken());
        }
        
        readHeaders();
        
        headerLength = pos - markpos;
        reset();
    }
    
    /**
     * 调用此方法将抛出UnsupportedOperationException异常
     * @return 
     * @throws UnsupportedOperationException
     */
    @Override
    @Deprecated
    public String getMethod() {
        throw new UnsupportedOperationException("Http Response have no method");
    }

    /**
     * @return the responseCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return the reasonPhrase
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }
    
    @Override
    public String getHeader(String name) {
        return (String)headers.get(name);
    }
    
    @Override
    public Map getHeaders() {
        return headers;
    }
    
}
