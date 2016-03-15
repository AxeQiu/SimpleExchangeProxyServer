/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import io.pingpang.simpleexchangeproxyserver.dispatcher.DispatcherFactory;
import io.pingpang.simpleexchangeproxyserver.handler.RequestHandle;
import io.pingpang.simpleexchangeproxyserver.handler.ResponseHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author qiuyue
 */
public class SimpleExchangeProxyServer {

    protected int messageHandlerPoolSize = 100;
    protected int dispatcherPoolSize = 100;
    protected int acceptorPoolSize = 10;
    
    protected Acceptor acceptor;
    
    protected ThreadPoolExecutor dispatcherPool;
    protected ThreadPoolExecutor acceptorPool;
    protected ThreadPoolExecutor messageHandlerPool;
    
    protected Connector connector;
    
    
    
    

    public SimpleExchangeProxyServer () {
        
        System.setProperty("http.keepAlive", "true");
        System.setProperty("http.maxConnections", "100");
        System.setProperty("sun.net.http.errorstream.enableBuffering", "true");
        System.setProperty("sun.net.http.errorstream.timeout", "300");
        System.setProperty("sun.net.http.errorstream.bufferSize", "4096");
        
        messageHandlerPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(messageHandlerPoolSize, (Runnable r) -> {
            Thread t = new Thread (r, "Thread-MessageHandler");
            return t;
        });

        dispatcherPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(dispatcherPoolSize, (Runnable r) -> {
            Thread t = new Thread(r, "Thread-Dispatcher");
            return t;
        });

        acceptorPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(acceptorPoolSize, (Runnable r) -> {
            Thread t = new Thread(r, "Thread-Acceptor");
            return t;
        });
        
        DispatcherFactory.setMessageHandlerPool(messageHandlerPool);

    }
    
    public void start() {
        acceptor.dispatcherPool = dispatcherPool;
        acceptor.connector = connector;
        Future future = acceptorPool.submit(acceptor);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(System.out);
        } finally {
            stop();
        }
    }
    
    public void stop() {
        acceptorPool.shutdownNow();
        dispatcherPool.shutdownNow();
        messageHandlerPool.shutdownNow();
    }

    /**
     * @return the dispatcherPoolSize
     */
    public int getDispatcherPoolSize() {
        return dispatcherPoolSize;
    }

    /**
     * @param dispatcherPoolSize the dispatcherPoolSize to set
     */
    public void setDispatcherPoolSize(int dispatcherPoolSize) {
        this.dispatcherPoolSize = dispatcherPoolSize;
    }

    /**
     * @return the acceptorPoolSize
     */
    public int getAcceptorPoolSize() {
        return acceptorPoolSize;
    }

    /**
     * @param acceptorPoolSize the acceptorPoolSize to set
     */
    public void setAcceptorPoolSize(int acceptorPoolSize) {
        this.acceptorPoolSize = acceptorPoolSize;
    }

    /**
     * @return the connector
     */
    public Connector getConnector() {
        return connector;
    }

    /**
     * @param connector the connector to set
     */
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    /**
     * @return the messageHandlerPoolSize
     */
    public int getMessageHandlerPoolSize() {
        return messageHandlerPoolSize;
    }

    /**
     * @param messageHandlerPoolSize the messageHandlerPoolSize to set
     */
    public void setMessageHandlerPoolSize(int messageHandlerPoolSize) {
        this.messageHandlerPoolSize = messageHandlerPoolSize;
    }

    /**
     * @return the acceptor
     */
    public Acceptor getAcceptor() {
        return acceptor;
    }

    /**
     * @param acceptor the acceptor to set
     */
    public void setAcceptor(Acceptor acceptor) {
        this.acceptor = acceptor;
    }
    
    
    
    
}
