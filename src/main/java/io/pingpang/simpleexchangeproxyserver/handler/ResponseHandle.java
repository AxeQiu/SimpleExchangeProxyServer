/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver.handler;

import io.pingpang.simpleexchangeproxyserver.ExchangeResponseObject;
import io.pingpang.simpleexchangeproxyserver.ExchangeSession;

/**
 *
 * @author qiuyue
 */
public interface ResponseHandle {
    
    public boolean handle(ExchangeSession session, ExchangeResponseObject response);
    
}
