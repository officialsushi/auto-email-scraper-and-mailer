package com.soohyunchoi;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
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
public class autoMail {
	final String from = "intern2@360payments.com";
	final String username = "intern2@360payments.com";// change accordingly
	final String password = "Soohyun9090!";// change accordingly
	final String host = "smtp.office365.com";
			public static void autoMail(String[] args) {
		
				String to = "soohyun9090@gmail.com";

				
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", host);
				props.put("mail.smtp.port", "25");
				
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
	
	public class SpreadsheetReader{
		private final String directory;
		public ArrayList<Index> indices = new ArrayList();
		public ArrayList<Publisher> publishers = new ArrayList();
		public ArrayList<Index> userCategories = new ArrayList();
		
		/**
		 *
		 * @param directory: directory of spreadsheet database, white space separated values
		 * @throws Exception
		 */
		public SpreadsheetReader(String directory, int readOption) throws Exception {
			this.directory = directory;
			spreadsheetReader(readOption);
		}
		
		/**
		 * read spreadsheets
		 * @throws Exception
		 */
		public void spreadsheetReader(int readOption) throws Exception {
			if(readOption == 0){
				ArrayList<ArrayList<String>> lists = whiteSpaceSeparatedFileToLists();
				genCategoryIndices(lists.get(0));
				userChooseCategories();
				listsToObjects(lists);
			}
			else if (readOption == 1){
				ArrayList<ArrayList<String>> lists = commaSeparatedFileToLists();
				genCategoryIndices(lists.get(0));
				userChooseCategories();
				listsToObjects(lists);
			}
			else
				throw new IllegalArgumentException("Illegal argument, gotta be 0 for white space separated or 1 for comma separated");
		}
		
		/**
		 * @RETURN arraylist of Index objects of what they want
		 */
		public void userChooseCategories() {
			Scanner in = new Scanner(System.in);
			System.out.println("\n=============================================");
			System.out.println(">> Select categories of interest or -1 for all:");
			for (int i = 0; i < indices.size(); i++){
				System.out.println(i+1 + ". " + indices.get(i).getCategory());
			}
			boolean keepGoing = true;
			while (keepGoing){
				int input = in.nextInt();
				if (input == -1){
					for (Index a : indices){
						userCategories.add(a);
					}
					in.close();
					return;
				}
				if (input == 0){
					in.close();
					return;
				}
				userCategories.add(indices.get(input-1));
				System.out.println(indices.get(input-1).getCategory());
				System.out.println("Enter next category or 0 if finished selecting");
			}
		}
		
		/**
		 * generates from what index to what index each category is
		 */
		private void genCategoryIndices (ArrayList<String> cats) {
			System.out.print("Generating category indices... ");
			String cat;
			int i = 0;
			while (i < cats.size()) {
				cat = cats.get(i);
				int n = i;
				while (i + 1 < cats.size() && cats.get(i).equals(cats.get(i + 1))) {
					i++;
				}
				Index newIndices = new Index(cat, n, i);
				indices.add(newIndices);
				i++;
			}
			System.out.println("Category indices generated!");
		}
		
		/**
		 * gets file with white space separated values (must be scrubbed)
		 * turns each column into list
		 *
		 * @returns ArrayList[0] = Categories, [1] = Website top level url, [2] = Website submission page url
		 *
		 * @throws Exception
		 */
		private ArrayList<ArrayList<String>> whiteSpaceSeparatedFileToLists() throws Exception {
			System.out.print("Converting database file to list... ");
			final int numberDatabaseColumns = 3;
			String token;
			File file = new File(directory);
			Scanner inFile = new Scanner(file);
			ArrayList<ArrayList<String>> lists = new ArrayList();
			lists.add(new ArrayList<>());
			lists.add(new ArrayList<>());
			lists.add(new ArrayList<>());
			int counter = numberDatabaseColumns;
			while (inFile.hasNext()){
				token = inFile.next();
				lists.get(counter % numberDatabaseColumns).add(token);
				counter++;
			}
			inFile.close();
			System.out.println("Database file converted to lists!");
			return lists;
		}
		
		/**
		 * gets file with comma separated values (must be scrubbed)
		 * turns each column into list
		 *
		 * @returns ArrayList[0] = Categories, [1] = Website top level url, [2] = Website submission page url
		 *
		 * @throws Exception
		 */
		private ArrayList<ArrayList<String>> commaSeparatedFileToLists() throws Exception {
			System.out.print("Converting database file to list... ");
			final int numberDatabaseColumns = 3;
			String token;
			File file = new File(directory);
			Scanner inFile = new Scanner(file);
			ArrayList<ArrayList<String>> lists = new ArrayList();
			lists.add(new ArrayList<>());
			lists.add(new ArrayList<>());
			lists.add(new ArrayList<>());
			int counter = numberDatabaseColumns;
			while (inFile.hasNextLine()){
				token = inFile.nextLine();
				while(token.contains(",")){
					String cutToken = token.substring(0, token.indexOf(","));
					token = token.substring(token.indexOf(",")+1);
					lists.get(counter % numberDatabaseColumns).add(cutToken);
					counter++;
				}
				lists.get(counter % numberDatabaseColumns).add(token);
				counter++;
			}
			inFile.close();
			System.out.println("Database file converted to lists!");
			
			return lists;
		}
		
		/**
		 * Uses indices ArrayList to only parse the right categories
		 * @param lists list of lists of the different data value types
		 * @throws Exception
		 */
		private void listsToObjects(ArrayList<ArrayList<String>> lists) throws Exception {
			System.out.print("Converting lists to objects... ");
			JavaScriptScraper.createAndStartService();
			String currentDateAndTime = java.time.LocalDate.now().toString() + " " + LocalTime.now().toString();
			PrintWriter outFile = new PrintWriter(new File("Results for " + currentDateAndTime + ".csv"));
			for (int n = 0; n < userCategories.size(); n++){
				System.out.println("User's categories #" + n + 1 + " | Category: " + userCategories.get(n).getCategory() + " | Start/End: " + userCategories.get(n).getStart() + "/" + userCategories.get(n).getEnd() + "\n");
				for (int i = userCategories.get(n).getStart(); i < userCategories.get(n).getEnd(); i++){
					Publisher publisher = new Publisher((lists.get(0)).get(i), (lists.get(1)).get(i), (lists.get(2)).get(i));
					publishers.add(publisher);
					outFile.println(publisher.getCategory() + "," + publisher.getSubmission() + "," + publisher.getEmail() + "," + publisher.getUrl());
				}
			}
			outFile.close();
			JavaScriptScraper.stopService();
			System.out.println("Converted lists to objects!");
		}
		
		
	}
		
		
		
		
		
		}