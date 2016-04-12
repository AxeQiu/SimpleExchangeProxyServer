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
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import javax.net.ssl.SSLContext;

/**
 *
 * @author qiuyue
 */
public class Acceptor implements Callable<Void>, AcceptorMBean {
    
    private static long totalConnection = 0;
    
    protected int soTimeout = 120 * 1000;
    
    protected final InetSocketAddress address;
    protected SSLContext sslContext;
    protected ThreadPoolExecutor handlerPool;
    protected Routable routable;
    
    /**
     * 
     * @return
     * @throws IOException 
     */
    protected ServerSocket getServerSocket() throws IOException {
        return 
                getSslContext() == null ?
                new ServerSocket() :
                getSslContext().getServerSocketFactory().createServerSocket();
    }
    
    public Acceptor(InetSocketAddress address) {
        this.address = address;
    }

    /**
     * 
     * @return
     * @throws IOException 
     */
    @Override
    public Void call() throws IOException {
        ServerSocket serverSocket = getServerSocket();
        serverSocket.setPerformancePreferences(2, 1, 0);
        serverSocket.bind(address);
        while (true) {
            Socket connection = serverSocket.accept();
            connection.setTrafficClass(0xb8);
            connection.setSoTimeout(soTimeout);
            connection.setKeepAlive(true);
            connection.setTcpNoDelay(true);
            totalConnection += 1;
            handlerPool.submit(
                    DispatcherFactory.getDispatcher(
                            connection, routable));
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
     * @return the routable
     */
    public Map<Integer, Connector> getRoutable() {
        return routable;
    }

    /**
     * @param routable the routable to set
     */
    public void setRoutable(Routable routable) {
        this.routable = routable;
    }
    
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
    
}