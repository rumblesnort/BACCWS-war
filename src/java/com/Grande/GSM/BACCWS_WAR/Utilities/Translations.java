/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Grande.GSM.BACCWS_WAR.Utilities;

import com.Grande.GSM.BACCWS_WAR.DataObjects.Database.FirmwareDefinition;
import com.Grande.GSM.BACCWS_WAR.DataObjects.QueryResponse;
import com.Grande.GSM.BACCWS_WAR.DataObjects.SenchaFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.rowset.CachedRowSetImpl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.serial.SerialBlob;

/**
 *
 * @author rich
 */
@ApplicationScoped
public class Translations {
    
    // <editor-fold defaultstate="collapsed" desc="****** Class member variables ******">
    @Inject
    private Configurator cfgConfEJB;
    private final HashMap<Object, List> mapDataCache;
    private final List<FirmwareDefinition> lstFirmwareDefs;
    private final String strThreadId;
    private Boolean blnUseCaches;
    private Boolean blnRefreshCaches;
    private final ObjectMapper jsonSerializer;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** Bean State Methods ******">
    public Translations() {
        this.strThreadId = String.valueOf(Thread.currentThread().getId());
        this.mapDataCache = new HashMap();
        this.lstFirmwareDefs = new ArrayList();
        this.blnUseCaches = false;  // default on app startup is to cache
        this.blnRefreshCaches = true; // default on app startup is to cache
        this.jsonSerializer = new ObjectMapper();
        this.jsonSerializer.configure(SerializationFeature.INDENT_OUTPUT, false);
        this.jsonSerializer.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.jsonSerializer.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }
    
    @PostConstruct
    public void TranslationsC() {
        SimpleLogging.vLogEvent(this.strThreadId, "Translations bean loading");
        SimpleLogging.vLogEvent(this.strThreadId, "****** Translations bean initializing ******");
        SimpleLogging.vLogEvent(this.strThreadId, "Result set caching: " + this.blnUseCaches);
        SimpleLogging.vLogEvent(this.strThreadId, "Auto-refresh caches: " + this.blnRefreshCaches);
        if (this.blnUseCaches) {
            SimpleLogging.vLogEvent(this.strThreadId, "* Pre-loading caches");
            this.vReloadCaches();
        } else {
            this.vReloadCaches();
            SimpleLogging.vLogEvent(this.strThreadId, "* No caches will be pre-loaded");
        }
        SimpleLogging.vLogEvent(this.strThreadId, "****** Translations bean initialized ******");
    }

