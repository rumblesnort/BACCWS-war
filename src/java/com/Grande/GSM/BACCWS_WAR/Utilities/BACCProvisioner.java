/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Grande.GSM.BACCWS_WAR.Utilities;

import com.cisco.provisioning.cpe.Batch;
import com.cisco.provisioning.cpe.BatchStatus;
import com.cisco.provisioning.cpe.CommandStatus;
import com.cisco.provisioning.cpe.PACEConnection;
import com.cisco.provisioning.cpe.PACEConnectionFactory;
import com.cisco.provisioning.cpe.api.DeviceDetailsOption;
import com.cisco.provisioning.cpe.api.MACAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author rich
 */
@Stateless
@LocalBean
public class BACCProvisioner {
    
    // <editor-fold defaultstate="collapsed" desc="****** Class member variables ******">
    @Inject
    private Configurator cfgConfEJB;
    @Inject
    private Translations trnBN;
    private final String strThreadId;
    private PACEConnection pcnConn;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** Bean State Methods ******">
    public BACCProvisioner() {
        this.strThreadId = String.valueOf(Thread.currentThread().getId());
    }
    
    @PostConstruct
    public void BACCProvisionerC() {
        SimpleLogging.vLogEvent(this.strThreadId, "BACCProvisioner bean loading");
        SimpleLogging.vLogEvent(this.strThreadId, "****** BACCProvisioner bean initializing ******");
        SimpleLogging.vLogEvent(this.strThreadId, "Dev or production: " + this.cfgConfEJB.strGetMode());
        SimpleLogging.vLogEvent(this.strThreadId, "BACC host: " + this.cfgConfEJB.strGetBACCHostName());
        SimpleLogging.vLogEvent(this.strThreadId, "BACC port: " + this.cfgConfEJB.intGetBACCPort());
        SimpleLogging.vLogEvent(this.strThreadId, "BACC user: " + this.cfgConfEJB.strGetBACCUser());
        SimpleLogging.vLogEvent(this.strThreadId, "BACC password: " + this.cfgConfEJB.strGetBACCPassword());
        SimpleLogging.vLogEvent(this.strThreadId, "BACC alert email: " + this.cfgConfEJB.strGetBACCAlertEmail());
        // connect to BACC
        this.pcnConn = PACEConnectionFactory
            .getInstance(this.cfgConfEJB.strGetBACCHostName(),
                this.cfgConfEJB.intGetBACCPort(), this.cfgConfEJB.strGetBACCUser(),
                this.cfgConfEJB.strGetBACCPassword(), true);
        SimpleLogging.vLogEvent(this.strThreadId, "BACC connection successful: " + this.pcnConn.isAlive());
        SimpleLogging.vLogEvent(this.strThreadId, "****** BACCProvisioner bean initialized ******");
    }

