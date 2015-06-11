/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Grande.GSM.BACCWS_WAR.DataObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author rich
 */
@JacksonXmlRootElement (localName="QueryResponse")
public class QueryResponse implements Serializable {

    @JsonProperty("success")
    @JacksonXmlProperty(isAttribute=false,localName="success")
    Boolean blnSuccess = Boolean.FALSE;
    
    @JsonProperty("resultCount")
    @JacksonXmlProperty(isAttribute=false,localName="resultCount")
    private Integer intCount = 0;
    
    @JsonProperty("pagedResultCount")
    @JacksonXmlProperty(isAttribute=false,localName="pagedResultCount")
    private Integer intPagedResultCount = 0;
    
    @JsonProperty("requestStamp")
    @JacksonXmlProperty(isAttribute=false,localName="requestStamp")
    private String strRequestStamp = null;
    
    @JsonProperty("roundTripInSeconds")
    @JacksonXmlProperty(isAttribute=false,localName="roundTripInSeconds")
    private String strRoundTripInSeconds = null;
    
    @JsonProperty("message")
    @JacksonXmlProperty(isAttribute=false,localName="message")
    private Object strServerMessage = "";
    
    @JsonProperty("node")
    @JacksonXmlProperty(isAttribute=false,localName="node")
    private String strNode = "";
   
    @JsonProperty("resultSet")
    @JacksonXmlProperty(isAttribute=false,localName="resultSet")
    private List<Object> rs = null;

    public QueryResponse() {
        this.rs = new ArrayList<>();
        this.strRequestStamp = this.strGetTimeStamp();
    }
    
    public String strGetRoundTripInSeconds() {
        return this.strRoundTripInSeconds;
    }
    
    public void vSquashResult(List<Object> rs) {
        this.rs = rs;
        this.intCount = rs.size();
    }
    
    public String vSetResponseStamp() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/YY HH:mm:ss");
        Timestamp timeStampDate = null;
        return formatter.format(date);
    }
    
    public static String strGetTimeStamp() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/YY HH:mm:ss");
        Timestamp timeStampDate = null;
        return formatter.format(date);
    }
    
    public void vClearResults() { 
        if (this.rs != null) {
            this.rs.clear();
        } else {
            this.rs = new ArrayList();
        }
    }
    
    public void vSetRoundTrip(String strSeconds) { 
        this.strRoundTripInSeconds = strSeconds;
    }
    
    public String strGetRequestStamp() {
        return this.strRequestStamp;
    }
    
    public void vSetMessage(Object strServerMessage) {
        this.strServerMessage = strServerMessage;
    }
    
    public Object strGetMessage() { 
        return this.strServerMessage;
    }

    public Boolean blnGetSuccess() {
        return this.blnSuccess;
    }
    
    public void vSetSuccessFlag(Boolean blnFlag) {
        this.blnSuccess = blnFlag;
    }
    
    public String strGetNode() {
        return this.strNode;
    }
    
    public void vSetNode(String strNode) {
        this.strNode = strNode;
    }
    
    public void vAddResult(Object objResult) {
        if (this.rs == null) {
            this.rs = new ArrayList<>();
        }
        this.rs.add(objResult);
        this.intCount++;
    }
    
    public void vDecrementResultCount() {
        this.intCount--;
    }

    public List arlGetResultSet() {
        return this.rs;
    }
    
    public List<Object> vGetAttributeMap() {
        return this.rs;
    }
    
    public Integer intGetDataCount() {
        return this.intCount;
    }
    
    public Integer intGetPagedResultCount() {
        return this.intPagedResultCount;
    }
    
    public void vSetPagedResultCount(Integer intResultCount) {
        this.intPagedResultCount = intResultCount;
    }
}