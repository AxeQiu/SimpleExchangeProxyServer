/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import java.net.InetAddress;

/**
 *
 * @author qiuyue
 */
public class Routable {
    
    protected Connector defaultConnector;
    protected Router router;
    
    public Routable (Connector defaultConnector) {
        this.defaultConnector = defaultConnector;
    }
    
    public Routable (Router router) {
        this.router = router;
    }
    
    public Connector getConnector(InetAddress addr) {
        return router == null ? 
                defaultConnector : router.getConnector(addr);
    }
}
