/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.dispatcher;

import io.pingpang.simpleexchangeproxyserver.Connector;
import io.pingpang.simpleexchangeproxyserver.ExchangeInputStream;
import io.pingpang.simpleexchangeproxyserver.ExchangeResponseInputStream;
import io.pingpang.simpleexchangeproxyserver.handler.MessageHandler;
import io.pingpang.simpleexchangeproxyserver.handler.MessageHandlerFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author qiuyue
 */
public class Dispatcher implements Callable<Void> {
    
    ThreadPoolExecutor messageHandlerPool;
    
    protected final Socket connection;
    protected final Connector connector;
    
    public Dispatcher(Socket connection, Connector connector) throws IOException {
        this.connection = connection;
        this.connector = connector;
        /*
        System.out.println("Left_TcpNoDelay: " + this.connection.getTcpNoDelay());
        System.out.println("Left_KeepAlive: " + this.connection.getKeepAlive());
        System.out.println("System_http.keepAlive: " + System.getProperty("http.keepAlive"));
        System.out.println("System_http.maxConnections: " + System.getProperty("http.maxConnections"));
        System.out.println("System_http.errorstream.enableBuffering: " + System.getProperty("sun.net.http.errorstream.enableBuffering"));
        System.out.println("System_http.errorstream.timeout: " + System.getProperty("sun.net.http.errorstream.timeout"));
        System.out.println("System_http.errorstream.bufferSize: " + System.getProperty("sun.net.http.errorstream.bufferSize"));
        */
    }

    @Override
    public Void call() throws Exception {
        try (ExchangeInputStream eis = new ExchangeInputStream(connection.getInputStream()); 
                Socket connection2 = connector.getSocket(); ) {
            while (true) {
                eis.readRequest();
                
                /*Test*/
                System.out.println(eis.getMethod() + " " + eis.getPath());
                eis.getHeaders().forEach((key, value) -> {
                    System.out.println(key + " : " + value);
                });
                /*Test*/
                
                MessageHandler requestHandler = MessageHandlerFactory.getDefaultMessageHandler();
                requestHandler.setInput(eis);
                requestHandler.setOutput(connection2.getOutputStream());
                messageHandlerPool.submit(requestHandler).get();
                
                
                /*Test*/
                System.out.println("");
                /*Test*/
                
                ExchangeResponseInputStream eris = new ExchangeResponseInputStream(connection2.getInputStream());
                eris.readRequest();
                
                /*Test*/
                eris.getHeaders().forEach((key, value) -> {
                    System.out.println(key + " : " + value);
                });
                /*Test*/
                
                boolean isChunked = eris.isChunked();
                MessageHandler responseHandler = isChunked ? 
                        MessageHandlerFactory.getChunkedMessageHandler() :
                        MessageHandlerFactory.getDefaultMessageHandler();
                
                responseHandler.setInput(eris);
                responseHandler.setOutput(connection.getOutputStream());
                messageHandlerPool.submit(responseHandler).get();
                
            }
        } catch (IOException e) {
            /*Test*/
            e.printStackTrace(System.out);
            /*Test*/
            throw e;
        }
    }

}
