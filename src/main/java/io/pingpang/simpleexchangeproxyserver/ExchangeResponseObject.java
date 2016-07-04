/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import java.util.Map;

/**
 *
 * @author qiuyue
 */
public class ExchangeResponseObject {
    protected ExchangeResponseLine responseLine;
    protected Map headers;
    protected byte[] content;

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
     * @return the headers
     */
    public Map getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map headers) {
        this.headers = headers;
    }

    /**
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }
}
