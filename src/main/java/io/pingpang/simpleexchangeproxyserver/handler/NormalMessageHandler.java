/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.handler;

import io.pingpang.simpleexchangeproxyserver.ExchangeInputStream;
import io.pingpang.simpleexchangeproxyserver.ExchangeRequestLine;
import io.pingpang.simpleexchangeproxyserver.ExchangeRequestObject;
import io.pingpang.simpleexchangeproxyserver.ExchangeResponseInputStream;
import io.pingpang.simpleexchangeproxyserver.ExchangeResponseLine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 *
 * @author qiuyue
 */
public class NormalMessageHandler extends MessageHandler {
    
    protected void directlyTransmit() throws IOException {
        int buffersize = 1024;
        int headerLength = input.getHeaderLength();
        String value = String.valueOf(input.getHeader("Content-Length"));
        int contentLength;
        if (parseNull(value) == null) {
            contentLength = 0;
        } else {
            contentLength = Integer.valueOf(value);
        }

        //header + body
        int total = headerLength + contentLength;
        int readed = 0;
        while (readed < total) {
            int rest = total - readed;
            int size = rest < buffersize ? rest : buffersize;
            byte[] buffer = new byte[size];
            int c = input.read(buffer);
            if (c == -1) {
                break;
            }
            //System.out.println(new String(buffer, 0, c));
            output.write(buffer, 0, c);
            output.flush();
            readed += c;
        }
        output.flush();
    }
    
    protected void handleRequest(ExchangeRequestLine requestLine) throws IOException {
        if (getRequestHandleMap().containsKey(requestLine)) {
            input.readContent();
            RequestHandle handle = getRequestHandleMap().get(requestLine);
            ExchangeRequestObject requestObject = new ExchangeRequestObject();
            requestObject.setRequestLine(requestLine);
            requestObject.setHeaders(input.getHeaders());
            requestObject.setContent(input.getContent());
            handle.setSession(session);
            handle.setRequestObject(requestObject);
            handle.handle(); //Hook
            if (!handle.block) {
                byte[] requestBytes = convert(requestObject);
                output.write(requestBytes);
                output.flush();
            }
            dumpInputStream(input);
        } else {
            directlyTransmit();
        }
    }
    
    protected byte[] convert(ExchangeRequestObject requestObject) {
        ExchangeRequestLine requestLine = requestObject.getRequestLine();
        String version = requestLine.getVersion();
        Map headers = requestObject.getHeaders();
        byte[] content = requestObject.getContent();
        
        StringBuilder sb = new StringBuilder();
        sb.delete(0, sb.length());
        
        String queryString = requestLine.getQueryString();
        
        sb.append(requestLine.getVerb()).append(" ").append(requestLine.getPath());
        if (queryString != null && !queryString.isEmpty()) {
            sb.append("?").append(requestLine.getQueryString());
        }
        sb.append(" ").append(version).append("\r\n");
        
        headers.forEach((key, value) -> {
            sb.append((String)key).append(": ").append((String)value).append("\r\n");
        });
        sb.append("\r\n");
        
        byte[] part1 = sb.toString().getBytes();
        
        byte[] completeRequest = new byte[part1.length + content.length];
        System.arraycopy(part1, 0, completeRequest, 0, part1.length);
        System.arraycopy(content, 0, completeRequest, part1.length, content.length);

        return completeRequest;
    }
    
    
    
    protected void handleResponse(ExchangeRequestLine requestLine) throws IOException {
        if (getResponseHandleMap().containsKey(requestLine)) {
            ResponseHandle handle = getResponseHandleMap().get(requestLine);
            int responseCode = ((ExchangeResponseInputStream)input).getStatusCode();
            ExchangeResponseLine responseLine = new ExchangeResponseLine();
            responseLine.setResponseCode(responseCode);
            handle.setResponseLine(responseLine);
            handle.handle(); //Hook
            if (!handle.block) {
                directlyTransmit();
            } else {
                dumpInputStream(input);
            }
        } else {
            directlyTransmit();
        }
    }
    
    protected void dumpInputStream(ExchangeInputStream inputStream) throws IOException {
        ByteArrayOutputStream mockOutput = new ByteArrayOutputStream();
        OutputStream temp = this.output;
        this.output = mockOutput;
        directlyTransmit();
        this.output = temp;
        mockOutput.reset();
    }
    

    @Override
    public Void call() throws Exception {
        String key = "request";
        if (input instanceof ExchangeResponseInputStream) {
            ExchangeRequestLine requestLine = (ExchangeRequestLine)session.get(key);
            handleResponse(requestLine);
        } else if (input instanceof ExchangeInputStream) {
            String verb = input.getMethod();
            String path = input.getPath();
            String queryString = input.getQueryString();
            float version = input.getVersion();
            ExchangeRequestLine requestLine = new ExchangeRequestLine();
            requestLine.setPath(path);
            requestLine.setVerb(verb);
            requestLine.setQueryString(queryString);
            requestLine.setVersion("HTTP/" + version);
            session.put(key, requestLine);
            handleRequest(requestLine);
        }
        return null;
    }
}
