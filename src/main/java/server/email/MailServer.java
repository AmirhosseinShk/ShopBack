/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.email;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import server.properties.ProjectProperties;

/**
 *
 * @author @AmirShk
 */
public class MailServer {

    public static MailServer instance;

    private Properties prop;
    private Session session;

    public static MailServer getInstance() {
        if (instance == null) {
            instance = new MailServer();
            instance.InitialSession_Prop();
        }
        return instance;
    }

    private void InitialSession_Prop() {
        prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.host", "mail.maximmagazin.ru");
        prop.put("mail.smtp.port", "465");

        session = Session.getInstance(prop, null);
    }

    public void SendEmail(String emailAddress, String content) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("shop@maximmagazin.ru"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(emailAddress));
            message.setSubject("MaximMagazin Order List");
            String msg = content;
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);

            String username = ProjectProperties.getInstance().getProperty("mailServer.username");
            String password = ProjectProperties.getInstance().getProperty("mailServer.password");

            Transport transport = session.getTransport("smtps");
            transport.connect("mail.maximmagazin.ru", 465, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException ex) {
            Logger.getLogger(MailServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
