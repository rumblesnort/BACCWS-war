package com.Grande.GSM.BACCWS_WAR.Utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author rich
 */
public class SimpleLogging implements Runnable {
    
    // <editor-fold defaultstate="collapsed" desc="****** CLASS MEMBER VARS ******">
    final Path pthForLogging;
    final String strClassName;
    final String strMethodName;
    final String strEventText;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** CONSTRUCTORS ******">
    // for logging prefix + event
    public SimpleLogging(String strPrefix, String strEvent) {
        
        // <editor-fold defaultstate="collapsed" desc="****** GET A CURRENT TIMESTAMP ******">
        Date dteThis = new Date();
        SimpleDateFormat sdfThis = new SimpleDateFormat("MMM dd HH:mm:ss ");
        String strNow = sdfThis.format(dteThis);
        // </editor-fold>
        
        // determine calling class name and method
        Throwable thrThis = new Throwable();
        StackTraceElement[] aseThis = thrThis.getStackTrace();
        this.strClassName = aseThis[1].getFileName().replaceAll(".java", "");
        this.strMethodName = aseThis[1].getMethodName();
        // set the full file path
        this.pthForLogging = Paths.get(this.vSetFilePath());
        // set the log entry
        this.strEventText = strNow + "(" + this.strClassName
                + "::" + this.strMethodName + " "
                + strPrefix + ")" + strEvent + "\n";
    }
    // for logging event
    public SimpleLogging(String strEvent) {
        
        // <editor-fold defaultstate="collapsed" desc="****** GET A CURRENT TIMESTAMP ******">
        Date dteThis = new Date();
        SimpleDateFormat sdfThis = new SimpleDateFormat("MMM dd HH:mm:ss ");
        String strNow = sdfThis.format(dteThis);
        // </editor-fold>
        
        // determine calling class name and method
        Throwable thrThis = new Throwable();
        StackTraceElement[] aseThis = thrThis.getStackTrace();
        this.strClassName = aseThis[1].getFileName().replaceAll(".java", "");
        this.strMethodName = aseThis[1].getMethodName();
        // set the full file path
        this.pthForLogging = Paths.get(this.vSetFilePath());
        // set the log entry
        this.strEventText = strNow + "(" + this.strClassName
                + "::" + this.strMethodName + ")" + strEvent + "\n";
    }
    // for logging Exceptions
    public SimpleLogging(String strPrefix, Throwable t) {
        
        // <editor-fold defaultstate="collapsed" desc="****** GET A CURRENT TIMESTAMP ******">
        final Date dteThis = new Date();
        final SimpleDateFormat sdfThis = new SimpleDateFormat("MMM dd HH:mm:ss ");
        final String strNow = sdfThis.format(dteThis);
        final StringWriter sw = new StringWriter();
        final PrintWriter pw;
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** CONVERT STACK TRACE INTO A STRING ******">
        pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.close();
        // </editor-fold>
            
        // determine calling class name and method
        Throwable thrThis = new Throwable();
        StackTraceElement[] aseThis = thrThis.getStackTrace();
        this.strClassName = aseThis[1].getFileName().replaceAll(".java", "");
        this.strMethodName = aseThis[1].getMethodName();
        // set the full file path
        this.pthForLogging = Paths.get(this.vSetFilePath());
        // set the log entry
        this.strEventText = strNow + "(" + this.strClassName
                + "::" + this.strMethodName + " "
                + strPrefix + ")\n" + sw.toString();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** RUNNABLE ******">
    @Override
    public void run() {
        SeekableByteChannel sbcChannel = null;
        final ByteBuffer bytBuffer;
        try {
            Set<PosixFilePermission> perms =
                PosixFilePermissions.fromString("rw-r--r---");
            FileAttribute<Set<PosixFilePermission>> attr =
                PosixFilePermissions.asFileAttribute(perms);
            sbcChannel = Files.newByteChannel(this.pthForLogging, StandardOpenOption.APPEND);
            bytBuffer = ByteBuffer.wrap(this.strEventText.getBytes());
            sbcChannel.write(bytBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { sbcChannel.close(); } catch (Exception e) {} finally {}
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** Legacy Methods ******">
    public static void vLogEvent(String strPrefix, String strEvent) {
       
        // <editor-fold defaultstate="collapsed" desc="****** METHOD VARS ******">
        final String strClassName;
        final String strMethodName;
        final String strEventText;
        final String strPath;
        final String strOS = System.getProperty("os.name");
        final Path pthForLogging;
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** GET A CURRENT TIMESTAMP ******">
        Date dteThis = new Date();
        SimpleDateFormat sdfThis = new SimpleDateFormat("MMM dd HH:mm:ss ");
        String strNow = sdfThis.format(dteThis);
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** DETERMINE FILE PATH/NAME ******">
        // determine calling class name and method
        Throwable thrThis = new Throwable();
        StackTraceElement[] aseThis = thrThis.getStackTrace();
        strClassName = aseThis[1].getFileName().replaceAll(".java", "");
        strMethodName = aseThis[1].getMethodName();
        // set the full file path
        if (strOS.startsWith("Windows")) {
            strPath = "C:\\temp\\gsm\\" + strClassName + ".log";
        } else {
            strPath = "/var/log/gsm/BACC/" + strClassName + ".log";
        }
        pthForLogging = Paths.get(strPath);
        // set the log entry
        strEventText = strNow + "(" + strClassName
                + "::" + strMethodName + " "
                + strPrefix + ") " + strEvent + "\n";
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** WRITE TO FILE ******">
        SeekableByteChannel sbcChannel = null;
        final ByteBuffer bytBuffer;
        try {
            Set<PosixFilePermission> perms =
                PosixFilePermissions.fromString("rw-rw-r--");
            FileAttribute<Set<PosixFilePermission>> attr =
                PosixFilePermissions.asFileAttribute(perms);
            sbcChannel = Files.newByteChannel(pthForLogging, 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            bytBuffer = ByteBuffer.wrap(strEventText.getBytes());
            sbcChannel.write(bytBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { sbcChannel.close(); } catch (Exception e) {} finally {}
        }
        // </editor-fold>
        
    }
    
    public static void vLogEvent(String strEvent) {
       
        // <editor-fold defaultstate="collapsed" desc="****** METHOD VARS ******">
        final String strClassName;
        final String strMethodName;
        final String strEventText;
        final String strPath;
        final String strOS = System.getProperty("os.name");
        final Path pthForLogging;
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** GET A CURRENT TIMESTAMP ******">
        Date dteThis = new Date();
        SimpleDateFormat sdfThis = new SimpleDateFormat("MMM dd HH:mm:ss ");
        String strNow = sdfThis.format(dteThis);
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** DETERMINE FILE PATH/NAME ******">
        // determine calling class name and method
        Throwable thrThis = new Throwable();
        StackTraceElement[] aseThis = thrThis.getStackTrace();
        strClassName = aseThis[1].getFileName().replaceAll(".java", "");
        strMethodName = aseThis[1].getMethodName();
        // set the full file path
        if (strOS.startsWith("Windows")) {
            strPath = "C:\\temp\\gsm\\" + strClassName + ".log";
        } else {
            strPath = "/var/log/gsm/BACC/" + strClassName + ".log";
        }
        pthForLogging = Paths.get(strPath);
        // set the log entry
        strEventText = strNow + "(" + strClassName
                + "::" + strMethodName + ") "
                + strEvent + "\n";
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** WRITE TO FILE ******">
        SeekableByteChannel sbcChannel = null;
        final ByteBuffer bytBuffer;
        try {
            Set<PosixFilePermission> perms =
                PosixFilePermissions.fromString("rw-rw-r--");
            FileAttribute<Set<PosixFilePermission>> attr =
                PosixFilePermissions.asFileAttribute(perms);
            sbcChannel = Files.newByteChannel(pthForLogging, 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            bytBuffer = ByteBuffer.wrap(strEventText.getBytes());
            sbcChannel.write(bytBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { sbcChannel.close(); } catch (Exception e) {} finally {}
        }
        // </editor-fold>
        
    }
    
    public static void vLogException(String strPrefix, Throwable t) {
        
        // <editor-fold defaultstate="collapsed" desc="****** METHOD VARS ******">
        final String strClassName;
        final String strMethodName;
        final String strEventText;
        final String strPath;
        final String strOS = System.getProperty("os.name");
        final Path pthForLogging;
        final StringWriter sw = new StringWriter();
        final PrintWriter pw;
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** CONVERT STACK TRACE INTO A STRING ******">
        pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.close();
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** GET A CURRENT TIMESTAMP ******">
        Date dteThis = new Date();
        SimpleDateFormat sdfThis = new SimpleDateFormat("MMM dd HH:mm:ss ");
        String strNow = sdfThis.format(dteThis);
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** DETERMINE FILE PATH/NAME ******">
        // determine calling class name and method
        Throwable thrThis = new Throwable();
        StackTraceElement[] aseThis = thrThis.getStackTrace();
        strClassName = aseThis[1].getFileName().replaceAll(".java", "");
        strMethodName = aseThis[1].getMethodName();
        // set the full file path
        if (strOS.startsWith("Windows")) {
            strPath = "C:\\temp\\gsm\\" + strClassName + ".log";
        } else {
            strPath = "/var/log/gsm/BACC/" + strClassName + ".log";
        }
        pthForLogging = Paths.get(strPath);
        // set the log entry
        strEventText = strNow + "(" + strClassName
                + "::" + strMethodName 
                + ") " + strPrefix + " "
                + sw.toString();
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="****** WRITE TO FILE ******">
        SeekableByteChannel sbcChannel = null;
        final ByteBuffer bytBuffer;
        try {
            Set<PosixFilePermission> perms =
                PosixFilePermissions.fromString("rw-rw-r--");
            FileAttribute<Set<PosixFilePermission>> attr =
                PosixFilePermissions.asFileAttribute(perms);
            sbcChannel = Files.newByteChannel(pthForLogging, 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            bytBuffer = ByteBuffer.wrap(strEventText.getBytes());
            sbcChannel.write(bytBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { sbcChannel.close(); } catch (Exception e) {} finally {}
        }
        // </editor-fold>
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="****** Helper methods ******">
    private String vSetFilePath() {
        final String strOS = System.getProperty("os.name");
        final String strPath;
        // determine the path using the OS, windows uses the universal escape character for file paths
        if (strOS.startsWith("Windows")) {
            strPath = "C:\\temp\\gsm\\";
        } else {
            strPath = "/var/log/gsm/BACC/";
        }
        return strPath + this.strClassName + ".log";
    }
    // </editor-fold>
    
}

