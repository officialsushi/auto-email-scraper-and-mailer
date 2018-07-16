package com.soohyunchoi;

/**
 * publisher object
 *
 * @author sushi
 * @version 0.1
 */

public class Publisher {
    private final String category, url, email, submission;
	public boolean failedConnection;
    public Publisher(String category, String url, String submission) throws Exception{
		this.category = category;
		this.url = url;
		this.submission = submission;
		this.email = scrapeForEmail();
    }
	public Publisher(String category, String url, String email, String submission){
		this.category = category;
		this.url = url;
		this.submission = submission;
		this.email = email;
	}
    private String scrapeForEmail() throws Exception{
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
