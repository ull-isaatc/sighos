/**
 * 
 */
package es.ull.isaatc.test;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestMail {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.put("mail.smtp.user", "masbecas@gmail.com");

		props.put("mail.debug", "true");

		Session session = Session.getInstance(props);
		session.setDebug(true);
		
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress("masbecas@gmail.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("ivan@isaatc.ull.es"));
			message.setSubject("Cosas");
			BodyPart texto = new MimeBodyPart();
			texto.setText("Texto del mensaje");
			BodyPart adjunto = new MimeBodyPart();
			adjunto.setDataHandler(new DataHandler(new FileDataSource("d:\\Recetas\\Recetas de mi casa.doc")));
			adjunto.setFileName("Recetas de mi casa.doc");
			MimeMultipart multiParte = new MimeMultipart();

			multiParte.addBodyPart(texto);
			multiParte.addBodyPart(adjunto);
			message.setContent(multiParte);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		try {
			Transport t = session.getTransport("smtp");
			t.connect("masbecas@gmail.com","eadlmd50p");
			t.sendMessage(message,message.getAllRecipients());
			t.close();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
