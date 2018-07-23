package com.soohyunchoi;

/**
 * publisher object
 *
 * @author sushi
 * @version 0.1
 */

public class Publisher {
    private final String category, email, submission;
    private String url;
	public boolean failedConnection;
    public Publisher(String category, String url, String submission) {
		this.category = category;
		this.url = url;
		this.submission = submission;
		this.email = scrapeForEmail();
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
	public String toString() {
		return String.format("%23s  |%34s  |%40s  |%2s%-40s", this.category, this.url, this.email, "", this.submission);
	}
	public boolean isFailedConnection() { return failedConnection; }
	public String getCategory() { return category; }
	public String getUrl(){ return url; }
	public String getSubmission(){ return submission;}
	public String getEmail() { return email;}

}
