package com.soohyunchoi;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 * Gangin.
 */
@SuppressWarnings("Duplicates")
public class autoMail {
	final private String from = "intern2@360payments.com";
	final private String username = "intern2@360payments.com";// change accordingly
	final private String password = "Soohyun9090!";// change accordingly
	final private String host = "smtp.office365.com";
	private static Properties props = new Properties();
	
			public autoMail() {
				setUpProperties();
			}
			private void setUpProperties() {
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", host);
				props.put("mail.smtp.port", "25");
			}
			
			private void sendMail() {
				String to = "soohyun9090@gmail.com";
				// Get the Session object.
				Session session = Session.getInstance(props,
						new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(username, password);
							}
						});
				
				try {
					// Create a default MimeMessage object.
					Message message = new MimeMessage(session);
					
					// Set From: header field of the header.
					message.setFrom(new InternetAddress(from));
					
					// Set To: header field of the header.
					message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse(to));
					
					// Set Subject: header field
					message.setSubject("Testing Subject");
					
					// Now set the actual message
					message.setText("Hello, this is sample for to check send " +
							"email using JavaMailAPI ");
					
					// Send message
					Transport.send(message);
					
					System.out.println("Sent message successfully....");
					
				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			}
}