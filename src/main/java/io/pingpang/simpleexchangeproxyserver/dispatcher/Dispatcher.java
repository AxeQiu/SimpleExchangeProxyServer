/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.dispatcher;

import io.pingpang.simpleexchangeproxyserver.Connector;
import io.pingpang.simpleexchangeproxyserver.ExchangeInputStream;
import io.pingpang.simpleexchangeproxyserver.ExchangeResponseInputStream;
import io.pingpang.simpleexchangeproxyserver.ExchangeSession;
import io.pingpang.simpleexchangeproxyserver.Routable;
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
    protected final Routable routable;
    
    public Dispatcher(Socket connection, Routable routable) {
        this.connection = connection;
        this.routable = routable;
    }

    @Override
    public Void call() throws Exception {
        Connector connector = routable.getConnector(connection.getInetAddress());
        try (ExchangeInputStream eis = new ExchangeInputStream(connection.getInputStream()); 
                Socket connection2 = connector.getSocket(); ) {
            ExchangeSession session = new ExchangeSession();
            while (true) {
                eis.readRequest();
                
                /*Test
                System.out.println(eis.getMethod() + " " + eis.getPath());
                eis.getHeaders().forEach((key, value) -> {
                    System.out.println(key + " : " + value);
                });
                Test*/
                
                MessageHandler requestHandler = MessageHandlerFactory.getNormalMessagehandler();
                requestHandler.setRequestHandleMap(connector.getRequestHandle());
                requestHandler.setSession(session);
                requestHandler.setInput(eis);
                requestHandler.setOutput(connection2.getOutputStream());
                messageHandlerPool.submit(requestHandler).get();
                
                
                /*Test
                System.out.println("");
                Test*/
                
                ExchangeResponseInputStream eris = new ExchangeResponseInputStream(connection2.getInputStream());
                eris.readRequest();
                
                /*Test
                eris.getHeaders().forEach((key, value) -> {
                    System.out.println(key + " : " + value);
                });
                Test*/
                
                boolean isChunked = eris.isChunked();
                MessageHandler responseHandler = isChunked ? 
                        MessageHandlerFactory.getChunkedMessageHandler() :
                        MessageHandlerFactory.getNormalMessagehandler();
                responseHandler.setResponseHandleMap(connector.getResponseHandle());
                responseHandler.setSession(session);
                responseHandler.setInput(eris);
                responseHandler.setOutput(connection.getOutputStream());
                messageHandlerPool.submit(responseHandler).get();
                
            }
        } catch (IOException e) {
            /*Test
            e.printStackTrace(System.out);
            Test*/
            throw e;
        }
    }

}
