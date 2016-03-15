/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.pingpang.simpleexchangeproxyserver;

import java.util.Objects;

/**
 *
 * @author qiuyue
 */
public class ExchangeRequestLine {
    
    protected String verb;
    protected String path;
    protected String queryString;
    protected String version;
    
    @Override
    public boolean equals(Object o) {
        return 
                o != null &&
                o instanceof ExchangeRequestLine &&
                verb != null &&
                path != null &&
                path.equalsIgnoreCase(((ExchangeRequestLine)o).getPath()) &&
                verb.equalsIgnoreCase(((ExchangeRequestLine)o).getVerb());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.verb);
        hash = 47 * hash + Objects.hashCode(this.path);
        return hash;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the verb
     */
    public String getVerb() {
        return verb;
    }

    /**
     * @param verb the verb to set
     */
    public void setVerb(String verb) {
        this.verb = verb;
    }

    /**
     * @return the queryString
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * @param queryString the queryString to set
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
