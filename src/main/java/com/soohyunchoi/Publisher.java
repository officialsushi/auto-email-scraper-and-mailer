package com.soohyunchoi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * publisher object
 *
 * @author sushi
 * @version 0.1
 */

public class Publisher {
    private final String category, email;
    private String submission;
    private String url;
	public boolean failedConnection;
	
    public Publisher(String category, String url, String submission) {
		this.category = category;
		this.url = url;
		this.submission = submission;
		this.email = scrapeForEmail();
    }
    public Publisher(String category, String url) {
		this.category = category;
		this.url = url;
		this.email = scrapeForEmailNoSub();
    }
	public Publisher(String category, String submission, String email, String url){
		this.category = category;
		if (url != null) {
			try {
				String urlFirstLetterCapitalized = url.substring(0, 1).toUpperCase();
				String betterUrl = urlFirstLetterCapitalized + url.substring(1, url.indexOf("."));
				if (betterUrl.equals("Gmail"))
					this.url = "there";
				else
					this.url = betterUrl;
			}catch(Exception e){
				this.url = url;
				System.out.println("Url fuckery failed");
			}
		}
		else
			this.url = url;
		this.submission = submission;
		this.email = email;
	}
    private String scrapeForEmail() {
        if (submission != null) {
            WebScraper webScraper = new WebScraper(submission);
            String emailFromScraper = webScraper.getEmail();
            this.failedConnection = webScraper.isFailedConnection();
            return emailFromScraper;
        } else
            throw new IllegalArgumentException("Submission url is NULL");
    }
    private String scrapeForEmailNoSub() {
    	String[] CONTACTPAGEENDS = {"/write-for-us/", "/contribute/", "/submit-guest-post/", "/guest-posts/", "/guest-post/", "/contact-us/"};
		String contactPageEndsFromFile = "";
    	try {
			Scanner in = new Scanner(new File("contactPageEnds.txt"));
			while(in.hasNext()) {
				contactPageEndsFromFile += in.next();
			}
			CONTACTPAGEENDS = contactPageEndsFromFile.split(",");
		}catch(IOException e){
    		System.out.println("Could not read contactPageEnds file. Using default contact ends...");
		}
		String emailFromScraper = null;
		int index = 0;
		if (url != null) {
			String syntheticSubmission = null;
			while (emailFromScraper == null && index < CONTACTPAGEENDS.length) {
				syntheticSubmission = "http://www." + url+CONTACTPAGEENDS[index];
				WebScraper webScraper = new WebScraper(syntheticSubmission);
				emailFromScraper = webScraper.getEmail();
				System.out.println(emailFromScraper);
				this.failedConnection = webScraper.isFailedConnection();
				index++;
			}
			this.submission = syntheticSubmission;
		}
		return emailFromScraper;
	}
	public String toString() {
		return String.format("%23s  |%34s  |%40s  |%2s%-40s", this.category, this.url, this.email, "", this.submission);
	}
	public boolean isFailedConnection() { return failedConnection; }
	public String getCategory() { return category; }
	public String getUrl(){ return url; }
	public String getSubmission(){ return submission;}
	public String getEmail() { return email;}

}
