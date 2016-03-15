/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import io.pingpang.simpleexchangeproxyserver.dispatcher.DispatcherFactory;
import io.pingpang.simpleexchangeproxyserver.mbean.AcceptorMBean;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import javax.net.ssl.SSLContext;

/**
 *
 * @author qiuyue
 */
public class Acceptor implements Callable<Void>, AcceptorMBean {
    
    private static long totalConnection = 0;
    
    protected int soTimeout = 1000 * 60 * 10;
    protected boolean keepAlive = true;

    /**
     * @return the soTimeout
     */
    public int getSoTimeout() {
        return soTimeout;
    }

    /**
     * @param aSoTimeout the soTimeout to set
     */
    public void setSoTimeout(int aSoTimeout) {
        this.soTimeout = aSoTimeout;
    }
    
    protected final InetSocketAddress address;
    protected SSLContext sslContext;
    ThreadPoolExecutor dispatcherPool;
    Connector connector;
    
    
    protected ServerSocket getServerSocket() throws IOException {
        return 
                getSslContext() == null ?
                new ServerSocket() :
                getSslContext().getServerSocketFactory().createServerSocket();
    }
    
    public Acceptor(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public Void call() throws IOException {
        ServerSocket serverSocket = getServerSocket();
        serverSocket.setPerformancePreferences(2, 1, 0);
        serverSocket.bind(address);
        while (true) {
            Socket connection = serverSocket.accept();
            connection.setSoTimeout(soTimeout);
            connection.setKeepAlive(isKeepAlive());
            connection.setTcpNoDelay(true);
            totalConnection += 1;
            dispatcherPool.submit(
                    DispatcherFactory.getDispatcher(
                            connection, connector));
        }
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