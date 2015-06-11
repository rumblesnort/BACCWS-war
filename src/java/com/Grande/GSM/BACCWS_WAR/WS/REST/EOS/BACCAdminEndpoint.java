/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Grande.GSM.BACCWS_WAR.WS.REST.EOS;

import com.Grande.GSM.BACCWS_WAR.DataObjects.QueryResponse;
import com.Grande.GSM.BACCWS_WAR.Utilities.BACCProvisioner;
import com.Grande.GSM.BACCWS_WAR.Utilities.SimpleLogging;
import com.Grande.GSM.BACCWS_WAR.Utilities.Translations;
import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author rich
 */
@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class BACCAdminEndpoint {

    // <editor-fold defaultstate="collapsed" desc="****** Class member vars ******">
    @Inject
    private Translations trnBN;
    @Inject
    private BACCProvisioner bacEJB;
    private final String strThreadId;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="****** Bean State Methods ******">
    public BACCAdminEndpoint() {
        this.strThreadId = String.valueOf(Thread.currentThread().getId());
        SimpleLogging.vLogEvent(this.strThreadId, "Constructor called");
    }
    // </editor-fold>

    @Path("/AllowedDevices")
    @GET
    public String fetchAllowedDevices() {
        
        // <editor-fold defaultstate="collapsed" desc="****** Method vars ******">
        final Stopwatch timer = new Stopwatch();
        final QueryResponse qRes = new QueryResponse();
        String strResponse = null;
        List lstResult  = null;
        // start the execution timer
        timer.start();
        // </editor-fold>
        
        try {
            
            qRes.vSetNode(java.net.InetAddress.getLocalHost().getHostName());
            lstResult = this.bacEJB.lstFetchAllowedDeviceTypes();
            qRes.vSetSuccessFlag(true);
            qRes.vSquashResult(lstResult);
            
        } catch (Exception e) {
            
            // <editor-fold defaultstate="collapsed" desc="****** Handle failures ******">
            qRes.vSetSuccessFlag(false);
            // handle NPE differently since getMessage() is null
            if (e instanceof NullPointerException) {
                qRes.vSetMessage("NPE occured when serializing result to JSON! " 
                    + "File: " + e.getStackTrace()[0].getFileName() + ", "
                    + "Method: " + e.getStackTrace()[0].getMethodName() + ", "
                    + "Line: " + e.getStackTrace()[0].getLineNumber());
            } else {
                qRes.vSetMessage(e.getMessage());
            }
            SimpleLogging.vLogException(this.strThreadId, e);
            // </editor-fold>
            
        } finally {
            
            // <editor-fold defaultstate="collapsed" desc="****** Stop timer, convert response to JSON ******">
            timer.stop();
            qRes.vSetRoundTrip(String.valueOf(timer.elapsedTime(TimeUnit.SECONDS)) 
                + "." + String.valueOf(timer.elapsedTime(TimeUnit.MILLISECONDS)));
            strResponse = this.trnBN.strQueryResponseToJSON(qRes);
            SimpleLogging.vLogEvent(this.strThreadId 
                    + "|" + qRes.strGetRoundTripInSeconds() + "s", 
                "retrieved " + qRes.intGetDataCount() + " records");
            // </editor-fold>
            
        }
        return strResponse;
        
    }
    
    @Path("/AllowedProperties")
    @GET
    public String fetchAllowedProperties() {
        
        // <editor-fold defaultstate="collapsed" desc="****** Method vars ******">
        final Stopwatch timer = new Stopwatch();
        final QueryResponse qRes = new QueryResponse();
        String strResponse = null;
        List lstResult = null;
        // start the execution timer
        timer.start();
        // </editor-fold>
        
        try {
            
            qRes.vSetNode(java.net.InetAddress.getLocalHost().getHostName());
            lstResult = this.bacEJB.lstFetchAllowedProperties();
            qRes.vSetSuccessFlag(true);
            qRes.vSquashResult(lstResult);
            
        } catch (Exception e) {
            
            // <editor-fold defaultstate="collapsed" desc="****** Handle failures ******">
            qRes.vSetSuccessFlag(false);
            // handle NPE differently since getMessage() is null
            if (e instanceof NullPointerException) {
                qRes.vSetMessage("NPE occured when serializing result to JSON! " 
                    + "File: " + e.getStackTrace()[0].getFileName() + ", "
                    + "Method: " + e.getStackTrace()[0].getMethodName() + ", "
                    + "Line: " + e.getStackTrace()[0].getLineNumber());
            } else {
                qRes.vSetMessage(e.getMessage());
            }
            SimpleLogging.vLogException(this.strThreadId, e);
            // </editor-fold>
            
        } finally {
            
            // <editor-fold defaultstate="collapsed" desc="****** Stop timer, convert response to JSON ******">
            timer.stop();
            qRes.vSetRoundTrip(String.valueOf(timer.elapsedTime(TimeUnit.SECONDS)) 
                + "." + String.valueOf(timer.elapsedTime(TimeUnit.MILLISECONDS)));
            strResponse = this.trnBN.strQueryResponseToJSON(qRes);
            SimpleLogging.vLogEvent(this.strThreadId 
                    + "|" + qRes.strGetRoundTripInSeconds() + "s", 
                "retrieved " + qRes.intGetDataCount() + " records");
            // </editor-fold>
            
        }
        return strResponse;
    }
    
    @Path("/AllowedDHCPCriterias")
    @GET
    public String fetchAllowedDHCPCriterias() {
        
        // <editor-fold defaultstate="collapsed" desc="****** Method vars ******">
        final Stopwatch timer = new Stopwatch();
        final QueryResponse qRes = new QueryResponse();
        String strResponse = null;
        List lstResult  = null;
        // start the execution timer
        timer.start();
        // </editor-fold>
        
        try {
            
            qRes.vSetNode(java.net.InetAddress.getLocalHost().getHostName());
            lstResult = this.bacEJB.lstFetchAllowedDHCPCriterias();
            qRes.vSetSuccessFlag(true);
            qRes.vSquashResult(lstResult);
            
        } catch (Exception e) {
            
            // <editor-fold defaultstate="collapsed" desc="****** Handle failures ******">
            qRes.vSetSuccessFlag(false);
            // handle NPE differently since getMessage() is null
            if (e instanceof NullPointerException) {
                qRes.vSetMessage("NPE occured when serializing result to JSON! " 
                    + "File: " + e.getStackTrace()[0].getFileName() + ", "
                    + "Method: " + e.getStackTrace()[0].getMethodName() + ", "
                    + "Line: " + e.getStackTrace()[0].getLineNumber());
            } else {
                qRes.vSetMessage(e.getMessage());
            }
            SimpleLogging.vLogException(this.strThreadId, e);
            // </editor-fold>
            
        } finally {
            
            // <editor-fold defaultstate="collapsed" desc="****** Stop timer, convert response to JSON ******">
            timer.stop();
            qRes.vSetRoundTrip(String.valueOf(timer.elapsedTime(TimeUnit.SECONDS)) 
                + "." + String.valueOf(timer.elapsedTime(TimeUnit.MILLISECONDS)));
            strResponse = this.trnBN.strQueryResponseToJSON(qRes);
            SimpleLogging.vLogEvent(this.strThreadId 
                    + "|" + qRes.strGetRoundTripInSeconds() + "s", 
                "retrieved " + qRes.intGetDataCount() + " records");
            // </editor-fold>
            
        }
        return strResponse;
    }
    
    @Path("/SystemProperties")
    @GET
    public String fetchSystemProperties() {
        
        // <editor-fold defaultstate="collapsed" desc="****** Method vars ******">
        final Stopwatch timer = new Stopwatch();
        final QueryResponse qRes = new QueryResponse();
        String strResponse = null;
        Map mapResult  = null;
        // start the execution timer
        timer.start();
        // </editor-fold>
        
        try {
            
            qRes.vSetNode(java.net.InetAddress.getLocalHost().getHostName());
            mapResult = this.bacEJB.lstFetchSystemProperties();
            qRes.vSetSuccessFlag(true);
            qRes.vAddResult(mapResult);
            
        } catch (Exception e) {
            
            // <editor-fold defaultstate="collapsed" desc="****** Handle failures ******">
            qRes.vSetSuccessFlag(false);
            // handle NPE differently since getMessage() is null
            if (e instanceof NullPointerException) {
                qRes.vSetMessage("NPE occured when serializing result to JSON! " 
                    + "File: " + e.getStackTrace()[0].getFileName() + ", "
                    + "Method: " + e.getStackTrace()[0].getMethodName() + ", "
                    + "Line: " + e.getStackTrace()[0].getLineNumber());
            } else {
                qRes.vSetMessage(e.getMessage());
            }
            SimpleLogging.vLogException(this.strThreadId, e);
            // </editor-fold>
            
        } finally {
            
            // <editor-fold defaultstate="collapsed" desc="****** Stop timer, convert response to JSON ******">
            timer.stop();
            qRes.vSetRoundTrip(String.valueOf(timer.elapsedTime(TimeUnit.SECONDS)) 
                + "." + String.valueOf(timer.elapsedTime(TimeUnit.MILLISECONDS)));
            strResponse = this.trnBN.strQueryResponseToJSON(qRes);
            SimpleLogging.vLogEvent(this.strThreadId 
                    + "|" + qRes.strGetRoundTripInSeconds() + "s", 
                "retrieved " + qRes.intGetDataCount() + " records");
            // </editor-fold>
            
        }
        return strResponse;
    }
    
    
        
}
