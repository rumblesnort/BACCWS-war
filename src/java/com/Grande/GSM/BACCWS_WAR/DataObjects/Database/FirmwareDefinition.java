/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Grande.GSM.BACCWS_WAR.DataObjects.Database;

import com.Grande.GSM.BACCWS_WAR.DataObjects.ClientObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;

/**
 *
 * @author rich
 */
public class FirmwareDefinition implements ClientObject {
    
    // <editor-fold defaultstate="collapsed" desc="****** Class member vars ******">
    @JacksonXmlProperty(localName="defId", isAttribute=false)
    @JsonProperty("defId")
    public Integer intId = 0;
    
    @JacksonXmlProperty(localName="make", isAttribute=false)
    @JsonProperty("make")
    public String strMake = null;
    
    @JacksonXmlProperty(localName="model", isAttribute=false)
    @JsonProperty("model")
    public String strModel = null;
    
    @JacksonXmlProperty(localName="docsisLevel", isAttribute=false)
    @JsonProperty("docsisLevel")
    public Integer intDocsisLevel = 0;
    
    @JacksonXmlProperty(localName="firmwareFile", isAttribute=false)
    @JsonProperty("firmwareFile")
    public String strFirmwareFile = null;
    
    @JacksonXmlProperty(localName="residentialCOS", isAttribute=false)
    @JsonProperty("residentialCOS")
    public String strResidentialCOS = null;
    
    @JacksonXmlProperty(localName="commercialCOS", isAttribute=false)
    @JsonProperty("commercialCOS")
    public String strCommercialCOS = null;
    
    @JacksonXmlProperty(localName="maxPorts", isAttribute=false)
    @JsonProperty("maxPorts")
    public Integer intMaxPorts = 0;
    
    @JacksonXmlProperty(localName="disabledCOS", isAttribute=false)
    @JsonProperty("disabledCOS")
    public String strDisabledCOS = null;
    
    @JacksonXmlProperty(localName="emtaCOS", isAttribute=false)
    @JsonProperty("emtaCOS")
    public String strMTACOS = null;
    
    @JacksonXmlProperty(localName="upgradeProtocol", isAttribute=false)
    @JsonProperty("upgradeProtocol")
    public Integer intUpgradeProtocol = 0;
    
    @JacksonXmlProperty(localName="ipv6", isAttribute=false)
    @JsonProperty("ipv6")
    public Integer intIPV6 = 0;
    
    @JacksonXmlProperty(localName="BSDeviceName", isAttribute=false)
    @JsonProperty("bsDeviceName")
    public String strBSDdeviceName = null;
    
    @JacksonXmlProperty(localName="BSDeviceType", isAttribute=false)
    @JsonProperty("bsDeviceType")
    public String strBSDeviceType = null;
    
    @JacksonXmlProperty(localName="BSDeviceLevel", isAttribute=false)
    @JsonProperty("bsDeviceLevel")
    public String strBSDDeviceLevel = null;
    // </editor-fold>
    
    @Override
    public String toString() {
        final Map mapStringify = new HashMap();
        mapStringify.put("intId", this.intId);
        mapStringify.put("strMake", this.strMake);
        mapStringify.put("strModel", this.strModel);
        mapStringify.put("intDocsisLevel", this.intDocsisLevel);
        mapStringify.put("strFirmwareFile", this.strFirmwareFile);
        mapStringify.put("strResidentialCOS", this.strResidentialCOS);
        mapStringify.put("strCommercialCOS", this.strCommercialCOS);
        mapStringify.put("intMaxPorts", this.intMaxPorts);
        mapStringify.put("strDisabledCOS", this.strDisabledCOS);
        mapStringify.put("strMTACOS", this.strMTACOS);
        mapStringify.put("intUpgradeProtocol", this.intUpgradeProtocol);
        mapStringify.put("intIPV6", this.intIPV6);
        mapStringify.put("strBSDdeviceName", this.strBSDdeviceName);
        mapStringify.put("strBSDeviceType", this.strBSDeviceType);
        mapStringify.put("strBSDDeviceLevel", this.strBSDDeviceLevel);
        return mapStringify.toString();
    }
    
}
