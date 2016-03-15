/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.dispatcher;

import io.pingpang.simpleexchangeproxyserver.Routable;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author qiuyue
 */
public class DispatcherFactory {
    
    protected static ThreadPoolExecutor messageHandlerPool;
    
    public static Dispatcher getDispatcher(Socket connection, Routable routable) throws IOException {
        Dispatcher dispatcher = new Dispatcher(connection, routable);
        dispatcher.messageHandlerPool = messageHandlerPool;
        return dispatcher;
    }

    /**
     * @param aMessageHandlerPool the messageHandlerPool to set
     */
    public static void setMessageHandlerPool(ThreadPoolExecutor aMessageHandlerPool) {
        messageHandlerPool = aMessageHandlerPool;
    }
}
