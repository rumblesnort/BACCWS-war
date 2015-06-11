package com.Grande.GSM.BACCWS_WAR.Utilities;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * The Configurator bean is a singleton that serves configuration data read from
 * <code>ResourceBundle</code> objects.<br>
 *<br>
 * The singleton starts on application deployment (or re-deployment).  The
 * <code>@PostConstruct</code> reads the server's hostname to determine what
 * "mode" the application is in.  Listed below are the modes of operation and the 
 * hostnames associated:<br>
 * <br><code>
 * <b><u>Environment</b></u>&nbsp;&nbsp;&nbsp;&nbsp;<b><u>Hostname</b></u><br>
 * production&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;jnode* (jnode00, jnode01, etc)<br>
 * staging&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ntac<br>
 * development&nbsp;&nbsp;&nbsp;&nbsp;anything else<br>
 * <br></code>
 *
 * Mode-specific resources like databases and BACC that have development and
 * production instances rely on the Configurator to return the correct
 * resource handle/JNDI names.  Additionally, machine-specific variables like
 * file paths are utilized here so different operating systems may utilize
 * the application.
 * 
 * @author Richard Fogle
 * @since 1/04/2010
 */
@ApplicationScoped
public class Configurator {
    // <editor-fold defaultstate="collapsed" desc="******** GLOBAL VARIABLES ********">
    /**
     * Contains the key/value mappings for gsm.properties file
     */
    private HashMap<String,HashMap<String,String>> hsmProps = null;
    /**
     * This server's hostname
     */
    private String strHostName = null;
    /**
     * The current mode (development/staging/production) we're operating in
     */
    private String strMode = null;
    private final static String GSMPROP = "com.Grande.GSM.BACCWS_WAR.Utilities.BACC";
    // </editor-fold>

