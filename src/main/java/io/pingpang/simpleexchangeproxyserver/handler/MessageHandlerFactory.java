/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.handler;

/**
 *
 * @author qiuyue
 */
public class MessageHandlerFactory {
    
    public static MessageHandler getDefaultMessageHandler() {
        MessageHandler handler = new DefaultMessageHandler();
        return handler;
    }
    
    public static MessageHandler getChunkedMessageHandler() {
        ChunkedMessageHandler handler = new ChunkedMessageHandler();
        return handler;
    }
    
    public static MessageHandler getNormalMessagehandler() {
        NormalMessageHandler handler = new NormalMessageHandler();
        return handler;
    }
}
