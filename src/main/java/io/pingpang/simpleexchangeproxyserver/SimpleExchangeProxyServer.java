/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import io.pingpang.simpleexchangeproxyserver.dispatcher.DispatcherFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author qiuyue
 */
public class SimpleExchangeProxyServer {

    protected int threadPoolSize = 6000;
    
    protected Acceptor acceptor;
    
    protected ThreadPoolExecutor threadPool;
    
    protected Routable routable;
    
    

    public SimpleExchangeProxyServer () {
        
        threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(threadPoolSize, (Runnable r) -> {
            Thread t = new Thread (r, "Thread-Handler");
            return t;
        });
        threadPool.setKeepAliveTime(30, TimeUnit.MINUTES);
        DispatcherFactory.setMessageHandlerPool(threadPool);
    }
    
    public void start() {
        acceptor.handlerPool = getThreadPool();
        acceptor.routable = getRoutable();
        Future future = getThreadPool().submit(acceptor);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(System.out);
        } finally {
            stop();
        }
    }
    
    public void stop() {
        getThreadPool().shutdownNow();
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

    /**
     * @return the routable
     */
    public Routable getRoutable() {
        return routable;
    }

    /**
     * @param routable the routable to set
     */
    public void setRoutable(Routable routable) {
        this.routable = routable;
    }

    /**
     * @return the threadPool
     */
    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    /**
     * @param threadPool the threadPool to set
     */
    public void setThreadPool(ThreadPoolExecutor threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * @return the threadPoolSize
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * @param threadPoolSize the threadPoolSize to set
     */
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        threadPool.setCorePoolSize(threadPoolSize);
    }
    
}
