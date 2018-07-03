package com.soohyunchoi;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author sushi choi
 * @version v0.1
 */


public class WebScraper {

    private final String url, urlSuffix;
    private final String[] pageText;
    private Document document;
    private String email;
    private boolean failedConnection;
	private static String[] commonTLDs = {".com", ".org", ".uk", ".net"};
    
    public WebScraper(String url) throws Exception{
        this.url = url;
        this.pageText = scrape();
		this.urlSuffix = parseSuffix();
		this.email = findEmail();
    }
	
	/**
	 * Connect and scrape entire HTML page
	 * @return string[] of each word from scrape
	 * @throws Exception
	 */
	private String[] scrape() throws Exception {
		boolean internetWorks = false;
		while(!internetWorks) {
			try {
				Jsoup.connect("https://www.google.com/").timeout(3000);
				internetWorks = true;
			} catch (Exception e){
				System.out.print("\u001b[31mInternet down, retrying in 3...");
				Thread.sleep(2000);
				System.out.print(" 2...");
				Thread.sleep(2000);
				System.out.print(" 1...\u001b[0m");
				Thread.sleep(2000);
			}
		}
		
        System.out.print("Attempting to scrape " + url + " ... ");
        try {
        	document = Jsoup.connect(url).timeout(8000).get();
            String paragraph = document.select("p").text();
            String[] split = paragraph.split("\\s+");
			System.out.println("Success!");
			failedConnection = false;
			return split;
		} catch (SocketTimeoutException e){
			System.out.println("\n\u001b[31mDead link!\u001b[0m\n");
			failedConnection = true;
			return null;
		} catch (SocketException e){
			System.out.println("\n\u001b[31mDead link!\u001b[0m\n");
			failedConnection = true;
			return null;
		} catch (HttpStatusException e){
			System.out.println("\n\u001b[31mDead link!\u001b[0m\n");
			failedConnection = true;
			return null;
		} catch (Exception a){
			System.out.println(a + "\n");
			failedConnection = true;
			return null;
		}
    }
	/**
	 *  find email from string[] of tokens scraped from webpage
	 * @return the email, all cleaned up or null
	 * @throws MalformedURLException
	 */
	private String findEmail(){
        if (pageText == null){
            return null;
        }
        System.out.println("> Finding email...");
		Token tokens = new Token();
        for (int i = pageText.length-1; i >= 0; i--) {
        	//pretoken
        	if (i-1 > 0)
        		tokens.setPreToken(pageText[i-1]);
        	else
        		tokens.setPreToken(null);
			tokens.setToken(pageText[i]);
			//posttoken
        	if (i + 1 < pageText.length)
        		tokens.setPostToken(pageText[i+1]);
        	else
        		tokens.setPostToken(null);
        	//postposttoken
			if (i + 2 < pageText.length)
        		tokens.setPostPostToken(pageText[i+2]);
			else
				tokens.setPostPostToken(null);
			//check if valid
			if(isValidEmail(tokens)) {
				System.out.println("Found email, cleaning " + pageText[i] + "...");
				System.out.println(">> MONEY!!: " + "\u001b[32m" + plainTextEmailCleaner(email) + "\u001b[0m\n");
				return plainTextEmailCleaner(email);
			}
			
		}
        System.out.print("Email not found in plaintext, attempting to find in href... ");
        try {
			return findEmailFromHref();
		} catch (Exception e){
        	System.out.println(e);
		}
        return null;
    }
	
	/**
	 * checks to see if the scraped token with @ is an email, and makes it ready for cleaning
	 * @param tokens Token object
	 * @return true or false if the @ is part of an email
	 */
	private boolean isValidEmail(Token tokens) {
		
		boolean isValid = false;
		String crawledEmail = tokens.getToken();
		String postToken = tokens.getPostToken();
		String postPostToken = tokens.getPostPostToken();
		String preToken = tokens.getPreToken();
		
		if ( (crawledEmail.contains("@") ) )
			isValid = true;
		String[] badAtSignage = {"[at]", "[@]", "(at)", "(@)"};
		for (int i = 0; i < badAtSignage.length; i++){
			if (crawledEmail.contains(badAtSignage[i])) {
				isValid = true;
				crawledEmail = crawledEmail.replace(badAtSignage[i], "@");
			}
		}
		if (!isValid) {
			return false;
		}
		
		System.out.print("Checking to see if " + crawledEmail + " is a valid email... ");
		//decrypt shitty encryption for @ sign
		for (int i = 0; i < badAtSignage.length; i++){
			if(crawledEmail.contains(badAtSignage[i])){
				crawledEmail = crawledEmail.replace(badAtSignage[i], "@");
			}
		}
		//check if its social media or anything with @bullshit
		if ( crawledEmail.indexOf("@") == 0
				&& !containsValidSuffix(crawledEmail)
				&& postToken != null
				&& !containsValidSuffix(postToken)
				&& postPostToken != null
				&& !containsValidSuffix(postPostToken) ) {
			System.out.println(crawledEmail + " is NOT valid!");
			return false;
		}
		//check if they separated urlSuffix from back
		if (postPostToken != null && containsValidSuffix(postPostToken))
			postToken += postPostToken;
		//check if the thing only picked up the @ sign since they did front @ back.com
		if (preToken != null && postToken != null && crawledEmail.length() == 1)
			crawledEmail = preToken + crawledEmail + postToken;
		//checking to if its @foo.com
		if (preToken != null && crawledEmail.indexOf("@") == 0 && containsValidSuffix(postToken))
			crawledEmail = preToken + crawledEmail;
		//checking to see if its foo@
		if (postToken != null && crawledEmail.indexOf("@") == crawledEmail.length()-1)
			crawledEmail = crawledEmail + postToken;
		int length = crawledEmail.length();
		String back = crawledEmail.substring(crawledEmail.indexOf("@")+1, length);
		//check for foo@bar[dot]com
		String[] badDotSignage = {"[dot]", "(dot)"};
		for (int i = 0; i < badDotSignage.length; i++){
			if (back.contains(badDotSignage[i])) {
				back = back.replace(badDotSignage[i], "@");
			}
		}
		if (crawledEmail == null) {
			System.out.println(crawledEmail + " is NOT valid!");
			return false;
		}
		email = crawledEmail;
		// does the back contain a valid suffix?
		if (crawledEmail.contains("@") && containsValidSuffix(back)) {
			System.out.println(crawledEmail + " is valid!");
			return true;
		}
		System.out.println(crawledEmail + " is NOT valid!");
		return false;
	}
	private boolean containsValidSuffix(String token){
		if(token == null)
			return false;
		if(token.contains(urlSuffix))
			return true;
		for (String end : commonTLDs){
			if(token.contains(end))
				return true;
		}
		return false;
	}
	
