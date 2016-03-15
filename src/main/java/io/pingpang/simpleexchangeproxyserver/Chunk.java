/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

/**
 *
 * @author qiuyue
 */
public class Chunk implements Comparable {
    protected Integer order;
    protected int length;
    protected byte[] content;

    @Override
    public int compareTo(Object o) {
        return order.compareTo((Integer)o);
    }
    
    @Override
    public boolean equals(Object o) {
        return 
                o != null &&
                o instanceof Chunk &&
                ((Chunk)o).compareTo(this) == 0;
    }

    @Override
    public int hashCode() {
        return order.hashCode();
    }

    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
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
