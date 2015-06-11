/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Grande.GSM.BACCWS_WAR.WS.REST.EOS;

import com.Grande.GSM.BACCWS_WAR.DataObjects.Database.FirmwareDefinition;
import com.Grande.GSM.BACCWS_WAR.DataObjects.QueryResponse;
import com.Grande.GSM.BACCWS_WAR.DataObjects.SenchaFilter;
import com.Grande.GSM.BACCWS_WAR.Utilities.SimpleLogging;
import com.Grande.GSM.BACCWS_WAR.Utilities.Translations;
import com.google.common.base.Stopwatch;
import java.util.List;
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
public class FirmwareDefinitionsEndpoint {

    // <editor-fold defaultstate="collapsed" desc="****** Class member vars ******">
    @Inject
    private Translations trnBN;
    private final String strThreadId;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="****** Bean State Methods ******">
    public FirmwareDefinitionsEndpoint() {
        this.strThreadId = String.valueOf(Thread.currentThread().getId());
        SimpleLogging.vLogEvent(this.strThreadId, "Constructor called");
    }
    // </editor-fold>

    @GET
    public String fetchFirmwareDefinitionsByMakeModel(@QueryParam("make") final String strMake, 
            @QueryParam("model") final String strModel) {
        
        // <editor-fold defaultstate="collapsed" desc="****** Method vars ******">
        final Stopwatch timer = new Stopwatch();
        final QueryResponse qRes = new QueryResponse();
        String strResponse = null;
        FirmwareDefinition fdThis = null;
        // start the execution timer
        timer.start();
        // </editor-fold>

        try {
            
            qRes.vSetNode(java.net.InetAddress.getLocalHost().getHostName());
            
            fdThis = this.trnBN
                    .pGetFirmwareDefinitionByMakeAndModel(strMake, strModel)
                    .orElseThrow(() -> new Exception("Make/model \"" + strMake 
                            + "/" + strModel + "\" not found in the firmware table"));
            qRes.vSetSuccessFlag(true);
            qRes.vAddResult(fdThis);
            
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
    
    public String fetchFirmwareDefinitions(@QueryParam("filter") String strFilter) {

        // <editor-fold defaultstate="collapsed" desc="****** Method vars ******">
        final Stopwatch timer = new Stopwatch();
        final QueryResponse qRes = new QueryResponse();
        String strResponse = null;
        String strMake = null;
        String strModel = null;
        List<SenchaFilter> lstFilters = null;
        List<FirmwareDefinition> lstFirmwareDefs = null;
        // start the execution timer
        timer.start();
        // </editor-fold>

        try {
            qRes.vSetNode(java.net.InetAddress.getLocalHost().getHostName());
            
            // <editor-fold defaultstate="collapsed" desc="****** No filters (return all records) ******">
            if (strFilter == null || strFilter.equals("")) {
                SimpleLogging.vLogEvent(this.strThreadId, "No filter detected, fetching all definitions");
                lstFirmwareDefs = this.trnBN.lstGetFirmwareDefinitions();
                SimpleLogging.vLogEvent(this.strThreadId, "Returning " + lstFirmwareDefs.size() + " definitions");
            // </editor-fold>
                
            // <editor-fold defaultstate="collapsed" desc="****** Evaluate/apply Sencha filters ******">
            } else {

                // <editor-fold defaultstate="collapsed" desc="****** Filter extraction logic ******">
                // Deserialize the filters
                SimpleLogging.vLogEvent(this.strThreadId, "Processing filter JSON: " + strFilter);
                lstFilters = this.trnBN.lstDeserializeSenchaFilter(strFilter);
                // extract filters that contain 'make' and 'model'
                //lstFilters = this.trnBN.lstFilterSenchaFiltersByStrings(lstFilters, "make", "model");
                SimpleLogging.vLogEvent(this.strThreadId, "Extracted " + lstFilters.size() 
                        + " make/model filters: " + lstFilters);
                // </editor-fold>
            
                // <editor-fold defaultstate="collapsed" desc="****** Filter handling logic ******">
                lstFirmwareDefs = this.trnBN.lstGetFirmwareDefinitionByFilter(lstFilters);
                SimpleLogging.vLogEvent(this.strThreadId, "Returning " + lstFirmwareDefs.size() + " definitions");
                // </editor-fold>
            }
            qRes.vAddResult(lstFirmwareDefs.toArray());
            for (int x = 0; x < lstFirmwareDefs.size(); x++) {
                    qRes.vAddResult(lstFirmwareDefs.get(x));
            }
            qRes.vSetSuccessFlag(true);
            // </editor-fold>
            
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