    @PreDestroy
    public void BACCProvisionerD() {
        this.pcnConn.releaseConnection();
        this.pcnConn = null;
        SimpleLogging.vLogEvent(this.strThreadId, "BACCProvisioner bean destroyed");
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** BACC admin methods ******">
    public List lstFetchAllowedDeviceTypes() throws Exception {
        
        // <editor-fold defaultstate="collapsed" desc="****** Local variables ******">
        SimpleLogging.vLogEvent(this.strThreadId, "Fetching device types allowed in BACC");
        Batch btcBatch = null;
        BatchStatus btcStatus = null;
        CommandStatus cmsStatus = null;
        List lstResult = null;
        // </editor-fold>
        
        try {
            
            // <editor-fold defaultstate="collapsed" desc="****** Create batch, post ******">
            if(this.pcnConn.isAlive()) {
                btcBatch = this.pcnConn.newBatch(UUID.randomUUID().toString());
                btcBatch.getAllDeviceTypes();
            } else {
                throw new Exception("Connection to BACC died!");
            }
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="****** Post batch to BACC, check for errors ******">
            btcStatus = btcBatch.post(10000);
            if (btcStatus.isError()) {
                throw new Exception("BACC error: " + btcStatus.getCommandStatus(0).getErrorMessage());
            } else {
                lstResult = (List) btcStatus.getCommandStatus(0).getData();
            }
            // </editor-fold>
            
        } catch (Exception e) {
            SimpleLogging.vLogException(this.strThreadId, e);
            throw e;
        } finally {
            
        }
        return lstResult;   
    }
    
    public List lstFetchAllowedProperties() throws Exception {
        
        // <editor-fold defaultstate="collapsed" desc="****** Local variables ******">
        SimpleLogging.vLogEvent(this.strThreadId, "Fetching device types allowed in BACC");
        Batch btcBatch = null;
        BatchStatus btcStatus = null;
        CommandStatus cmsStatus = null;
        List lstResult = null;
        // </editor-fold>
        
        try {
            
            // <editor-fold defaultstate="collapsed" desc="****** Create batch, post ******">
            if(this.pcnConn.isAlive()) {
                btcBatch = this.pcnConn.newBatch(UUID.randomUUID().toString());
                btcBatch.getAllCustomPropertyDefinitions();
            } else {
                throw new Exception("Connection to BACC died!");
            }
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="****** Post batch to BACC, check for errors ******">
            btcStatus = btcBatch.post(10000);
            if (btcStatus.isError()) {
                throw new Exception("BACC error: " + btcStatus.getCommandStatus(0).getErrorMessage());
            } else {
                final Map<String, Object> mapResult = (Map) btcStatus.getCommandStatus(0).getData();
                for (String strKey : mapResult.keySet()) {
                    final Map<String, Object> mapProp = new HashMap();
                    mapProp.put(strKey, mapResult.get(strKey));
                    lstResult.add(mapProp);
                }
            }
            // </editor-fold>
            
        } catch (Exception e) {
            SimpleLogging.vLogException(this.strThreadId, e);
            throw e;
        } finally {
            
        }
        return lstResult;   
    }
    
    public List lstFetchAllowedDHCPCriterias() throws Exception {
        
        // <editor-fold defaultstate="collapsed" desc="****** Local variables ******">
        SimpleLogging.vLogEvent(this.strThreadId, "Fetching device types allowed in BACC");
        Batch btcBatch = null;
        BatchStatus btcStatus = null;
        CommandStatus cmsStatus = null;
        List lstResult = null;
        // </editor-fold>
        
        try {
            
            // <editor-fold defaultstate="collapsed" desc="****** Create batch, post ******">
            if(this.pcnConn.isAlive()) {
                btcBatch = this.pcnConn.newBatch(UUID.randomUUID().toString());
                btcBatch.getAllDHCPCriterias();
            } else {
                throw new Exception("Connection to BACC died!");
            }
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="****** Post batch to BACC, check for errors ******">
            btcStatus = btcBatch.post(10000);
            if (btcStatus.isError()) {
                throw new Exception("BACC error: " + btcStatus.getCommandStatus(0).getErrorMessage());
            } else {
                lstResult = (List) btcStatus.getCommandStatus(0).getData();
            }
            // </editor-fold>
            
        } catch (Exception e) {
            SimpleLogging.vLogException(this.strThreadId, e);
            throw e;
        } finally {
            
        }
        return lstResult;   
    }
    
    public Map lstFetchSystemProperties() throws Exception {
        
        // <editor-fold defaultstate="collapsed" desc="****** Local variables ******">
        SimpleLogging.vLogEvent(this.strThreadId, "Fetching device types allowed in BACC");
        Batch btcBatch = null;
        BatchStatus btcStatus = null;
        CommandStatus cmsStatus = null;
        Map mapResult = null;
        // </editor-fold>
        
        try {
            
            // <editor-fold defaultstate="collapsed" desc="****** Create batch, post ******">
            if(this.pcnConn.isAlive()) {
                btcBatch = this.pcnConn.newBatch(UUID.randomUUID().toString());
                btcBatch.getAllSystemPropertyDefinitions();
            } else {
                throw new Exception("Connection to BACC died!");
            }
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="****** Post batch to BACC, check for errors ******">
            btcStatus = btcBatch.post(10000);
            if (btcStatus.isError()) {
                throw new Exception("BACC error: " + btcStatus.getCommandStatus(0).getErrorMessage());
            } else {
                mapResult = (Map) btcStatus.getCommandStatus(0).getData();
            }
            // </editor-fold>
            
        } catch (Exception e) {
            SimpleLogging.vLogException(this.strThreadId, e);
            throw e;
        } finally {
            
        }
        return mapResult;   
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** Device fetch methods ******">
    
    public Map mapFetchModemByMac(String strMac) throws Exception {
        
        // <editor-fold defaultstate="collapsed" desc="****** Local variables ******">
        SimpleLogging.vLogEvent(this.strThreadId, "Received MAC: " + strMac);
        Batch btcBatch = null;
        BatchStatus btcStatus = null;
        CommandStatus cmsStatus = null;
        Map<String, Object> mapResult = null;
        final MACAddress macThis = new MACAddress(MiscUtils.convertMAC(strMac).get("bacc"));
        // </editor-fold>
        
        try {
            
            // <editor-fold defaultstate="collapsed" desc="****** Create batch, post ******">
            if(this.pcnConn.isAlive()) {
                btcBatch = this.pcnConn.newBatch(UUID.randomUUID().toString());
                List<DeviceDetailsOption> lstOptions = new ArrayList();
                lstOptions.add(DeviceDetailsOption.INCLUDE_LEASE_INFO);
                btcBatch.getDetails(macThis, new ArrayList());
            } else {
                throw new Exception("Connection to BACC died!");
            }
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="****** Post batch to BACC, check for errors ******">
            btcStatus = btcBatch.post(10000);
            if (btcStatus.isError()) {
                throw new Exception("BACC error: " + btcStatus.getCommandStatus(0).getErrorMessage());
            } else {
                mapResult = (Map) btcStatus.getCommandStatus(0).getData();
/*
                // flatten object; the DHCP information is returned in another call w/lease info
                mapResult.remove("/discoveredData/raw/dhcpv4");
                mapResult.remove("/discoveredData/dhcpv4");
                // take the properties out of the map, delete, and append to the wrapping map
                if (mapResult.containsKey("/properties")) {
                    Map<String, String> mapProps = (Map)mapResult.get("/properties");
                    SimpleLogging.vLogEvent(this.strThreadId, "Props: " + mapProps);
                    for (String strKey : mapProps.keySet()) {
                        mapResult.put(strKey, mapProps.get(strKey));
                    }
                    mapResult.remove("/properties");
                }
                // do the same with provisioning properties
                if (mapResult.containsKey("/provisioning/properties/selected")) {
                    Map<String, String> mapProps = (Map)mapResult.get("/provisioning/properties/selected");
                    SimpleLogging.vLogEvent(this.strThreadId, "Props: " + mapProps);
                    for (String strKey : mapProps.keySet()) {
                        mapResult.put(strKey, mapProps.get(strKey));
                    }
                    mapResult.remove("/provisioning/properties/selected");
                    mapResult.remove("/node");
                }
                
                SimpleLogging.vLogEvent(this.strThreadId, "Command count: " + btcStatus.getCommandCount());
                for (int x = 0; x < btcStatus.getCommandCount(); x++) {
                    SimpleLogging.vLogEvent(this.strThreadId, "Command[" + x + "]: "
                            + btcStatus.getCommandStatus(x).getData());
                }
        */
            }
            // </editor-fold>
            
        } catch (Exception e) {
            SimpleLogging.vLogException(this.strThreadId, e);
            throw e;
        } finally {
            
        }
        return mapResult;   
    }
    
    public Map mapFetchDHCPByMac(String strMac) throws Exception {
        
        // <editor-fold defaultstate="collapsed" desc="****** Local variables ******">
        SimpleLogging.vLogEvent(this.strThreadId, "Received MAC: " + strMac);
        Batch btcBatch = null;
        BatchStatus btcStatus = null;
        CommandStatus cmsStatus = null;
        Map mapResult = null;
        Map mapBACCResponse = null;
        final MACAddress macThis = new MACAddress(MiscUtils.convertMAC(strMac).get("bacc"));
        // </editor-fold>
        
        try {
            
            // <editor-fold defaultstate="collapsed" desc="****** Create batch, post ******">
            if(this.pcnConn.isAlive()) {
                btcBatch = this.pcnConn.newBatch(UUID.randomUUID().toString());
                List lstOptions = new ArrayList();
                lstOptions.add(DeviceDetailsOption.INCLUDE_LEASE_INFO);
                SimpleLogging.vLogEvent(this.strThreadId, "Sending request to BACC: " 
                        + btcBatch);
                btcBatch.getDetails(macThis, lstOptions);
            } else {
                throw new Exception("Connection to BACC died!");
            }
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="****** Post batch to BACC, check for errors ******">
            btcStatus = btcBatch.post(10000);
            if (btcStatus.isError()) {
                throw new Exception("BACC error: " + btcStatus.getCommandStatus(0).getErrorMessage());
            } else {
                mapBACCResponse = (Map) btcStatus.getCommandStatus(0).getData();
                SimpleLogging.vLogEvent(this.strThreadId, "Response from BACC: " 
                        + mapBACCResponse);
                mapResult = mapBACCResponse;
            }
            // </editor-fold>
            
        } catch (Exception e) {
            SimpleLogging.vLogException(this.strThreadId, e);
            throw e;
        } finally {
            
        }
        return mapResult;   
    }
    
    public List mapFetchCPEByMac(String strMac) throws Exception {
        
        // <editor-fold defaultstate="collapsed" desc="****** Local variables ******">
        SimpleLogging.vLogEvent(this.strThreadId, "Received MAC: " + strMac);
        Batch btcBatch = null;
        BatchStatus btcStatus = null;
        CommandStatus cmsStatus = null;
        List lstResult = null;
        final MACAddress macThis = new MACAddress(MiscUtils.convertMAC(strMac).get("bacc"));
        // </editor-fold>
        
        try {
            
            // <editor-fold defaultstate="collapsed" desc="****** Create batch, post ******">
            if(this.pcnConn.isAlive()) {
                btcBatch = this.pcnConn.newBatch(UUID.randomUUID().toString());
                List lstOptions = new ArrayList();
                btcBatch.getAllBehindDevice(macThis);
            } else {
                throw new Exception("Connection to BACC died!");
            }
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="****** Post batch to BACC, check for errors ******">
            btcStatus = btcBatch.post(10000);
            if (btcStatus.isError()) {
                throw new Exception("BACC error: " + btcStatus.getCommandStatus(0).getErrorMessage());
            } else {
                SimpleLogging.vLogEvent(this.strThreadId, "BACC returned "
                        + btcStatus.getCommandCount() + " results");
                lstResult = (List) btcStatus.getCommandStatus(0).getData();
            }
            // </editor-fold>
            
        } catch (Exception e) {
            SimpleLogging.vLogException(this.strThreadId, e);
            throw e;
        } finally {
            
        }
        return lstResult;   
    }
        
    // </editor-fold>
    
}
