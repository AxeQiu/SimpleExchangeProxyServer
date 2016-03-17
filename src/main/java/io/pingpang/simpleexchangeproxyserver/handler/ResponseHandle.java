/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.handler;

import io.pingpang.simpleexchangeproxyserver.ExchangeResponseLine;
import io.pingpang.simpleexchangeproxyserver.ExchangeSession;

/**
 *
 * @author qiuyue
 */
public abstract class ResponseHandle {
    protected ExchangeSession session;
    protected ExchangeResponseLine responseLine;
    
    protected boolean block = false;
    
    public abstract void handle();
    
    public void setTransmit() {
        this.block = false;
    }
    
    public void setBlock() {
        this.block = true;
    }

    /**
     * @return the responseLine
     */
    public ExchangeResponseLine getResponseLine() {
        return responseLine;
    }

    /**
     * @param responseLine the responseLine to set
     */
    public void setResponseLine(ExchangeResponseLine responseLine) {
        this.responseLine = responseLine;
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
}
