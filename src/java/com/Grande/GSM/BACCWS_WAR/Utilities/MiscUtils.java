/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.Grande.GSM.BACCWS_WAR.Utilities;

import com.Grande.GSM.BACCWS_WAR.DataObjects.BACC.Device;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rich
 */
public class MiscUtils {
    
    // <editor-fold defaultstate="collapsed" desc="****** String utils ******">
    public static String strIntegerToString(final InputStream insThis) {
        return null;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** Stream utils ******">
    public static String strInputStreamToString(final InputStream insThis) {
        return null;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** BACC specific utils ******">
    public static Boolean isValidMAC(String strMAC) {
        if (strMAC == null || strMAC.equals("")) {
            return false;
        } else if ((strMAC.indexOf(":") == 2) && (strMAC.lastIndexOf(":") == 14)) {
            return true;
        } else if (strMAC.length() == 12) {
            return true;
        } else if ((strMAC.indexOf("1,6,") > -1) && (strMAC.length() == 21)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static HashMap<String, String> convertMAC(String strMAC) {
        HashMap<String, String> map = new HashMap();
        String hexVals = "0123456789ABCDEF";
        // determine if the MAC is in standard(colonized) format (01:23:45:67:89:ab)
        if (strMAC == null || strMAC.equals("")) {
            System.out.println("MAC is null or an empty string: \"" + strMAC + "\"");
        } else if ((strMAC.indexOf(":") == 2) && (strMAC.lastIndexOf(":") == 14)) {
            //System.out.println("Received standard MAC");
            map.put("std", strMAC);
            map.put("csg", strMAC.replaceAll(":", ""));
            map.put("bacc", "1,6," + strMAC);
        // determine if the MAC is in CSG format (0123456789ab)
        } else if (strMAC.length() == 12) {
            //System.out.println("Received CSG MAC");
            String stdMAC = null;
            stdMAC = strMAC.substring(0,2) + ":" + strMAC.substring(2,4) + ":" +
                    strMAC.substring(4,6) + ":" + strMAC.substring(6,8) + ":" +
                    strMAC.substring(8,10) + ":" + strMAC.substring(10,12);
            //System.out.println(stdMAC);
            map.put("std", stdMAC);
            map.put("csg", strMAC);
            map.put("bacc", "1,6," + stdMAC);
        // determine if the MAC is in BACC format (1,6,01:23:45:67:89:ab)
        } else if ((strMAC.indexOf("1,6,") > -1) && (strMAC.length() == 21)) {
            //System.out.println("Received BACC MAC");
            String stdMAC = null;
            stdMAC = strMAC.replaceAll("1,6,", "");
            map.put("std", strMAC.replaceAll("1,6,", ""));
            map.put("csg", stdMAC.replaceAll(":", ""));
            map.put("bacc", strMAC);
        // if all three fail return the original MAC in the hasmap.  Not any worse off than before this was called.
        } else if ((strMAC.indexOf("grandenetworks.net") > -1)) {
            String stdMAC = null;
            stdMAC = strMAC.substring(0,2) + ":" + strMAC.substring(2,4) + ":" +
                    strMAC.substring(4,6) + ":" + strMAC.substring(6,8) + ":" +
                    strMAC.substring(8,10) + ":" + strMAC.substring(10,12);
            map.put("std", strMAC.replaceAll("1,6,", ""));
            map.put("csg", strMAC.replaceAll(":", ""));
            map.put("bacc", "1,6," + stdMAC);
//            v0015d0518414.voice-lab.grandenetworks.net
        } else {
            System.out.println("Could not convert MAC: " + strMAC);
            map.put("std", strMAC);
            map.put("csg", strMAC);
            map.put("bacc", strMAC);
        }
        return map;
     }
    
    public static Device BACCResultToDeviceObject(Map mapResult) {
        Device devResult = new Device();
        
        
        return devResult;
    }
    // </editor-fold>
}