    @PreDestroy
    public void TranslationsD() {
        SimpleLogging.vLogEvent(this.strThreadId, "Translations bean destroyed");
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** Temporal/getter/setter/convenience methods ******">
    private void vReloadCaches() {
        if (!this.blnRefreshCaches) {
        
            // <editor-fold defaultstate="collapsed" desc="****** Don't reload firmware defs ******">
            SimpleLogging.vLogEvent(this.strThreadId, "Cache map reloading is DISABLED, skipping");
            // </editor-fold>
        
        } else {
            
            // <editor-fold defaultstate="collapsed" desc="****** Reload firmware defs ******">
            final String strSQL = "SELECT * FROM HSD.T_PROV_BACC_FIRMWARE";
            try (CachedRowSet crs = new CachedRowSetImpl()) {
                crs.populate(this.execAnyPreparedSQL(strSQL));
                synchronized(this) {
                    this.lstFirmwareDefs.clear();
                    while (crs.next()) {
                        FirmwareDefinition fdThis = new FirmwareDefinition();
                        fdThis.intId = crs.getInt("firmware_id");
                        fdThis.strMake = crs.getString("make");
                        fdThis.strModel = crs.getString("model");
                        fdThis.intDocsisLevel = crs.getInt("docsis");
                        fdThis.intMaxPorts = crs.getInt("maxMTAPorts");
                        fdThis.strResidentialCOS = crs.getString("resiCoS");
                        fdThis.strCommercialCOS = crs.getString("commCoS");
                        fdThis.strDisabledCOS = crs.getString("disabledCoS");
                        fdThis.strMTACOS = crs.getString("mtaCoS");
                        fdThis.intIPV6 = crs.getInt("ipv6");
                        fdThis.intUpgradeProtocol = crs.getInt("UpgradeProtocol");
                        fdThis.strBSDdeviceName = crs.getString("BSdeviceName");
                        fdThis.strBSDeviceType = crs.getString("BSdeviceType");
                        fdThis.strBSDDeviceLevel = crs.getString("BSdeviceLevel");
                        this.lstFirmwareDefs.add(fdThis);
                    }
                }
            } catch (Exception e) {
                SimpleLogging.vLogException(this.strThreadId, e);
            } finally {
                SimpleLogging.vLogEvent(this.strThreadId, "Loaded " + this.lstFirmwareDefs.size()
                        + " firmware definition objects");
            }
            // </editor-fold>
            
        }
    }
    
    public List<SenchaFilter> lstFilterSenchaFiltersByStrings(final List<SenchaFilter> lstFilters,
            final String... strKeys) throws Exception {
        
        SimpleLogging.vLogEvent(this.strThreadId, "Received " + lstFilters.size() 
                + " filters: " + lstFilters);
        List lstReturn = null;
        // Check if the keys already have a cached resultset
        if (this.blnUseCaches && this.mapDataCache.containsKey(Arrays.asList(strKeys).toString())) {
            SimpleLogging.vLogEvent(this.strThreadId, "Cached copy found!");
            lstReturn = this.mapDataCache.get(Arrays.asList(strKeys).toString());
        } else {
            // List to contain the predicates
            final List<Predicate<SenchaFilter>> lstPredicates = new ArrayList();
            
            SimpleLogging.vLogEvent(this.strThreadId, "Received " + lstFilters.size() 
                    + " Sencha filters, filtering for the following keywords: " 
                    + Arrays.asList(strKeys));

            // build the predicate list off of "if the SenchaFilter::strProperty equals strKey"
            for (final String strKey : strKeys) {
                SimpleLogging.vLogEvent(this.strThreadId, "Adding filter: " + strKey);
                lstPredicates.add(p->p.strProperty.equalsIgnoreCase(strKey));
            }

            // build one predicate off of the predicate list - note this is joined together by
            // OR statements: "key1 OR key2 OR key3.." 
            final Predicate<SenchaFilter> prdComposite = lstPredicates.parallelStream()
                    .reduce(Predicate::and).orElse(t->true);

            // return a list of SenchaFilters that match the keys.  Note the distinct() keyword 
            // removes duplicates
            lstReturn = lstFilters
                    .parallelStream()
                    .unordered()
                    .filter(prdComposite)
                    .collect(Collectors.toList());
            if (this.blnUseCaches) {
                this.mapDataCache.put(Arrays.asList(strKeys).toString(), lstReturn);
            }
        }
        SimpleLogging.vLogEvent(this.strThreadId, "Found " + lstReturn.size() + " de-duplicated filters matching "
                + " the keywords " + Arrays.asList(strKeys));
        SimpleLogging.vLogEvent(this.strThreadId, "Cache: " + this.mapDataCache);
        return lstReturn;
    }
    
    private FirmwareDefinition fdFetchFirmwareDefByMakeModel(final String strMake, 
            final String strModel) throws Exception {
        
        SimpleLogging.vLogEvent(this.strThreadId, "Searching for " + strMake + "/" + strModel
                + " firmware definition");
        FirmwareDefinition fdThis = null;
            
        // check if caching is enabled; if yes use the list at class member; if not go SQL
        if (this.blnUseCaches) {
                
            // <editor-fold defaultstate="collapsed" desc="****** Handle via cache ******">
            // binary search through the firmware defs list
            List<FirmwareDefinition> lstDefs = this.lstFirmwareDefs
                    .stream()
                    .filter((p)-> p.strMake.equals(strMake))
                    .filter((p)-> p.strModel.equals(strModel))
                    .collect(Collectors.toList());
            SimpleLogging.vLogEvent(this.strThreadId, "found " + lstDefs.size()
                + " match(es)");
            // if the list is empty throw exception as the definitions weren't found
            if (lstDefs == null || lstDefs.isEmpty()) {
                throw new Exception(strMake + "/" + strModel 
                    + " not found in firmware table!");
            } else {
                // if the list has more than one result there are either dupe rows or the code is bad
                if (lstDefs.size() > 1) {
                    throw new Exception(strMake + "/" + strModel
                        + " returned more than 1 row");
                // successful result
                } else {
                    fdThis = lstDefs.get(0);
                    SimpleLogging.vLogEvent(this.strThreadId, "found match: " + fdThis);
                }
            }
            //</editor-fold>

        } else {

            // <editor-fold defaultstate="collapsed" desc="****** Handle via direct query ******">
            final String strSQL = "SELECT * FROM HSD.T_PROV_BACC_FIRMWARE " 
                    + "WHERE make = ? AND model = ?";
            try (CachedRowSet crs = this.execAnyPreparedSQL(strSQL);) {
                while (crs.next()) {
                    fdThis = new FirmwareDefinition();
                    fdThis.intId = crs.getInt("firmware_id");
                    fdThis.strMake = crs.getString("make");
                    fdThis.strModel = crs.getString("model");
                    fdThis.intDocsisLevel = crs.getInt("docsis");
                    fdThis.intMaxPorts = crs.getInt("maxMTAPorts");
                    fdThis.strResidentialCOS = crs.getString("resiCoS");
                    fdThis.strCommercialCOS = crs.getString("commCoS");
                    fdThis.strDisabledCOS = crs.getString("disabledCoS");
                    fdThis.strMTACOS = crs.getString("mtaCoS");
                    fdThis.intIPV6 = crs.getInt("ipv6");
                    fdThis.intUpgradeProtocol = crs.getInt("UpgradeProtocol");
                    fdThis.strBSDdeviceName = crs.getString("BSdeviceName");
                    fdThis.strBSDeviceType = crs.getString("BSdeviceType");
                    fdThis.strBSDDeviceLevel = crs.getString("BSdeviceLevel");
                    SimpleLogging.vLogEvent(this.strThreadId, 
                        "Found " + strMake + "/" + strModel + " firmware definition: " + fdThis);
                }
            } catch (Exception e) {
                throw e;
            }
            //</editor-fold>

        }
        return fdThis;
    }
    
    public void vDisableResultCacheReloading() {
        this.blnRefreshCaches = false;
        SimpleLogging.vLogEvent(this.strThreadId, "Resultset cache reloading is DISABLED");
    }
    
    public void vEnableResultCacheReloading() {
        this.blnRefreshCaches = true;
        SimpleLogging.vLogEvent(this.strThreadId, "Resultset cache reloading is ENABLED");
    }
    
    public void vDisableResultCaching() {
        this.blnUseCaches = false;
        SimpleLogging.vLogEvent(this.strThreadId, "Resultset caching is ENABLED");
    }
    
    public void vEnableResultCaching() {
        this.blnUseCaches = true;
        SimpleLogging.vLogEvent(this.strThreadId, "Resultset caching is DISABLED");
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** JSON/XML serialization methods ******">
    public String strQueryResponseToJSON(final QueryResponse qRes) {
        SimpleLogging.vLogEvent(this.strThreadId, "Converting queryResponse to JSON: " + qRes);
        String strJSON = null;
        
        try {
            strJSON = this.jsonSerializer.writeValueAsString(qRes);
        
        // <editor-fold defaultstate="collapsed" desc="****** Handle serialization errors ******">
        } catch (JsonProcessingException jpe) {
            SimpleLogging.vLogException(this.strThreadId, jpe);
            strJSON = "{ "
                    + "success: false,"
                    + "resultCount: 1,"
                    + "requestStamp: \"" + qRes.strGetRequestStamp() + "\","
                    + "roundTripInSeconds: \"" + qRes.strGetRoundTripInSeconds() + "\","
                    + "message: Exception serializing result to JSON: " + jpe.getMessage() + "\"," 
                    + "node: \"" + qRes.strGetNode() + "\","
                    + "resultset: []"
                    + "}";
        // </editor-fold>
            
        // <editor-fold defaultstate="collapsed" desc="****** Handle NPE (in this method!) ******">    
        } catch (NullPointerException npe) {
            SimpleLogging.vLogException(this.strThreadId, npe);
            strJSON = "{ "
                    + "success: false,"
                    + "resultCount: 1,"
                    + "requestStamp: \"" + qRes.strGetRequestStamp() + "\","
                    + "roundTripInSeconds: \"" + qRes.strGetRoundTripInSeconds() + "\","
                    + "message: NPE occured when serializing result to JSON! " 
                        + "File: " + npe.getStackTrace()[0].getFileName() + ", "
                        + "Method: " + npe.getStackTrace()[0].getMethodName() + ", "
                        + "Line: " + npe.getStackTrace()[0].getLineNumber() 
                        + "\"," 
                    + "node: \"" + qRes.strGetNode() + "\","
                    + "resultset: []"
                    + "}";
        // </editor-fold>
            
        // <editor-fold defaultstate="collapsed" desc="****** Handle generic errors ******">     
        } catch (Exception e) {
            SimpleLogging.vLogException(this.strThreadId, e);
            strJSON = "{ "
                    + "success: false,"
                    + "resultCount: 1,"
                    + "requestStamp: \"" + qRes.strGetRequestStamp() + "\","
                    + "roundTripInSeconds: \"" + qRes.strGetRoundTripInSeconds() + "\","
                    + "message: Generic exception occured when serializing result to JSON: "
                        + e.getMessage()
                        + "\","
                    + "node: \"" + qRes.strGetNode() + "\","
                    + "resultset: []"
                    + "}";
        }
        // </editor-fold>
        
        //SimpleLogging.vLogEvent(this.strThreadId, "Returning: " + strJSON);
        return strJSON;
    }
    
    public List<SenchaFilter> lstDeserializeSenchaFilter(final String strJSON) throws Exception {
        SimpleLogging.vLogEvent(this.strThreadId, "Converting JSON to a list of Senchafilters: " + strJSON);
        final List<SenchaFilter> lstResponse = this.jsonSerializer.readValue(strJSON, new TypeReference<List<SenchaFilter>>(){});
        return lstResponse;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="****** SQL execution methods (MSSQL/APP) ******">
    private Integer execAnyPreparedUpdate(Boolean blnGetKey, String strSQL, Object... objBind) throws Exception {
        
        UUID uuidThis = UUID.randomUUID();
        String strPrefix = String.valueOf(Thread.currentThread().getId());
        SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "received " + strSQL);
        Integer intRowsChanged = 0;
        // key for logging
        
        
        try (Connection con = DriverManager.getConnection(this.cfgConfEJB.strGetMSSQLAPPURL());
            PreparedStatement pstmt = con.prepareStatement(strSQL, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            // convert the object array to bind variables
            for (int x = 0; x < objBind.length; x++) {
                String strBindVar = String.valueOf(objBind[x]);
                if (objBind[x] instanceof String) {
                    pstmt.setString((x + 1), strBindVar);
                    SimpleLogging.vLogEvent(strPrefix, "found String for bindvar("
                            + (x + 1) + "): " + strBindVar);
                } else if (objBind[x] instanceof Integer) {
                    pstmt.setInt((x + 1), (int) objBind[x]);
                    SimpleLogging.vLogEvent(strPrefix, "found Integer for bindvar("
                            + (x + 1) + "): " + Integer.parseInt(strBindVar));
                } else if (objBind[x] instanceof Long) {
                    pstmt.setLong(x + 1, Long.parseLong(strBindVar));
                    SimpleLogging.vLogEvent(strPrefix, "found Long for bindvar("
                            + (x + 1) + "): " + Long.parseLong(strBindVar));
                } else if (objBind[x] instanceof Float) {
                    pstmt.setFloat(x + 1, Float.parseFloat(strBindVar));
                    SimpleLogging.vLogEvent(strPrefix, "found Float for bindvar("
                            + (x + 1) + "): " + Float.parseFloat(strBindVar));
                } else if (objBind[x] instanceof Date) {
                    pstmt.setDate(x + 1, (java.sql.Date)objBind[x]);
                    SimpleLogging.vLogEvent(strPrefix, "found Date for bindvar("
                            + (x + 1) + "): " + java.sql.Date.parse(strBindVar));
                } else if (objBind[x] instanceof byte[]) {
                    SerialBlob serBlob = new SerialBlob((byte[]) objBind[x]);
                    pstmt.setBlob(x + 1, serBlob);
                    SimpleLogging.vLogEvent(strPrefix, "found BLOB for bindvar("
                            + (x + 1) + "): " + serBlob.toString());
                } else {
                    throw new Exception("Unsupported bind variable (" + x + ") type in prepared statement: " 
                            + objBind[x].getClass().getName());
                }
            }
            // generate a list of K->V for the debug log
            /*Map mapBindVars = new HashMap<String, Object>();
            for (int x = 1; x < pstmt.getParameterMetaData().getParameterCount(); x++) {
                mapBindVars.put(pstmt.getParameterMetaData().getParameterTypeName(x), 
                        pstmt.getParameterMetaData().getParameterType(x));
            }
            // send K->V to the debug log
            SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "successfully converted object list to " 
                    + pstmt.getParameterMetaData().getParameterCount()
                    + " parameter(s): " + mapBindVars);
            */
            // determine if we're changing data or fetching data
            if ((strSQL.toLowerCase().indexOf("update") > -1) || 
                (strSQL.toLowerCase().indexOf("insert") > -1) ||
                (strSQL.toLowerCase().indexOf("delete") > -1)) {
                // we are changing data, use the update method 
                
                intRowsChanged = pstmt.executeUpdate();
                /**
                 * INSERT/UPDATE will return a generated key if there is an IDENTITY
                 * column.  This is what we will return to the calling method.  If
                 * there is no IDENTITY column then 0 (default) will be returned.
                 * The calling method can decide whether to discard this value or use it.
                 * If this is a DELETE it will automatically return the number of rows
                 * deleted, which is the default behavior on getResultSet().
                 */
                if (strSQL.toLowerCase().indexOf("delete") > -1) {
                    SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "deleted " + intRowsChanged
                            + " rows");
                } else if (strSQL.toLowerCase().indexOf("update") > -1) {
                    SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "updated " + intRowsChanged
                            + " rows");
                } else if (strSQL.toLowerCase().indexOf("insert") > -1) {
                    Integer intGeneratedKey = 0;
                    while(pstmt.getGeneratedKeys().next()) {
                        intGeneratedKey = pstmt.getGeneratedKeys().getInt(1);
                        SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "key: " + pstmt.getGeneratedKeys().getInt(1));
                    }
                    SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "inserted " + intRowsChanged
                            + " rows, generated key: " + intGeneratedKey);
                    if (blnGetKey) {
                        intRowsChanged = intGeneratedKey;
                    }
                } else {
                    throw new Exception("It is impossible to be here.");
                }

            } else {
                throw new Exception("SELECT or sent but only INSERT, UPDATE, DELETE allowed.  Please use the "
                        + "execAnyPreparedSQL method.");
            }

        } catch (SQLException sqe) {
            SimpleLogging.vLogException(strPrefix + "|" + uuidThis.toString()
                + "|" + sqe.getSQLState()
                + "|" + sqe.getErrorCode(), sqe);
            throw new Exception("SQL error has occured.  Message: "
                    + sqe.getMessage()
                    + ", Log ID: " + uuidThis.toString()
                    + ", SQL state: " + sqe.getSQLState()
                    + ", SQL err code: " + sqe.getErrorCode()
                    + ", Q: " + strSQL
                    + ", bind vars: " + Arrays.asList(objBind));
        } catch (Exception e) {
            SimpleLogging.vLogException(strPrefix, e);
            throw e;
        }
        return intRowsChanged;
    }
        
    private CachedRowSet execAnyPreparedSQL(String strSQL, Object... objBind) throws Exception {
        
        UUID uuidThis = UUID.randomUUID();
        String strPrefix = String.valueOf(Thread.currentThread().getId());
        final CachedRowSet crs = new CachedRowSetImpl();
        SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "received " + strSQL);
        
        try (Connection con = DriverManager.getConnection(this.cfgConfEJB.strGetMSSQLAPPURL());
             PreparedStatement pstmt = con.prepareStatement(strSQL, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            // convert the object array to bind variables
            for (int x = 0; x < objBind.length; x++) {
                String strBindVar = String.valueOf(objBind[x]);
                if (objBind[x] instanceof String) {
                    pstmt.setString((x + 1), strBindVar);
                    SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "found string for bindvar("
                            + (x + 1) + "): " + strBindVar);
                } else if (objBind[x] instanceof Integer) {
                    pstmt.setInt((x + 1), (int) objBind[x]);
                    SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "found integer for bindvar("
                            + (x + 1) + "): " + Integer.parseInt(strBindVar));
                } else if (objBind[x] instanceof Long) {
                    pstmt.setLong(x + 1, Long.parseLong(strBindVar));
                } else if (objBind[x] instanceof Float) {
                    pstmt.setFloat(x + 1, Float.parseFloat(strBindVar));
                } else if (objBind[x] instanceof Date) {
                    pstmt.setDate(x + 1, (java.sql.Date)objBind[x]);
                } else if (objBind[x] instanceof byte[]) {
                    pstmt.setBlob(x + 1, new SerialBlob((byte[]) objBind[x]));
                } else {
                    throw new Exception("Unsupported bind variable (" + x + ") type in prepared statement: " 
                            + objBind[x].getClass().getName());
                }
            }
            // generate a list of K->V for the debug log
            Map mapBindVars = new HashMap<String, Object>();
            for (int x = 1; x < pstmt.getParameterMetaData().getParameterCount(); x++) {
                mapBindVars.put(pstmt.getParameterMetaData().getParameterTypeName(x), 
                        pstmt.getParameterMetaData().getParameterType(x));
            }
            // send K->V to the debug log
            SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "successfully converted object list to " 
                    + pstmt.getParameterMetaData().getParameterCount()
                    + " parameter(s): " + mapBindVars);

            // determine if we're changing data or fetching data
            if ((strSQL.toLowerCase().indexOf("update") > -1) || 
                (strSQL.toLowerCase().indexOf("insert") > -1) ||
                (strSQL.toLowerCase().indexOf("delete") > -1)) {
                throw new Exception("INSERT, UPDATE, or DELETE sent when only SELECTs are supported!  Please "
                        + "use the execAnyPreparedUpdate() method instead.");
            } else {
                // we are fetching data, use the query method
                crs.populate(pstmt.executeQuery());
                SimpleLogging.vLogEvent(strPrefix + "|" + uuidThis.toString(), "retrieved " + crs.size()
                            + " rows");
            }

        } catch (Exception e) {
            SimpleLogging.vLogException(strPrefix, e);
            throw e;
                    
        }
        return crs;
    }  
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** Translations methods ******">
    public List<FirmwareDefinition> lstGetFirmwareDefinitions() {
        return this.lstFirmwareDefs;
    }
    
    public Optional<FirmwareDefinition> pGetFirmwareDefinitionByMakeAndModel(final String strMake, final String strModel) {
        return this.lstFirmwareDefs
                .parallelStream()
                .unordered()
                .filter(p -> p.strMake.equalsIgnoreCase(strMake) && p.strModel.equalsIgnoreCase(strModel))
                .findFirst();
    }
    
    public List<FirmwareDefinition> lstGetFirmwareDefinitionByFilter(final List<SenchaFilter> lstFilters) 
        throws Exception {
        
        SimpleLogging.vLogEvent(this.strThreadId, "Querying for filters: " + lstFilters);
        List lstFirmware = new ArrayList();
        if (this.blnUseCaches && this.mapDataCache.containsKey(lstFilters)) {
            SimpleLogging.vLogEvent(this.strThreadId, "Cached copy found!");
            lstFirmware = this.mapDataCache.get(lstFilters);
        } else {
            SimpleLogging.vLogEvent(this.strThreadId, "Received " + lstFilters.size() 
                + " filters: " + lstFilters);
            List lstReturn = null;
            // Check if the keys already have a cached resultset
            if (this.blnUseCaches && this.mapDataCache.containsKey(lstFilters)) {
                SimpleLogging.vLogEvent(this.strThreadId, "Cached copy found!");
                lstReturn = this.mapDataCache.get(Arrays.asList(lstFilters).toString());
            } else {
                // List to contain the predicates
                final List<Predicate<FirmwareDefinition>> lstPredicatesOR = new ArrayList();
                final List<Predicate<FirmwareDefinition>> lstPredicatesAND = new ArrayList();
                final List<String> lstANDs = new ArrayList();
                final List<String> lstORs = new ArrayList();
                // Loop through the filters and build the composite predicate with matched filter keys to known keys in the data struct
                for (int x = 0; x < lstFilters.size(); x++) {
                    
                    SimpleLogging.vLogEvent(this.strThreadId, "Processing filter[" 
                            + x + "]: " + lstFilters.get(x));
                    final SenchaFilter sfThis = lstFilters.get(x);
                    final SenchaFilter sfBefore = lstFilters.get(x-1);
                    final Predicate<FirmwareDefinition> pThis;

                    // <editor-fold defaultstate="collapsed" desc="****** Case statement that matches incoming filter keys with the Firmware objects ******">
                    switch (lstFilters.get(x).strProperty.toLowerCase()) {
                        case "make":
                            pThis = p->p.strMake.equalsIgnoreCase(sfThis.strValue);
                            if (lstANDs.contains("make")) {
                                //lstPredicatesAND.add(pThis.negate());
                                lstPredicatesOR.add(pThis);
                                lstPredicatesAND.add(pThis);
                                //lstORs.add("make");
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"make\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(pThis);
                                lstPredicatesOR.add(pThis);
                                lstANDs.add("make");
                                //lstORs.remove("make");
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"make\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        case "model":
                            pThis = p->p.strModel.equalsIgnoreCase(sfThis.strValue);
                            if (lstANDs.contains("model")) {
                                lstPredicatesAND.add(pThis.negate());
                                lstPredicatesOR.add(pThis);
                                //lstORs.add("model");
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"model\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(pThis);
                                lstPredicatesOR.add(pThis);
                                //lstANDs.add("model");
                                //lstORs.remove("model");
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"model\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        case "defid":
                            if (lstANDs.contains("defid")) {
                                 lstPredicatesOR.add(p->p.intId == Integer.valueOf(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"defid\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.intId == Integer.valueOf(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"defid\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        case "docsislevel":
                            if (lstANDs.contains("docsislevel")) {
                                 lstPredicatesOR.add(p->p.intDocsisLevel == Integer.valueOf(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"docsislevel\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.intDocsisLevel == Integer.valueOf(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"docsislevel\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }       
                            break;
                        case "firmwarefile":
                            if (lstANDs.contains("firmwarefile")) {
                                 lstPredicatesOR.add(p->p.strFirmwareFile.equalsIgnoreCase(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"firmwarefile\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.strFirmwareFile.equalsIgnoreCase(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"firmwarefile\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            } 
                            break;
                        case "residentialcos":
                            if (lstANDs.contains("residentialcos")) {
                                 lstPredicatesOR.add(p->p.strResidentialCOS.equalsIgnoreCase(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"residentialcos\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.strResidentialCOS.equalsIgnoreCase(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"residentialcos\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            } 
                            break;
                        case "commercialcos":
                            if (lstANDs.contains("commercialcos")) {
                                 lstPredicatesOR.add(p->p.strCommercialCOS.equalsIgnoreCase(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"commercialcos\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.strCommercialCOS.equalsIgnoreCase(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"commercialcos\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        case "disabledcos":
                            if (lstANDs.contains("disabledcos")) {
                                 lstPredicatesOR.add(p->p.strDisabledCOS.equalsIgnoreCase(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"disabledcos\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.strDisabledCOS.equalsIgnoreCase(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"disabledcos\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        case "emtacos":
                            if (lstANDs.contains("emtacos")) {
                                 lstPredicatesOR.add(p->p.strMTACOS.equalsIgnoreCase(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"emtacos\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.strMTACOS.equalsIgnoreCase(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"emtacos\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;    
                        case "maxports":
                            if (lstANDs.contains("maxports")) {
                                 lstPredicatesOR.add(p->p.intMaxPorts == Integer.valueOf(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"maxports\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.intMaxPorts == Integer.valueOf(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"maxports\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        case "upgradeprotocol":
                            if (lstANDs.contains("upgradeprotocol")) {
                                 lstPredicatesOR.add(p->p.intUpgradeProtocol == Integer.valueOf(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"upgradeprotocol\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.intUpgradeProtocol == Integer.valueOf(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"upgradeprotocol\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        case "ipv6":
                            if (lstANDs.contains("ipv6")) {
                                 lstPredicatesOR.add(p->p.intIPV6 == Integer.valueOf(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"ipv6\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.intIPV6 == Integer.valueOf(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"ipv6\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        case "bsdevicename":
                            if (lstANDs.contains("bsdevicename")) {
                                 lstPredicatesOR.add(p->p.strBSDdeviceName.equalsIgnoreCase(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"bsdevicename\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.strBSDdeviceName.equalsIgnoreCase(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"bsdevicename\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        case "bsdevicetype":
                            if (lstANDs.contains("bsdevicetype")) {
                                 lstPredicatesOR.add(p->p.strBSDeviceType.equalsIgnoreCase(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"bsdevicetype\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.strBSDeviceType.equalsIgnoreCase(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"bsdevicetype\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        case "bsdevicelevel":
                            if (lstANDs.contains("bsdevicelevel")) {
                                 lstPredicatesOR.add(p->p.strBSDeviceType.equalsIgnoreCase(sfThis.strValue));
                                 SimpleLogging.vLogEvent(this.strThreadId, "Match found \"bsdevicelevel\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to OR list");
                            } else {
                                lstPredicatesAND.add(p->p.strBSDeviceType.equalsIgnoreCase(sfThis.strValue));
                                SimpleLogging.vLogEvent(this.strThreadId, "Match found \"bsdevicelevel\" == \"" 
                                    + sfThis.strProperty + "\"!  Adding predicate to AND list");
                            }
                            break;
                        default:
                            throw new Exception("Unknown firmware key in filter: " + lstFilters.get(x));
                    }
                    // </editor-fold>

                }

                // build one predicate off of the predicate list - note this is joined together by
                // OR statements: "key1 OR key2 OR key3.." 
                final Predicate<FirmwareDefinition> prdCompositeAND = lstPredicatesAND.parallelStream()
                        .reduce(Predicate::and).orElse(t->true);
                
                final Predicate<FirmwareDefinition> prdCompositeOR = lstPredicatesOR.parallelStream()
                        .reduce(Predicate::or).orElse(t->true);

                // return a list of SenchaFilters that match the keys.  Note the distinct() keyword 
                // removes duplicates
                List<FirmwareDefinition> lstFirmware1 = new ArrayList();
                List<FirmwareDefinition> lstFirmware2 = new ArrayList();
                
                if (lstPredicatesOR.isEmpty() || lstPredicatesOR == null) {
                    lstFirmware1 = this.lstFirmwareDefs;
                } else {
                    lstFirmware1 = this.lstFirmwareDefs
                        .parallelStream()
                        .unordered()
                        .filter(prdCompositeOR)
                        .distinct()
                        .collect(Collectors.toList());
                }
                
                if (lstPredicatesAND.isEmpty() || lstPredicatesAND == null) {
                    lstFirmware = lstFirmware1;
                } else {
                    lstFirmware = lstFirmware1
                        .parallelStream()
                        .unordered()
                        .filter(prdCompositeAND)
                        .distinct()
                        .collect(Collectors.toList());
                }
                
                /*
                if (lstORs.isEmpty()) {
                    SimpleLogging.vLogEvent(this.strThreadId, "NOT processing OR statements");
                    lstFirmware = this.lstFirmwareDefs
                        .parallelStream()
                        .unordered()
                        .filter(prdCompositeAND)
                        .distinct()
                        .collect(Collectors.toList());     
                } else {
                    SimpleLogging.vLogEvent(this.strThreadId, "Processing AND then OR statements");
                    lstFirmware = this.lstFirmwareDefs
                        .parallelStream()
                        .unordered()
                        .filter(prdCompositeOR)
                        .distinct()
                        .collect(Collectors.toList()); 
                    /*
                    lstFirmware = Stream.concat(this.lstFirmwareDefs
                            .parallelStream()
                            .distinct()
                            .unordered()
                            .filter(prdCompositeAND), this.lstFirmwareDefs
                            .parallelStream()
                            .distinct()
                            .unordered()
                            .filter(prdCompositeOR))
                            .collect(Collectors.toList());
                    */
               
                if (this.blnUseCaches) {
                    this.mapDataCache.put(lstFilters, lstFirmware);
                }
            }
        }
        return lstFirmware;
    }
    // </editor-fold>

}
