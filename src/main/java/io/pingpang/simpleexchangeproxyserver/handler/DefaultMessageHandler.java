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
public class DefaultMessageHandler extends MessageHandler {

    @Override
    public Void call() throws Exception {
        int buffersize = 1024;
        int headerLength = input.getHeaderLength();
        String value = String.valueOf(input.getHeader("Content-Length"));
        int contentLength;
        if (parseNull(value) == null) {
            contentLength = 0;
        } else {
            contentLength = Integer.valueOf(value);
        }

        //header + body
        int total = headerLength + contentLength;
        int readed = 0;
        while (readed < total) {
            int rest = total - readed;
            int size = rest < buffersize ? rest : buffersize;
            byte[] buffer = new byte[size];
            int c = input.read(buffer);
            if (c == -1) {
                break;
            }
            output.write(buffer, 0, c);
            output.flush();
            readed += c;
        }
        output.flush();
        return null;
    }
}
