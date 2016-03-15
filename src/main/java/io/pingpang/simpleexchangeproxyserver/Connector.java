/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import io.pingpang.simpleexchangeproxyserver.mbean.ConnectorMBean;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.net.ssl.SSLContext;

/**
 *
 * @author qiuyue
 */
public class Connector implements ConnectorMBean {
    
    private static long totalConnection = 0;
    
    protected int soTimeout = 1000 * 60;
    protected boolean keepAlive = true;
    
    protected final InetAddress address;
    protected final int port;
    protected SSLContext sslContext;
    
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
        socket.setSoTimeout(soTimeout);
        socket.setKeepAlive(isKeepAlive());
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

    /**
     * @return the keepAlive
     */
    public boolean isKeepAlive() {
        return keepAlive;
    }

    /**
     * @param keepAlive the keepAlive to set
     */
    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }
    
}
