/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.Grande.GSM.BACCWS_WAR.WS.REST.EOS;

import com.Grande.GSM.BACCWS_WAR.Utilities.SimpleLogging;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author rich
 */
@ApplicationPath("/EOS/Modem")
public class ModemConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        try {
            Class jsonProvider = Class.forName("org.glassfish.jersey.jackson.JacksonFeature");
            resources.add(jsonProvider);
            resources.add(com.Grande.GSM.BACCWS_WAR.WS.REST.EOS.ModemEndpoint.class);
        } catch (Exception e) {
            SimpleLogging.vLogException(String.valueOf(Thread.currentThread().getId()), e);
        }
        return resources;
    }
    
}