	private String getCorrectTLD(String crawledEmail){
		if (crawledEmail.contains(urlSuffix))
			return urlSuffix;
		for (String end : commonTLDs){
			if (crawledEmail.contains(end))
				return end;
		}
		return null;
	}
	
	/**
	 * if the email is found in plain text, this cleans it
	 * @param crawledEmail
	 * @return cleaned email
	 */
	private String plainTextEmailCleaner (String crawledEmail) {
		String correctSuffix = getCorrectTLD(crawledEmail);
		String front = crawledEmail.substring(0, crawledEmail.indexOf("@"));
		String back = crawledEmail.substring(crawledEmail.indexOf("@")+1, crawledEmail.length());
		//checking and cleaning if [foo]@bar.com
		if (front.startsWith("["))
			front = front.substring(1, front.length());
		if (front.endsWith("]"))
			front = front.substring(0, front.length()-1);
		if (front.contains(":"))
			front = front.substring(front.indexOf(":") + 1);
		//checking and cleaning if foo@bar.com doesn't end in .com so should take care of everything else
				if (! (back.endsWith(correctSuffix))) {
					back = back.substring(0, back.lastIndexOf(correctSuffix) + correctSuffix.length());
				}
		return front + "@" + back;
	}
	
	/**
	 * @return href data if it has email inside
	 * @throws Exception
	 */
	private String findEmailFromHref() throws Exception {
		Elements links = document.select("p>a[href]");
		for (int i = links.size()-1; i >= 0; i--) {
			String scrapedEmail = links.get(i).attr("href");
			if( scrapedEmail.contains("@") ){
				if(scrapedEmail.indexOf("@") == 0){
					notFoundInHref();
					return null;
				}
				String front = scrapedEmail.substring(0, scrapedEmail.indexOf("@"));
				String back = scrapedEmail.substring(scrapedEmail.indexOf("@")+1);
				if (containsValidSuffix(back)) {
					System.out.println("Success!");
					scrapedEmail = front + "@" + back;
					System.out.println(scrapedEmail);
					System.out.println("Found email, cleaning " + scrapedEmail + "...\nMONEY!!: \u001b[32m" + hrefEmailCleaner(scrapedEmail) + "\u001b[0m\n");
					return hrefEmailCleaner(front + "@" + back);
				}
			
			}
		}
		notFoundInHref();
		return null;
	}
	
	private static void notFoundInHref(){
		System.out.println("Not found in href!");
		System.out.println("\u001b[37mNo email on page!\u001b[0m\n");
	}
	
	/**
	 * cleans scraped emails from href
	 * @param crawledEmail confirmed potential email that is not clean
	 * @return cleaned email
	 */
	private String hrefEmailCleaner (String crawledEmail) throws Exception{
		String correctSuffix = getCorrectTLD(crawledEmail);
		//split foo@bar.com into foo / bar.com
		String front = crawledEmail.substring(0, crawledEmail.indexOf("@"));
		String back = crawledEmail.substring(crawledEmail.indexOf("@")+1, crawledEmail.length());
		//checking for foo@bar.com/subject? afsfasf or whatever wonky shit comes after the actual address
		if (! (back.indexOf(correctSuffix) == back.length() - correctSuffix.length()))
			back = back.substring(0, back.indexOf(correctSuffix) + correctSuffix.length());
		//checking for mailto: or other prefixes
		if (front.contains(":"))
			front = front.substring(front.indexOf(":") + 1);
		return front + "@" + back;
	}
	private String parseSuffix(){
		try {
			URL url = new URL(this.url);
			String[] domainNameParts = url.getHost().split("\\.");
			String tldString = domainNameParts[domainNameParts.length - 1];
			return "."+tldString;
		}
		catch(MalformedURLException e){
			return ".uk";
		}
		catch(Exception e){
			System.out.println(e);
			return ".uk";
		}
	}
	public boolean isFailedConnection() {
		return failedConnection;
	}
	public String getUrl(){
        return url;
    }
	public String getEmail() {
        return email;
    }

}

