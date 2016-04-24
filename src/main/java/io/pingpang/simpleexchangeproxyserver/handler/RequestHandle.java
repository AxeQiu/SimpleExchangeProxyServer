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
public interface RequestHandle {
    
    public boolean handle(ExchangeSession session, ExchangeRequestObject requestObject) throws HttpException;
}
