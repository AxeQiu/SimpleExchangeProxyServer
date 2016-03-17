/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import io.pingpang.simpleexchangeproxyserver.handler.RequestHandle;
import io.pingpang.simpleexchangeproxyserver.handler.ResponseHandle;
import io.pingpang.simpleexchangeproxyserver.mbean.ConnectorMBean;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLContext;

/**
 *
 * @author qiuyue
 */
public class Connector implements ConnectorMBean {
    
    private static long totalConnection = 0;
    
    protected int soTimeout = 1000 * 60;
    
    protected final InetAddress address;
    protected final int port;
    protected SSLContext sslContext;
    
    protected Map<ExchangeRequestLine, RequestHandle> requestHandleMap = new HashMap<>();
    protected Map<ExchangeRequestLine, ResponseHandle> responseHandleMap = new HashMap<>();
    
    public Connector(InetAddress target, int port) {
        this.address = target;
        this.port = port;
    }
    
    public Socket getSocket() throws IOException {
        Socket socket =  
                sslContext == null ?
                new Socket() :
                sslContext.getSocketFactory().createSocket();
        socket.setPerformancePreferences(2, 1, 0);
        socket.setTrafficClass(0xb8);
        socket.setSoTimeout(soTimeout);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        socket.connect(new InetSocketAddress(address, port));
        totalConnection += 1;
        return socket;
    }

    /**
     * @return the sslContext
     */
    public SSLContext getSslContext() {
        return sslContext;
    }

    /**
     * @param sslContext the sslContext to set
     */
    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public long getTotalConnection() {
        return totalConnection;
    }

    /**
     * @return the soTimeout
     */
    public int getSoTimeout() {
        return soTimeout;
    }

    /**
     * @param soTimeout the soTimeout to set
     */
    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }
    
    public void registerRequestHandle(ExchangeRequestLine requestLine, RequestHandle handle) {
        requestHandleMap.put(requestLine, handle);
    }
    
    public void registerResponseHandle(ExchangeRequestLine requestLine, ResponseHandle handle) {
        responseHandleMap.put(requestLine, handle);
    }
    
    public Map<ExchangeRequestLine, RequestHandle> getRequestHandle() {
        return requestHandleMap;
    }
    
    public Map<ExchangeRequestLine, ResponseHandle> getResponseHandle() {
        return responseHandleMap;
    }
}
