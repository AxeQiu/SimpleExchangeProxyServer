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
public interface Router {
    public Connector getConnector(InetAddress addr);
}
