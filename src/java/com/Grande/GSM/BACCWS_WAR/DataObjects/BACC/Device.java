/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Grande.GSM.BACCWS_WAR.DataObjects.BACC;

import com.Grande.GSM.BACCWS_WAR.DataObjects.ClientObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 *
 * @author rich
 */
public class Device implements ClientObject {
    
    // <editor-fold defaultstate="collapsed" desc="****** Class member vars ******">
    @JacksonXmlProperty(localName="macAddress", isAttribute=false)
    @JsonProperty("macAddress")
    private String strMac;
    
    @JacksonXmlProperty(localName="classOfServiceRequested", isAttribute=false)
    @JsonProperty("classOfServiceRequested")
    private String strSelectedClassOfService;
    
    @JacksonXmlProperty(localName="classOfServiceSelected", isAttribute=false)
    @JsonProperty("classOfServiceSelected")
    private String strProvisionedClassOfService;
    
    @JacksonXmlProperty(localName="fqdn", isAttribute=false)
    @JsonProperty("fqdn")
    private String strFQDN;
    
    @JacksonXmlProperty(localName="ownerId", isAttribute=false)
    @JsonProperty("ownerId")
    private String strOwnerId;
    
    @JacksonXmlProperty(localName="isProvisioned", isAttribute=false)
    @JsonProperty("isProvisioned")
    private Boolean blnIsProvisioned = false;
    
    @JacksonXmlProperty(localName="domain", isAttribute=false)
    @JsonProperty("domain")
    private String strDomain;
    
    @JacksonXmlProperty(localName="provExplanation", isAttribute=false)
    @JsonProperty("provExplanation")
    private String strProvExplanation;
    
    @JacksonXmlProperty(localName="oidRevision", isAttribute=false)
    @JsonProperty("oidRevision")
    private String strOIDRevision;
    
    @JacksonXmlProperty(localName="provGroup", isAttribute=false)
    @JsonProperty("provGroup")
    private String strProvGroup;
    
    @JacksonXmlProperty(localName="host", isAttribute=false)
    @JsonProperty("host")
    private String strHost;
    
    @JacksonXmlProperty(localName="deviceType", isAttribute=false)
    @JsonProperty("deviceType")
    private String strDeviceType;
    
    @JacksonXmlProperty(localName="isRegistered", isAttribute=false)
    @JsonProperty("isRegistered")
    private Boolean isRegistered = false;
    
    @JacksonXmlProperty(localName="isInRequiredProvGroup", isAttribute=false)
    @JsonProperty("isInRequiredProvGroup")
    private Boolean blnisInRequiredProvGroup = false; 
    
    @JacksonXmlProperty(localName="provAccess", isAttribute=false)
    @JsonProperty("provAccess")
    private String strProvAccess;
    
    @JacksonXmlProperty(localName="provisiondeddhcpCriteria", isAttribute=false)
    @JsonProperty("dhcpCriteria")
    private String strDhcpCriteria;
    
    @JacksonXmlProperty(localName="dhcpCriteriaRequested", isAttribute=false)
    @JsonProperty("dhcpCriteriaRequested")
    private String strDhcpCriteriaRequested;
    
    @JacksonXmlProperty(localName="dhcpCriteriaSelected", isAttribute=false)
    @JsonProperty("dhcpCriteriaSelected")
    private String strDhcpCriteriaSelected;
    
    @JacksonXmlProperty(localName="provReason", isAttribute=false)
    @JsonProperty("provReason")
    private String strProvReason;
    
    @JacksonXmlProperty(localName="docsisVersionRequested", isAttribute=false)
    @JsonProperty("docsisVersionRequested")
    private String strDocsisVersionRequested;
    
    @JacksonXmlProperty(localName="docsisVersionSelected", isAttribute=false)
    @JsonProperty("docsisVersionSelected")
    private String strDocsisVersionSelected;
    
    @JacksonXmlProperty(localName="isBehindRequiredDevice", isAttribute=false)
    @JsonProperty("isBehindRequiredDevice")
    private Boolean blnIsBehindRequiredDevice = true;
    // </editor-fold>
    
}
