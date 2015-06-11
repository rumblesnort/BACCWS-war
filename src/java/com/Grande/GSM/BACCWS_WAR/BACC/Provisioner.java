package com.Grande.GSM.BACCWS_WAR.BACC;

import com.Grande.GSM.BACCWS_WAR.Utilities.SimpleLogging;
import com.cisco.provisioning.cpe.ActivationMode;
import com.cisco.provisioning.cpe.AlreadyPostedException;
import com.cisco.provisioning.cpe.Batch;
import com.cisco.provisioning.cpe.BatchStatus;
import com.cisco.provisioning.cpe.CommandStatus;
import com.cisco.provisioning.cpe.ConfirmationMode;
import com.cisco.provisioning.cpe.PACEConnection;
import com.cisco.provisioning.cpe.PACEConnectionFactory;
import com.cisco.provisioning.cpe.ProvisioningException;
import com.cisco.provisioning.cpe.PublishingMode;
import com.cisco.provisioning.cpe.api.DeviceDetailsOption;
import com.cisco.provisioning.cpe.api.DeviceID;
import com.cisco.provisioning.cpe.api.DeviceOperation;
import com.cisco.provisioning.cpe.api.DeviceType;
import com.cisco.provisioning.cpe.api.Key;
import com.cisco.provisioning.cpe.api.KeyType;
import com.cisco.provisioning.cpe.api.MACAddress;
import com.cisco.provisioning.cpe.api.SearchBookmark;
import com.cisco.provisioning.cpe.api.search.DeviceSearchType;
import com.cisco.provisioning.cpe.api.search.RecordData;
import com.cisco.provisioning.cpe.api.search.RecordSearchResults;
import com.cisco.provisioning.cpe.api.search.ReturnParameters;
import com.cisco.provisioning.cpe.constants.DeviceDetailsKeys;
import com.cisco.provisioning.cpe.constants.DeviceTypeValues;
import com.cisco.provisioning.cpe.internal.client.LeaseResultsImpl;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remove;
import javax.ejb.Stateless;
/**
 *
 * @author rich
 */
@Stateless
@LocalBean
public class Provisioner  {
    
    // <editor-fold defaultstate="collapsed" desc="****** Class annotations ******">
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** Class member objects ******">
    PACEConnection pcnConn = null;
    private long lngThreadID = 0;
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="****** Bean state methods ******">
    @PostConstruct
    private void ProvisionerC() {
        try {
            /*
            Thread thdData = Thread.currentThread();
            this.lngThreadID = thdData.getId();
            SimpleLogging.vLogEvent("Created EJB, threadID: " + this.lngThreadID);
            SimpleLogging.vLogEvent("(" + this.lngThreadID + ") atempting connction to " +
                    cnfThisEJB.strGetBACCUser() + "/" + cnfThisEJB.strGetBACCPassword() + 
                    "@" + cnfThisEJB.strGetBACCHostName() +
                    ":" + cnfThisEJB.strGetBACCPassword());
            this.pcnConn = PACEConnectionFactory.getInstance(cnfThisEJB.strGetBACCHostName(),
               cnfThisEJB.intGetBACCPort(), cnfThisEJB.strGetBACCUser(),
               cnfThisEJB.strGetBACCPassword(), true);
            this.pcnConn.openConnection();
            SimpleLogging.vLogEvent("(" + this.lngThreadID + ") connection to BACC created successfully");
            */
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter traceWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(traceWriter, false);
            e.printStackTrace(printWriter);
            printWriter.close();
            String strTrace = traceWriter.getBuffer().toString();
            SimpleLogging.vLogEvent("(" + this.lngThreadID + ") could not create connection " +
                    "to BACC, stacktrace: \n***EXCEPTION***\n" + strTrace + "\n***EXCEPTION***\n");
        }
    }
    
    @PreDestroy
    private void ProvisionerD() {
        SimpleLogging.vLogEvent("(" + this.lngThreadID + ") destroying EJB");
        if (this.pcnConn.isAlive()) {
            SimpleLogging.vLogEvent("(" + this.lngThreadID + ") PACEConnection is still alive!  Closing..");
            this.pcnConn.releaseConnection();
        }
        this.pcnConn = null;
        SimpleLogging.vLogEvent("(" + this.lngThreadID + ") PACEConnection nulled");
    }
    @Remove
    public void vProvisionerR() {
        SimpleLogging.vLogEvent("(" + this.lngThreadID + ") EJB removed from pool");
    }
    // </editor-fold>
    
}
