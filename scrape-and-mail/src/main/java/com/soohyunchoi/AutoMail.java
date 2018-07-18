package com.soohyunchoi;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
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
public class AutoMail {
	final private String from = "krussell@360payments.com";
	final private String username = "krussell@360payments.com";// change accordingly
	final private String password = "Kc@2017#!";               // change accordingly
	final private String host = "smtp.office365.com";
	private ArrayList<String> sentContacts = new ArrayList();
	private static Properties props = new Properties();
	public ArrayList<Publisher> publishers = new ArrayList();
	
	public AutoMail() {
		setUpProperties();
	}
	private void setUpProperties() {
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "25");
	}
	
	/**
	 * full sends only.
	 * @throws Exception
	 */
	public void sendIt() throws Exception {
		getContacts("scrapedDatabase.csv");
		// generate array of contacts we already sent an email to
		Scanner inFile = new Scanner(new File("sentEmails.txt"));
		while (inFile.hasNext()){
			sentContacts.add(inFile.next());
		}
		inFile.close();
		ArrayList<MimeMessage> messages = generateEmails();
		for (MimeMessage a : messages){
			if (a != null){
				if(!a.getRecipients(Message.RecipientType.TO)[0].toString().equals("null")) {
					if (! (sentContacts.contains(a.getRecipients(Message.RecipientType.TO)[0].toString())))
						sendMail(a);
					else
						System.out.println("Already sent to " + a.getRecipients(Message.RecipientType.TO)[0] + ", skipping...\n");
				}
			}
			else
				System.out.println("Message is null!\n");
		}
	}
	
	public void getContacts(String file) {
		// read spreadsheet
		try {
			SpreadsheetReader fileReader = new SpreadsheetReader(file, true);
			publishers = fileReader.getPublishers();
		} catch (IOException e){
			System.out.println("Can't read spreadsheet.");
		}
		// remove publisher objects w/ no email (null)
		for (int i = 0; i < publishers.size(); i++) {
			if (publishers.get(i).getEmail().equals("null"))
				publishers.remove(i);
		}
	}
	public ArrayList<MimeMessage> generateEmails () {
		ArrayList<MimeMessage> messages = new ArrayList();
		for (Publisher a : publishers) {
			String emailHtml =
					// this is a mess. sorry.
					"<html>\n" +
					"<head>\n" +
					"    <style>\n" +
					"        body{\n" +
					"            padding: 20px, 20px, 20px, 20px;}\n" +
					"        p {\n" +
					"            font-family: \"Arial\", \"Helvetica\", \"Verdana\", \"Geneva\", sans-serif;\n" +
					"            font-size: 15px;\n" +
					"            font-style: normal;\n" +
					"            font-variant: normal;\n" +
					"            line-height: 20px;}\n" +
					"        a {\n" +
					"            text-decoration: none;}\n" +
					"        .sigPic{\n" +
					"            width: 130px; \n" +
					"            height: 47.08px;}\n" +
					"    </style>\n" +
					"    <meta charset=\"UTF-8\">\n" +
					"    </head>\n" +
					"    <body>\n" +
					"        <p>\n" +
					
					// the good shit is
					"        Hi " +
					a.getUrl() +
					",<br><br>\n" +
					"        We’re 360 Payments, a Bay Area payments company, and I'm reaching out because we’d love to contribute an article to your blog. We are happy to return the favor by publishing an article authored by your team to our thousands of customers and connections on social media.<br>\n" +
					"            <br>\n" +
					"        We’ve written hundreds of articles read by thousands of business professionals, which are shown <a href=\"https://www.360payments.com/blog/\">here</a>. I am also open to writing a customized article that works for your audience. I can author articles on the payment industry, entrepreneurship and women in leadership.<br>\n" +
					"            <br>\n" +
					"        I look forward to your response, and please do not hesitate to contact me with questions.<br>\n" +
					"            <br>\n" +
					"        Thanks!<br>\n" +
					"        <br>\n" +
					"        <strong>Katie Russell</strong><br>\n" +
					"        Digital Marketing Manager<br>\n" +
					"        <img alt=\"360 Payments\" class=\"sigPic\" src=\"https://www.360payments.com/wp-content/uploads/2017/02/logo-1.png\"><br>\n" +
					"        Direct: (215) 808-5678<br>\n" +
					"        <a href =\"https://www.360payments.com/\">www.360payments.com</a><br>\n" +
					"        <a href =\"https://www.linkedin.com/in/kate-e-russell\">www.linkedin.com/in/kate-e-russell</a><br>\n" +
					"        </p>\n" +
					"    </body>\n" +
					"</html>";
			try {
				// Get the Session object.
				Session session = Session.getInstance(props,
						new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(username, password);
							}
						});
				// Create a default MimeMessage object.
				MimeMessage message = new MimeMessage(session);
				// Set From: header field of the header.
				message.setFrom(new InternetAddress(from));
				// Set Subject: header field
				message.setSubject("We love your blog!");
				// Set To: header field of the header.
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(a.getEmail()));
				// Now set the actual message
				message.setContent(emailHtml, "text/html; charset=utf-8");
				messages.add(message);
			} catch (MessagingException e) {
				System.out.println("Failed to create email for " + a);
			}
		}
		return messages;
	}
	
	public void sendMail(MimeMessage message) throws Exception {
		String recipientEmail = message.getRecipients(Message.RecipientType.TO)[0].toString();
		System.out.print("Sending to " + recipientEmail + "...");
		try {
			Transport.send(message);
			PrintWriter printerWriter = new PrintWriter(new FileWriter(new File("sentEmails.txt"), true));
			printerWriter.println(recipientEmail);
			sentContacts.add(recipientEmail);
			printerWriter.close();
			try{
				PrintWriter logger = new PrintWriter(new FileWriter(new File("sentMail/"+recipientEmail+".txt")));
				logger.println("Email sent to " + recipientEmail + ": \n");
				logger.println(message.getContent());
				logger.close();
			} catch (Exception a) { System.out.print(" Couldn't write log... "); }
			System.out.println(" Success!\n");
		} catch (Exception e) {
			System.out.println(" Failed!\n");
		}
	}
	public static void clearSentEmails() throws FileNotFoundException{
		new PrintWriter("sentEmails.txt").close();
	}
	public static void main(String args[]) throws Exception {
		AutoMail automail = new AutoMail();
		automail.sendIt();
		System.out.println("Done");
	}
	
}