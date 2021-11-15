package bean;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.SessionBean;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

@Stateless
@LocalBean
public class EJBEmail {
    
    //@Resource(lookup = "java:jboss/mail/jeeapp") //change name of resource in wildfly
    private Session mailSession;

    private String from="buscompany@gmail.com";
    public EJBEmail() {

    }

    @Asynchronous
    public void sendEmail(String to, String subject, String content) {
        try
        {
            Message message = new MimeMessage(getEmailSession());
            message.setFrom(new InternetAddress(this.from));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));            
            message.setSubject(subject);
            message.setText(content);        
            Transport.send(message);
        }
        catch (MessagingException e)
        {
            System.out.println("Error sending mail : "+ e );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Session getEmailSession() throws Exception{
        InitialContext context = new InitialContext();
        return (Session) context.lookup("java:jboss/mail/jeeapp");
    }

}