    /**
     * @inheritDoc
     */
    public void vReloadBundles() {
        //<editor-fold defaultstate="collapsed" desc="******** LOCAL VARIABLES ********">
        ResourceBundle rbnLocal = null;
        String strKey = null;
        HashMap<String,String> hsmLocalProps = new HashMap();
        Enumeration enumKeys = null;
        // </editor-fold>

        try {
            /* Clear the current in-memory Mappings */
            this.hsmProps.clear();

            /* Clear the current ResourceBundle cache */
            ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
            System.out.println("The global ResourceBundle cache has been cleared");

            //<editor-fold defaultstate="collapsed" desc="******** RELOAD GSM PROPERTIES ********">
            rbnLocal = ResourceBundle.getBundle(GSMPROP);
            System.out.println("The GSM ResourceBundle has been reloaded");

            /* Grab an enumeration of the keys */
            enumKeys = rbnLocal.getKeys();

            /* Iterate through all the keys and store them in our HashMap */
            while(enumKeys.hasMoreElements()) {
                strKey = (String) enumKeys.nextElement();
                hsmLocalProps.put(strKey, rbnLocal.getString(strKey));
            }

            this.hsmProps.put("GSM", hsmLocalProps);
            
            /* Clear the mappings. Must be done before iterating through a new properties file */
            hsmLocalProps.clear();
            // </editor-fold>

            //<editor-fold defaultstate="collapsed" desc="******** RELOAD DEBUG PROPERTIES ********">
           // rbnLocal = ResourceBundle.getBundle(DEBUGPROP);
            System.out.println("The DEBUG ResourceBundle has been reloaded");

            /* Grab an enumeration of the keys */
            enumKeys = rbnLocal.getKeys();

            /* Iterate through all the keys and store them in our HashMap */
            while(enumKeys.hasMoreElements()) {
                strKey = (String) enumKeys.nextElement();
                hsmLocalProps.put(strKey, rbnLocal.getString(strKey));
            }

            this.hsmProps.put("DEBUG", hsmLocalProps);

            /* Clear the mappings. Must be done before iterating through a new properties file */
            hsmLocalProps.clear();
            // </editor-fold>

            System.out.println("New property values have been cached");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="******** GETTER METHODS ********">
    /**
     * Queries the top-level properties {@code HashMap} and returns a mapping of
     * the key/value pairs for the given properties file as defined in strHashKey.<br><br>
     *
     * @since 07/06/2010
     * @param strHashKey The HashKey for the properties file mapping to lookup
     * @return A key/value mapping of the properties file
     */
    private HashMap<String,String> hsmGetHashKey(String strHashKey) {
        if(!this.hsmProps.containsKey(strHashKey)) {
            throw new MissingResourceException("(RUNTIME) \"" + strHashKey + "\" DOES NOT EXIST",
                                               GSMPROP, strHashKey);
        }
        else {
            return this.hsmProps.get(strHashKey);
        }
    }

    /**
     * @inheritDoc
     */
    public String strGetValue(String strHashKey, String strProp) {
        if(strProp == null) {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(RUNTIME EXCEPTION) Attempt to get a property value but passed in a null property key");
            // </editor-fold>

            throw new IllegalArgumentException("The property argument can not be null");
        }
        else if(strProp.isEmpty()) {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(RUNTIME EXCEPTION) Attempt to get a property value but passed in an empty property key");
            // </editor-fold>

            throw new IllegalArgumentException("The property argument can not be an empty String");
        }
        else if(!this.hsmGetHashKey(strHashKey).containsKey(strProp)) {
            throw new MissingResourceException("(RUNTIME) \"" + strProp + "\" DOES NOT EXIST",
                                               GSMPROP, strProp);
        }
        else {
            return this.hsmGetHashKey(strHashKey).get(strProp);
        }
    }

    /**
     * @inheritDoc
     */
    public Integer intGetValue(String strHashKey, String strProp) {
        if(strProp == null) {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(RUNTIME EXCEPTION) Attempt to get a property value but passed in a null property key");
            // </editor-fold>

            throw new IllegalArgumentException("The property argument can not be null");
        }
        else if(strProp.isEmpty()) {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(RUNTIME EXCEPTION) Attempt to get a property value but passed in an empty property key");
            // </editor-fold>

            throw new IllegalArgumentException("The property argument can not be an empty String");
        }
        else if(!this.hsmGetHashKey(strHashKey).containsKey(strProp)) {
            throw new MissingResourceException("(RUNTIME) \"" + strProp + "\" DOES NOT EXIST",
                                               GSMPROP, strProp);
        }
        else {
            return Integer.parseInt(this.hsmGetHashKey(strHashKey).get(strProp));
        }
    }

    /**
     * @inheritDoc
     */
    public Boolean blnGetValue(String strHashKey, String strProp) {
        if(strProp == null) {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(RUNTIME EXCEPTION) Attempt to get a property value but passed in a null property key");
            // </editor-fold>

            throw new IllegalArgumentException("The property argument can not be null");
        }
        else if(strProp.isEmpty()) {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(RUNTIME EXCEPTION) Attempt to get a property value but passed in an empty property key");
            // </editor-fold>

            throw new IllegalArgumentException("The property argument can not be an empty String");
        }
        else if(!this.hsmGetHashKey(strHashKey).containsKey(strProp)) {
            throw new MissingResourceException("(RUNTIME) \"" + strProp + "\" DOES NOT EXIST",
                                               GSMPROP, strProp);
        }
        else {
            return Boolean.parseBoolean(this.hsmGetHashKey(strHashKey).get(strProp));
        }
    }

    /**
     * @inheritDoc
     */
    public HashMap<String,String> hsmGetMappings() {
        return new HashMap(this.hsmProps);
    }

    /**
     * @inheritDoc
     */
    public Collection<String> GetPropertyFileKeys() {
        return new ArrayList(this.hsmProps.keySet());
    }

    /**
     * @inheritDoc
     */
    public Collection<String> GetGSMKeys() {
        return new ArrayList(this.hsmProps.get("GSM").keySet());
    }

    /**
     * @inheritDoc
     */
    public Collection<String> GetDEBUGKeys() {
        return new ArrayList(this.hsmProps.get("DEBUG").keySet());
    }

    // <editor-fold defaultstate="collapsed" desc="******** (PROVISIONING SYSTEM SPECIFIC) GETTER METHODS ********">
    /**
     * @inheritDoc
     */
    public String strGetMSSQLAPPURL() {
        return this.strGetValue("GSM", "GSM." + this.strMode + ".jdbc.MSSQL.APP.URL");
    }
        
    public Integer intGetBACCPort() {
        return this.intGetValue("GSM", "BACC." + this.strMode + ".port");
    }

    /**
     * @inheritDoc
     */
    public String strGetBACCHostName() {
        return this.strGetValue("GSM", "BACC." + this.strMode + ".hostname");
    }

    /**
     * @inheritDoc
     */
    public String strGetBACCPassword() {
        return this.strGetValue("GSM", "BACC." + this.strMode + ".password");
    }
    
    /**
     * @inheritDoc
     */
    public String strGetBACCUser() {
        return this.strGetValue("GSM", "BACC." + this.strMode + ".username");
    }
    
    /**
     * @inheritDoc
     */
    public String strGetHSDDomainName() {
        return this.strGetValue("GSM", "BACC." + this.strMode + ".HSD.domainname");
    }

    /**
     * @inheritDoc
     */
    public String strGetVOIPDomainName() {
        return this.strGetValue("GSM", "BACC." + this.strMode + ".VOIP.domainname");
    }
    // </editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="******** (EMAIL GROUP SPECIFIC) GETTER METHODS ********">
    /**
     * @inheritDoc
     */
    public String strGetGSMAlertEmail() {
        return this.strGetValue("GSM", "GSM." + this.strMode + ".alert.email");
    }

    /**
     * @inheritDoc
     */
    public String strGetOmniaAlertEmail() {
        return this.strGetValue("GSM", "OMNIA." + this.strMode + ".alert.email");
    }

    /**
     * @inheritDoc
     */
    public String strGetEmailResource() {
        return this.strGetValue("GSM", "GSM." + this.strMode + ".mailResource");
    }

    /**
     * @inheritDoc
     */
    public String strGetBACCAlertEmail() {
        return this.strGetValue("GSM", "BACC." + this.strMode + ".alert.email");
    }

    public String strGetFSTAlertEmail() {
        return this.strGetValue("GSM", "FST." + this.strMode + ".alert.email");
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="******** (MISC CONFIGS) GETTER METHODS ********">
    /**
     * @inheritDoc
     */
    public String strGetMode() {
        return this.strMode;
    }
    /**
     * @inheritDoc
     */
    public String strGetLoggingPrefix() {
        if(File.separator.equals("/")) {
            return this.strGetValue("GSM", "GSM." + this.strMode + ".debugLog.prefix");
        }
        else {
            return this.strGetValue("GSM", "GSM." + this.strMode + ".debugLogWindows.prefix");
        }
    }
    public String strGetHostName() {
        return this.strHostName;
    }
    // </editor-fold>

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="******** SETTER METHODS ********">
    public void vUpdateProperty(String strHashKey, String strProp, String strVal) {
        if(strProp == null) {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(RUNTIME) Attempt to update a property but passed in a null property key\n" + strProp);
            // </editor-fold>

            throw new IllegalArgumentException("The property argument can not be null");
        }
        else if(strProp.isEmpty()) {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(RUNTIME) Attempt to update a property but passed in an empty property key");
            // </editor-fold>

            throw new IllegalArgumentException("The property argument can not be an empty String");
        }
        else {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(UPDATE PROPERTY KEY) Setting key \"" + strProp + "\" to value \"" + strVal + "\"");
            // </editor-fold>

            this.hsmProps.get(strHashKey).put(strProp, strVal);
        }
    }

    /**
     * @inheritDoc
     */
    public void vRemoveProperty(String strHashKey, String strProp) {
        if(strProp == null) {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(RUNTIME EXCEPTION) Attempt to remove a property but passed in a null property key");
            // </editor-fold>

            throw new IllegalArgumentException("The property argument can not be null");
        }
        else if(strProp.isEmpty()) {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(RUNTIME EXCEPTION) Attempt to remove a property but passed in an empty property key");
            // </editor-fold>

            throw new IllegalArgumentException("The property argument can not be an empty String");
        }
        else {
            // <editor-fold defaultstate="collapsed" desc="******** DEBUG LOGGING ********">
            System.out.println("(REMOVE PROPERTY KEY) Removing key \"" + strProp + "\"");
            // </editor-fold>

            this.hsmProps.get(strHashKey).remove(strProp);
        }
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="******** BEAN STATE METHODS ********">
    /**
     * Populates the resources utilized by the business logic.<br><br>
     *
     * <p>The <code>@PostConstruct</code> method is called when the singleton
     * is first created.  Since the end of the singleton's lifecycle is when
     * the application is destroyed there is no <code>@PreDestroy</code>
     * method.</p>
     *
     * @author Richard Fogle
     * @since 1/4/2009
     * @param void
     *
     * @return void
     */
    @PostConstruct
    private void vConfiguratorConstruct() {
        //<editor-fold defaultstate="collapsed" desc="******** LOCAL VARIABLES ********">
        String strKey = null;
        HashMap<String,String> hsmGSMProps = new HashMap();
        HashMap<String,String> hsmDebugProps = new HashMap();
        Enumeration enumKeys = null;
        ResourceBundle rbnLocal = null;
        // </editor-fold>

        try {
            /* Initialize the gsm.properties HashMap */
            this.hsmProps = new HashMap();

            //<editor-fold defaultstate="collapsed" desc="******** POPULATE GSM PROPERTIES ********">
            /* Grab a resource bundle to gsm.properties */
            rbnLocal = ResourceBundle.getBundle(GSMPROP);

            /* Grab an enumeration of the keys */
            enumKeys = rbnLocal.getKeys();

            /* Iterate through all the keys and store them in our HashMap */
            while(enumKeys.hasMoreElements()) {
                strKey = (String)enumKeys.nextElement();
                hsmGSMProps.put(strKey, rbnLocal.getString(strKey));
            }

            this.hsmProps.put("GSM", hsmGSMProps);
            // </editor-fold>

            //<editor-fold defaultstate="collapsed" desc="******** POPULATE DEBUG PROPERTIES ********">
            /* Grab a resource bundle to DEBUG.properties */
            //rbnLocal = ResourceBundle.getBundle(DEBUGPROP);

            /* Grab an enumeration of the keys */
            enumKeys = rbnLocal.getKeys();

            /* Iterate through all the keys and store them in our HashMap */
            while(enumKeys.hasMoreElements()) {
                strKey = (String)enumKeys.nextElement();
                hsmDebugProps.put(strKey, rbnLocal.getString(strKey));
            }

            this.hsmProps.put("DEBUG", hsmDebugProps);
            // </editor-fold>

            /* Set the server hostname in our global variable */
            this.strHostName = InetAddress.getLocalHost().getHostName().trim();

            //<editor-fold defaultstate="collapsed" desc="******** SET OPERATING MODE BASED ON SERVER HOSTNAME ********">
            if(this.strHostName.indexOf("dev-gsm0") > -1) {
                this.strMode = "development";
            }
            else if(this.strHostName.indexOf("gsm0") > -1) {
                this.strMode = "production";
            }
            else if(this.strHostName.indexOf("oss0") > -1) {
                this.strMode = "production";
            }
            else {
                this.strMode = "development";
            }
            // </editor-fold>
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    //</editor-fold>
}
