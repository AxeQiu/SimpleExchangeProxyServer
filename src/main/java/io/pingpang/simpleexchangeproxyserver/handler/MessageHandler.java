/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.handler;

import io.pingpang.simpleexchangeproxyserver.ExchangeInputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

/**
 *
 * @author qiuyue
 */
public abstract class MessageHandler implements Callable<Void> {
    protected ExchangeInputStream input;
    protected OutputStream output;
    protected Handle handle;
    
    protected Object parseNull(Object obj) {
        if (obj == null || obj.toString().toLowerCase().equals("null")) {
            return null;
        }
        return obj;
    }

    /**
     * @return the handle
     */
    public Handle getHandle() {
        return handle;
    }

    /**
     * @param handle the handle to set
     */
    public void setHandle(Handle handle) {
        this.handle = handle;
    }

    /**
     * @return the output
     */
    public OutputStream getOutput() {
        return output;
    }

    /**
     * @param output the output to set
     */
    public void setOutput(OutputStream output) {
        this.output = output;
    }

    /**
     * @return the input
     */
    public ExchangeInputStream getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(ExchangeInputStream input) {
        this.input = input;
    }
    
}
