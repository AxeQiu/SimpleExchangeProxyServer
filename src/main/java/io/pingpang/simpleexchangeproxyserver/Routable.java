/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import java.net.InetAddress;
import java.util.HashMap;

/**
 *
 * @author qiuyue
 */
public class Routable extends HashMap<Integer, Connector> {
    
    protected Connector defaultConnector;
    
    public Routable (Connector defaultConnector) {
        this.defaultConnector = defaultConnector;
    }
    
    public Connector getConnector(InetAddress addr) {
        byte[] ip = addr.getAddress();
        Integer key = (int) ip[0];
        if (this.containsKey(key)) {
            return this.get(key);
        }
        return defaultConnector;
    }
}
