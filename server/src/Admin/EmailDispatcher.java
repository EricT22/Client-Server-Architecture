package Admin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailDispatcher {
    //private String host;
    ///private String port;
    //private String from; 
    public static final boolean gmail = true;
    // -- set the smtp host URL and port
    final static private String host = gmail ? "smtp.gmail.com" :
    "smtp.mail.yahoo.com";
    // -- set the smtp port
    final static private String port = "587";
    final static String from = gmail ? "adamjohncartozian@gmail.com" :
    "yahoousername@yahoo.com";
    // -- You must have a valid smtp server username/password pair, password is App specific generated
    final static private String smtpusername = gmail ? "adamjohncartozian@gmail.com" :
    "yahoousername";
    //final static private String smtppassword = gmail ? "gmailpassword" :
    final static private String smtppassword = gmail ? "rvij ffnm hapq btkp" :
    "rvij ffnm hapq btkp";
    /*public EmailDispatcher(String to, String anotheString){

    }*/

    public static void sendEmail(String to, String EmailText){
            // -- Configurations for the email connection to the smtp server using
            // Transport Layer Security (encryption)
            // 1) send an email, email service provider requests a secure connection from the recipient’s service
            // 2) the message is encrypted
            // 3) when the message reaches the recipient’s provider, it is decrypted
            // 4) The plain-text version of the email reaches the recipient’s device
            Properties props = System.getProperties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            // -- Create a session with required user details
            // this is basically logging into the smtp account
            Session session = Session.getInstance(props, new javax.mail.Authenticator()
            {
            protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(smtpusername, smtppassword);
        }
        });
        try {
            //-- create the Message to be sent
            MimeMessage msg = new MimeMessage(session);
            // -- get the internet addresses for the recipients
            InternetAddress addr = new InternetAddress(to);
            // -- from address
            msg.setFrom(new InternetAddress(from));
            // -- set the recipients
            msg.addRecipient(Message.RecipientType.TO, addr);
            // -- set the subject line (time stamp)
            Calendar cal = Calendar.getInstance();
            String timeStamp = new
            SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(cal.getTime());
            msg.setSubject("Sample Mail : " + timeStamp);
            msg.setSentDate(new Date());
            // -- set the message text
            //msg.setText("Message from " + (gmail ? "gmail.com" : "yahoo.com"));
            msg.setText(EmailText);
            System.out.println("Sending message...");
            // -- send the message
            Transport.send(msg);
            System.out.println("Mail has been sent successfully");
        } catch (MessagingException e) {
        System.out.println("Unable to send an email" + e);
        }
    }

    /*public static void main(String[] args) {
        // -- comma separated values of to email addresses
        String to = "acartozian@callutheran.edu";
        String message= "Hello how are you?";
        sendEmail(to, message);
        }*/
}
