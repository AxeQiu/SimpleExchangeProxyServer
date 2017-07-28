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
import javax.net.ssl.SSLServerSocket;

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
    
    protected static final String[] CIPHER_SUITES = {
        // http://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html#footnote1-1
        // https://blog.qualys.com/ssllabs/2013/06/25/ssl-labs-deploying-forward-secrecy
        "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
        "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
        "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
        "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
        "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
        "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
        "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
        "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
        "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA",
        "TLS_ECDHE_RSA_WITH_RC4_128_SHA",
        "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
        "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
        "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
        "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
        "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
        "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
        "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
        "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
        "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
        "TLS_DHE_DSS_WITH_AES_256_CBC_SHA",
        "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
        "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
        "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
        "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
        "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
        "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384",
        "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
        "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256"
    };
    
    /**
     * 
     * @return
     * @throws IOException 
     */
    protected ServerSocket getServerSocket() throws IOException {
        ServerSocket serverSocket = 
                getSslContext() == null ?
                new ServerSocket() :
                getSslContext().getServerSocketFactory().createServerSocket();
        //if (serverSocket instanceof SSLServerSocket) {
        //    ((SSLServerSocket)serverSocket).setEnabledCipherSuites(CIPHER_SUITES);
        //}
        return serverSocket;
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
