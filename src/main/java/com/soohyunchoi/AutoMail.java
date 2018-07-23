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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 * Gangin.
 */
@SuppressWarnings("Duplicates")
public class AutoMail {
	private static String OS = System.getProperty("os.name");
	private String port;
	private String from;
	private String username;
	private String password;
	private String host;
	private String databaseName;
	private ArrayList<String> sentContacts = new ArrayList();
	private static Properties props = new Properties();
	public ArrayList<Publisher> publishers = new ArrayList();
	
	public AutoMail() {
		setUpProperties();
	}
	public AutoMail(String databaseName, String email, String pw, String host, String port) {
		this.databaseName = databaseName;
		this.username = email;
		this.from = email;
		this.password = pw;
		this.host = host;
		this.port = port;
		setUpProperties();
	}
	/**
	 * Sets mail properties
	 */
	private void setUpProperties() {
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
//		props.put("mail.smtp.port", "25");
	}
	/**
	 * full sends only.
	 * @throws Exception
	 */
	public void sendIt() throws MessagingException, IOException {
		getContacts(databaseName);
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
	public void getContacts(String file) throws IOException {
		// read spreadsheet
		try {
			SpreadsheetReader fileReader = new SpreadsheetReader(file, true);
			publishers = fileReader.getPublishers();
		} catch (IOException e){
			System.out.println("ERROR: Can't read spreadsheet. You may have inputted the wrong file, or terminated the program before the email scraper could finish.");
			throw new IOException();
		}
		// remove publisher objects w/ no email (null)
		for (int i = 0; i < publishers.size(); i++) {
			if (publishers.get(i).getEmail().equals("null"))
				publishers.remove(i);
		}
	}
	/**
	 * Generates mimemessages
	 * @return array of mimemessages for every publisher
	 */
	public ArrayList<MimeMessage> generateEmails() throws AddressException{
		ArrayList<MimeMessage> messages = new ArrayList();
		for (Publisher a : publishers) {
			String emailHtml = "";
			try {
				Scanner in = new Scanner(new File("email.html"));
				while (in.hasNext()){
					String token = in.next();
					if (token.contains("[COMPANY]"))
						token = token.replace("[COMPANY]", a.getUrl());
					if (token.contains("[company]"))
						token = token.replace("[company]", a.getUrl());
					emailHtml += " " + token;
				}
			} catch(IOException e){
				System.out.println("No email.html! Please put one in in the root directory! Exiting...");
				System.exit(0);
			}
			
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
				throw new AddressException();
			}
		}
		return messages;
	}
	public void sendMail(MimeMessage message) throws MessagingException {
		String recipientEmail = message.getRecipients(Message.RecipientType.TO)[0].toString();
		System.out.print("Sending to " + recipientEmail + "...");
		try {
			Transport.send(message);
			PrintWriter printerWriter = new PrintWriter(new FileWriter(new File("sentEmails.txt"), true));
			printerWriter.println(recipientEmail);
			sentContacts.add(recipientEmail);
			printerWriter.close();
			try{
				PrintWriter logger;
				if (OS.indexOf("win") >= 0) {
					logger = new PrintWriter(new FileWriter(new File("sentMail\\" + recipientEmail + ".txt")));
				}
				else {
					logger = new PrintWriter(new FileWriter(new File("sentMail/" + recipientEmail + ".txt")));
				}
				logger.println("Email sent to " + recipientEmail + ": \n");
				logger.println(message.getContent());
				logger.close();
			} catch (Exception a) {
				System.out.print(" Couldn't write log... ");
			}
				System.out.println(" Success!\n");
		} catch (Exception e) {
			System.out.println(" Failed! Exception: " + e + "\n");
		}
	}
	public static void clearSentEmails(){
		try {
			new PrintWriter("sentEmails.txt").close();
		} catch(FileNotFoundException e){
			System.out.println("Looks like sentEmails.txt was deleted. Please manually make a " +
					"new text file called 'sentEmails.txt' (case sensitive) in the root directory.\nExiting...");
		}
	}
	
}