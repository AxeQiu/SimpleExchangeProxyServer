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
public class ChunkedMessageHandler extends MessageHandler {

    @Override
    public Void call() throws Exception {
        int buffersize = 4096;
        int readed;
        byte[] buffer = new byte[buffersize];
        while ((readed = input.read(buffer)) != -1) {
            output.write(buffer, 0, readed);
            //System.out.println(new String(buffer, 0, readed));
        }
        output.flush();
        return null;
    }
    
}
