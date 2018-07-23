package com.soohyunchoi;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Scanner;

/**
 * July 2: all had ~22% efficiency
 * July 3 pre-selenium: all had ~30% efficiency
 * July 5 pre-selenium: all had ~30% efficiency
 * July 10 selenium: all had ~43% efficiency
 * July 12 selenium: all had ~50% efficiency
 */

public class Main {
    public static void main(String[] args) {
		userMenu();
    }
    public static void userMenu(){
		Scanner in = new Scanner(System.in);
		System.out.print(
				"Welcome to fullsendanator live release 1.0.\n" +
				"Built by Soohyun Choi for 360 Payments in Campbell\n" +
				"https://github.com/officialsushi/full-send-anator | soohyunchoi.com\n"+
				"-------------------------------------------------------------------\n"
		);
		System.out.print(
				"\nPlease select a number 1-3:\n" +
				"1. Scrape Emails\n" +
				"2. Auto Mail\n"
		);
		boolean userChoiceValid = false;
		int userChoice = 0;
		while(!userChoiceValid){
			try{
				userChoice = in.nextInt();
				userChoiceValid = true;
			} catch (Exception e){
				System.out.println("Invalid choice - try '1', '2', or '3'");
			}
		}
		
		// Scrape emails
		if (userChoice == 1)
			scrapeEmails();
		else if (userChoice == 2)
			autoMail();
		else if (userChoice == 3) {
			scrapeEmails();
			autoMail();
		}
		else
			System.out.println("Invalid choice! Exiting...");
	}
	public static void autoMail(){
		Scanner in = new Scanner(System.in);
		// clearing emails
		boolean advance = false;
		String userChoice;
		while(!advance) {
			System.out.println("Would you like to clear the sent email addresses cache? (Y/N)");
			System.out.println("Type 'help' for more info");
			userChoice = in.next();
			if (userChoice.equalsIgnoreCase("help")){
				System.out.println("When a successful email is sent, the email address that you sent to is stored " +
						"in a text file called 'sentEmails.txt' so that you don't send duplicate emails to the" +
						"same email address. Clearing it means the script is allowed to send emails to these " +
						"addresses again. You can edit it manually to remove specific emails.");
			}
			if (userChoice.equalsIgnoreCase("Y")) {
				AutoMail.clearSentEmails();
				advance=true;
			}
			if (userChoice.equalsIgnoreCase("N"))
				advance=true;
		}
		// if they have a custom file it gets complicated. but its there.
		System.out.println("Do you have a custom email database file? (Usually no) (Y/N)");
		String hasCustomEmailDB = in.next();
		
		// custom email database file!!!
		if (hasCustomEmailDB.equalsIgnoreCase("Y")) {
			boolean userChoiceValid = false;
			while(!userChoiceValid) {
				System.out.println("Enter the name of your database file, ending in .csv");
				System.out.println("NOTE: All files should be in root directory!!");
				try {
					String directory = in.next();
					System.out.println("Please enter email to send from:");
					String email = in.next();
					System.out.println("Please enter password:");
					String pw = in.next();
					System.out.println("Is your email host smtp.office365.com? (Probably yes) (Y/N)");
					AutoMail autoMail;
					if (in.next().equalsIgnoreCase("Y")) {
						autoMail = new AutoMail(directory, email, pw, "smtp.office365.com", "587");
						userChoiceValid = true;
					}
					else {
						System.out.println("Enter the smtp url for your email host");
						String smtp = in.next();
						System.out.println("Enter the smtp port");
						String port = in.next();
						autoMail = new AutoMail(directory, email, pw, smtp, port);
						userChoiceValid = true;
					}
					// sendit!!!
					autoMail.sendIt();
				} catch (Exception e) {
					System.out.println("Error! Something went wrong.");
					System.out.println("Continue? (Y/N)");
					userChoiceValid = false;
					if (in.next().equalsIgnoreCase("N")) {
						System.out.println("Exiting...");
						System.exit(0);
					}
				}
			}
		}
		else if (hasCustomEmailDB.equalsIgnoreCase("N")) {
			boolean userChoiceValid = false;
			while (! userChoiceValid) {
				try {
					System.out.println("Please enter email to send from:");
					String email = in.next();
					System.out.println("Please enter password:");
					String pw = in.next();
					System.out.println("Is your email host smtp.office365.com? (Probably yes) (Y/N)");
					AutoMail autoMail;
					if (in.next().equalsIgnoreCase("Y")) {
						autoMail = new AutoMail("scrapedDatabase.csv", email, pw, "smtp.office365.com", "587");
						userChoiceValid = true;
					} else {
						System.out.println("Enter the smtp url for your email host");
						String smtp = in.next();
						System.out.println("Enter the smtp port");
						String port = in.next();
						autoMail = new AutoMail("scrapedDatabase.csv", email, pw, smtp, port);
						userChoiceValid = true;
					}
					// sendit!!!
					autoMail.sendIt();
				} catch (Exception e) {
					System.out.println("Error! Something went wrong. Probably incorrect login credentials.");
					System.out.println("Continue? (Y/N)");
					userChoiceValid = false;
					if (in.next().equalsIgnoreCase("N")) {
						System.out.println("Exiting...");
						System.exit(0);
					}
				}
			}
		}
		else
			System.out.println("Invalid input. Exiting...");
	}
	public static void scrapeEmails(){
		LocalTime startTime = LocalTime.now();
    	Scanner in = new Scanner(System.in);
		boolean userChoiceValid = false;
		SpreadsheetReader reader = null;
		while(!userChoiceValid){
			System.out.println("Enter the name of your database file, ending in .csv");
			System.out.println("NOTE: All files should be in root directory!!");
			try {
				String directory = in.next();
				System.out.println("Is the write for us contact page included? (Y/N)");
				boolean contactInc = false;
				if (in.next().equalsIgnoreCase("Y"))
					contactInc = true;
					reader = new SpreadsheetReader(directory, false, contactInc);
					userChoiceValid = true;
			} catch (Exception e){
				System.out.println(e);
				System.out.println("Error! The file name is probably wrong or the file is in the wrong place.");
				System.out.println("Continue? (Y/N)");
				if (in.next().equalsIgnoreCase("N")) {
					System.out.println("Exiting...");
					System.exit(0);
				}
			}
		}
		printPublishers(reader);
		reader.evaluatePerformance(startTime);
	}
	public static void printPublishers(SpreadsheetReader a){
		for (int i = 0; i < a.publishers.size(); i++) {
			System.out.println(a.publishers.get(i));
		}
	}
}
