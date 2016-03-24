/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.handler;

import java.io.IOException;
import mx4j.tools.adaptor.http.HttpException;

/**
 *
 * @author qiuyue
 */
public class ChunkedMessageHandler extends MessageHandler {

    @Override
    public Void call() throws Exception {
        try {
            if (input.getHeaders().containsKey("Trailer")) {
                throw new HttpException(400, "The ChunkedMessageHandler does not suuport Trailer");
            }
            int headerLength = input.getHeaderLength();

            //STEP1 : flush header
            int headerReaded = -1;
            int headerBegin = 0;
            int headerRest = headerLength;
            byte[] headerBuffer = new byte[headerLength];
            while (headerRest != 0 && ((headerReaded = input.read(headerBuffer, headerBegin, headerRest)) != -1)) {
                headerBegin += headerReaded;
                headerRest -= headerReaded;
            }
            output.write(headerBuffer);
            //output.flush();
            if (headerReaded == -1) {
                throw new HttpException(400, "Bad Chunk");
            }

            //STEP2: read and write chunk
            while (true) {
                int[] chunkSizeBuffer = new int[16];
                int i = 0;
                while (true) {
                    int b = input.read();
                    if (b == -1) {
                        if (i > 0) {
                            output.write(headerBuffer, 0, i);
                            //output.flush();
                            throw new HttpException(400, "Bad Chunk");
                        }
                        return null;
                    }
                    if (b == '\r') {
                        b = input.read();
                        if (b == '\n') {
                            break;
                        } else {
                            throw new HttpException(400, "Bad Chunk");
                        }
                    }
                    //ignore chunk extension
                    if (b == ';') {
                        break;
                    }
                    chunkSizeBuffer[i++] = b;
                }
                String s = new String(chunkSizeBuffer, 0, i);
                int chunkSize = Integer.parseInt(s, 16);
                if (chunkSize == 0) {
                    output.write("0\r\n\r\n".getBytes());
                    output.flush();
                    input.read(); // \r
                    input.read(); // \n
                    return null;
                }
                output.write((s + "\r\n").getBytes());
                //output.flush();
                int size = chunkSize + 2; // \r\n
                byte[] buffer = new byte[size];
                int readed;
                int rest = size;
                while ((readed = input.read(buffer, 0, rest)) != -1) {
                    rest -= readed;
                    output.write(buffer, 0, readed);
                    //output.flush();
                    if (rest == 0) {
                        break;
                    }
                }
                if (readed == -1) {
                    throw new HttpException(400, "Bad Chunk");
                }
            }
        } catch (IOException | NumberFormatException e) {
            //e.printStackTrace(System.out);
            throw e;
        }

        /*
        int buffersize = 1024;
        int readed;
        byte[] buffer = new byte[buffersize];
        while ((readed = input.read(buffer)) != -1) {
            output.write(buffer, 0, readed);
            System.out.println(new String(buffer, 0, readed));
        }
        output.flush();
        return null;
                */
    }
    
}
