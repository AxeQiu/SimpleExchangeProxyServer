/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.handler;

import io.pingpang.simpleexchangeproxyserver.ExchangeRequestObject;
import io.pingpang.simpleexchangeproxyserver.ExchangeSession;
import mx4j.tools.adaptor.http.HttpException;

/**
 *
 * @author qiuyue
 */
public abstract class RequestHandle {
    protected ExchangeSession session;
    protected ExchangeRequestObject requestObject;

    protected boolean block = false;
    
    public abstract void handle() throws HttpException;

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
    
    public void setTransmit() {
        this.block = false;
    }
    
    public void setBlock() {
        this.block = true;
    }

    /**
     * @return the requestObject
     */
    public ExchangeRequestObject getRequestObject() {
        return requestObject;
    }

    /**
     * @param requestObject the requestObject to set
     */
    public void setRequestObject(ExchangeRequestObject requestObject) {
        this.requestObject = requestObject;
    }
}
