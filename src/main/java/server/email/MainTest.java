/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.email;

import com.sun.mail.smtp.SMTPTransport;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 *
 * @author AmirShk
 */
public class MainTest {

    public static void main(String[] args) {
        String emailAddress = "Amirshk76@outlook.com";
        String text = "this is test mail message";
        MainTest mainTest = new MainTest();
        mainTest.SendEmail(emailAddress, text);
    }

    String username;
    String password;
    String url;

    Session session;
    Properties prop;

    public void MailServer() {
        this.username = "wemessenger@wemessange.ir";
        this.password = "s\\[SQYIJ>Do=>1=";
        this.url = "mail.iais.co";

        prop = new Properties();
        prop.put("mail.smtp.auth", true);
        //prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.host", url);
        prop.put("mail.smtp.port", "25");

        session = Session.getInstance(prop, null);
    }

    public void SendEmail(String emailAddress, String content) {
        MailServer();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(emailAddress));
            message.setSubject("WeMessenger Email Verification");
            String msg = content;
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);

            SMTPTransport  transport = (SMTPTransport)session.getTransport("smtp");
            transport.connect(url, 25, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException ex) {
            System.out.println(ex);
        }
    }

}
