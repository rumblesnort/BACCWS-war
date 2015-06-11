package com.Grande.GSM.BACCWS_WAR.Utilities;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.inject.Default;

/**
 * Alerting Class<br><br>
 *
 * <p>Class for GSM alerting through Email, Remedy,
 *    Syslog, and Netcool.</p>
 *
 * @since 02/02/2010
 * @author: Richard Fogle
 */
public class Alerting {

    @EJB
    private Configurator cfgThisEJB;

    @Resource(name = "java:app/mail/Exchange")
    private Session sesSession;

    /**
     * Sends an Email<br><br>
     *
     * <p>Sends an email to the GSM Distribution List.</p>
     *
     * @since 02/02/2010
     * @param strSubject Email Subject Line
     * @param strBody Email Message Body
     * @return Void
     */
    @Asynchronous
    public void vSendGSMEmailAlert(String strSubject, String strBody) {
        
        try {
            Message mmgMsg = new MimeMessage(this.sesSession);
            mmgMsg.setSubject(strSubject);
            mmgMsg.setSentDate(new Date());
            mmgMsg.setFrom(new InternetAddress(this.cfgThisEJB.strGetGSMAlertEmail()));
            mmgMsg.setRecipients(Message.RecipientType.TO,
                                 InternetAddress.parse(this.cfgThisEJB.strGetGSMAlertEmail(), false));
            mmgMsg.setText(strBody);
            Transport.send(mmgMsg);
        }
        catch(Exception e) {
            // this will be removed
            e.printStackTrace();
        }
    }

    /**
     * Sends an Email<br><br>
     *
     * <p>Sends an email to the BACC Distribution List.</p>
     *
     * @since 02/02/2010
     * @param strSubject Email Subject Line
     * @param strBody Email Message Body
     * @return Void
     */
    @Asynchronous
    public void vSendBACCEmailAlert(String strSubject, String strBody) {
        try {
            Message mmgMsg = new MimeMessage(this.sesSession);
            mmgMsg.setSubject(strSubject);
            mmgMsg.setSentDate(new Date());
            mmgMsg.setFrom(new InternetAddress(this.cfgThisEJB.strGetGSMAlertEmail()));
            mmgMsg.setRecipients(Message.RecipientType.TO,
                                 InternetAddress.parse(this.cfgThisEJB.strGetBACCAlertEmail(), false));
            mmgMsg.setText(strBody);
            Transport.send(mmgMsg);
        }
        catch(Exception e) {
            // this will be removed
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void AlertingC() {
    }

    @PreDestroy
    private void destroy() {
        this.cfgThisEJB = null;
        this.sesSession = null;
    }
}
