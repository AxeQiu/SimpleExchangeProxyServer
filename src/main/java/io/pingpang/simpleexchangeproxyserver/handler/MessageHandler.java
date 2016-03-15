/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.handler;

import io.pingpang.simpleexchangeproxyserver.ExchangeInputStream;
import io.pingpang.simpleexchangeproxyserver.ExchangeRequestLine;
import io.pingpang.simpleexchangeproxyserver.ExchangeSession;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 *
 * @author qiuyue
 */
public abstract class MessageHandler implements Callable<Void> {
    protected ExchangeInputStream input;
    protected OutputStream output;
    protected ExchangeSession session;
    protected Map<ExchangeRequestLine, RequestHandle> requestHandleMap;
    protected Map<ExchangeRequestLine, ResponseHandle> responseHandleMap;
    
    protected Object parseNull(Object obj) {
        if (obj == null || obj.toString().toLowerCase().equals("null")) {
            return null;
        }
        return obj;
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

    /**
     * @return the session
     */
    public ExchangeSession getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(ExchangeSession session) {
        this.session = session;
    }

    /**
     * @return the requestHandleMap
     */
    public Map<ExchangeRequestLine, RequestHandle> getRequestHandleMap() {
        return requestHandleMap;
    }

    /**
     * @param requestHandleMap the requestHandleMap to set
     */
    public void setRequestHandleMap(Map<ExchangeRequestLine, RequestHandle> requestHandleMap) {
        this.requestHandleMap = requestHandleMap;
    }

    /**
     * @return the responseHandleMap
     */
    public Map<ExchangeRequestLine, ResponseHandle> getResponseHandleMap() {
        return responseHandleMap;
    }

    /**
     * @param responseHandleMap the responseHandleMap to set
     */
    public void setResponseHandleMap(Map<ExchangeRequestLine, ResponseHandle> responseHandleMap) {
        this.responseHandleMap = responseHandleMap;
    }
    
}
