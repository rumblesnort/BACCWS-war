/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Grande.GSM.BACCWS_WAR.DataObjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 *
 * @author rich
 */
@JsonInclude
public class SenchaFilter implements Serializable, ClientObject {
    
    @JsonProperty("property")
    public String strProperty;
    
    @JsonProperty("value")
    public String strValue;
    
    
    @Override
    public String toString() {
        return this.strProperty + "->" + this.strValue;
    }
    
    // so stream's distinct() works as we want it to
    @Override
    public int hashCode() {
        int p = this.strProperty.toLowerCase().hashCode();
        int s = this.strValue.toLowerCase().hashCode();
        return (p+s);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this.hashCode() == obj.hashCode()) {
            return true;
        } else {
            return false;
        }
    }
}
