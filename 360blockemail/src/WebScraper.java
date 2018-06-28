import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

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
			}
			catch (Exception e){
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
//            System.out.println(document);
            String paragraph = document.select("p").text();
            String[] split = paragraph.split("\\s+");
//            for (String a : split) {
//                System.out.println(a);
//            }
			System.out.println("Success!");
			failedConnection = true;
			return split;
		}
        catch (SocketTimeoutException e){
			System.out.println("\n\u001b[31mFailed connection!\u001b[0m\n");
			failedConnection = false;
			return null;
		}
        catch (SocketException e){
			System.out.println("\n\u001b[31mFailed connection!\u001b[0m\n");
			failedConnection = false;
			return null;
		}
        catch (HttpStatusException e){
			System.out.println("\n\u001b[31mFailed connection!\u001b[0m\n");
			failedConnection = false;
			return null;
		}
        catch (Exception a){
			System.out.println(a + "\n");
			failedConnection = false;
			return null;
		}
    }
	/**
	 *  find email from string[] of tokens scraped from webpage
	 * @return the email, all cleaned up or null
	 * @throws MalformedURLException
	 */
	private String findEmail() throws Exception{
        if (pageText == null){
            return null;
        }
        System.out.println("> Finding email...");
        for (int i = pageText.length-1; i >= 0; i--) {
			if (i + 1 > pageText.length-1 && i - 1 >= 0) {
				if(isValidEmail(pageText[i], pageText[i - 1])) {
					System.out.println("Found email, cleaning " + pageText[i] + "...");
					System.out.println(">> MONEY!!: " + "\u001b[32m" + plainTextEmailCleaner(email) + "\u001b[0m\n");
					return plainTextEmailCleaner(email);
				}
			}
			if ( i + 2 > pageText.length-1 && i - 1 >= 0 && !(i + 1 > pageText.length-1)) {
				if (i + 2 > pageText.length-1) {
					if (isValidEmail(pageText[i], pageText[i - 1], pageText[i + 1])) {
						System.out.println("Found email, cleaning " + pageText[i] + "...");
						System.out.println(">> MONEY!!: " + "\u001b[32m" + plainTextEmailCleaner(email) + "\u001b[0m\n");
						return plainTextEmailCleaner(email);
					}
				}
			}
			if (i + 2 < pageText.length && i - 1 >= 0) {
				if (isValidEmail(pageText[i], pageText[i - 1], pageText[i + 1], pageText[i + 2])){
					System.out.println("Found email, cleaning " + pageText[i] + "...");
					System.out.println(">> MONEY!!: " + "\u001b[32m" + plainTextEmailCleaner(email) + "\u001b[0m\n");
					return plainTextEmailCleaner( email );
				}
			}
			else if ( i - 1 < 0 && i + 2 < pageText.length && isValidEmail(pageText[i], pageText[i + 1], pageText[i + 2], 0)) {
				System.out.println("Found email, cleaning " + pageText[i] + "...");
				System.out.println(">> MONEY!!: " + "\u001b[32m" + plainTextEmailCleaner(email) + "\u001b[0m\n");
				return plainTextEmailCleaner(email);
			}
			else{
			}
		}
        System.out.print("Email not found in plaintext, attempting to find in href... ");
        try {
			return findEmailFromHref();
		}
		catch (Exception e){
        	System.out.println(e);
		}
        return null;
    }
	
	/**
	 * checks to see if the scraped token with @ is an email, and makes it ready for cleaning
	 * @param crawledEmail
	 * @param preToken
	 * @param postToken
	 * @param postPostToken
	 * @return true or false if the @ is part of an email
	 */
	private boolean isValidEmail(String crawledEmail, String preToken, String postToken, String postPostToken) {
		boolean isValid = false;
		if ( (crawledEmail.contains("@") ) ) {
			isValid = true;
		}
		String[] badSignage = {"[at]", "[@]", "(at)", "(@)"};
		for (int i = 0; i < badSignage.length; i++){
			if (crawledEmail.contains(badSignage[i])) {
				isValid = true;
				crawledEmail = crawledEmail.replace(badSignage[i], "@");
			}
		}
		if (!isValid) {
			return false;
		}
		System.out.print("Checking to see if " + crawledEmail + " is a valid email... ");
		//decrypt shitty encryption for @ sign
		for (int i = 0; i < badSignage.length; i++){
			if(crawledEmail.contains(badSignage[i])){
				crawledEmail.replace(badSignage[i], "@");
			}
		}
				//check if its social media or anything with @bullshit
				if (crawledEmail.indexOf("@") == 0 && ! crawledEmail.contains(urlSuffix) && ! postToken.contains(urlSuffix) && ! postPostToken.contains(urlSuffix)) {
					System.out.println(crawledEmail + " is NOT valid!");
					return false;
				}
				//check if they separated urlSuffix from back
				if (postPostToken.equals(urlSuffix)) {
					postToken += postPostToken;
					System.out.println("\n\n\n\nWow actually used the postposttoken thing!\n\n\n\n");
				}
		//check if the thing only picked up the @ sign since they did front @ back.com
		if(crawledEmail.length() == 1){
			crawledEmail = preToken + crawledEmail + postToken;
		}
		boolean b = false;
		//checking to if its @foo.com
				if (crawledEmail.indexOf("@") == 0 && crawledEmail.contains(urlSuffix)) {
					crawledEmail = preToken + crawledEmail;
				}
		//checking to see if its foo@
		if(crawledEmail.indexOf("@") == crawledEmail.length()-1){
			crawledEmail = crawledEmail + postToken;
		}
		
		int length = crawledEmail.length();
		String back = crawledEmail.substring(crawledEmail.indexOf("@")-1, length);
		if(crawledEmail == null){
			System.out.println(crawledEmail + " is NOT valid!");
			return false;
		}
		email = crawledEmail;
		
		// does the back have the suffix?
			if (crawledEmail.contains("@") && back.contains(urlSuffix)) {
				System.out.println(crawledEmail + " is valid!");
				return true;
			}
		//
		System.out.println(crawledEmail + " is NOT valid!");
		return false;
		
	}
	
	private boolean isValidEmail(String crawledEmail, String postToken, String postPostToken, int a) {
		boolean isValid = false;
		if ( (crawledEmail.contains("@") ) ) {
			isValid = true;
		}
		String[] badSignage = {"[at]", "[@]", "(at)", "(@)"};
		for (int i = 0; i < badSignage.length; i++){
			if (crawledEmail.contains(badSignage[i])) {
				isValid = true;
			}
		}
		if (!isValid) {
			return false;
		}
		
		System.out.println("Checking to see if " + crawledEmail + " is a valid email...");
		//decrypt shitty encryption for @ sign
		for (int i = 0; i < badSignage.length; i++){
			if(crawledEmail.contains(badSignage[i])){
				crawledEmail.replace(badSignage[i], "@");
			}
		}
		//check other TLDs
				//check if they separated urlSuffix from back
				if (postPostToken.equals(urlSuffix)) {
					postToken += postPostToken;
					System.out.println("\n\n\n\nWow actually used the postposttoken thing!\n\n\n\n");
				}
				//check if its social media or anything with @bullshit
				if (crawledEmail.indexOf("@") == 0 && ! crawledEmail.contains(urlSuffix) && ! postToken.contains(urlSuffix) && ! postPostToken.contains(urlSuffix)) {
					System.out.println(crawledEmail + " is NOT valid!");
					return false;
				}
		//checking to see if its foo@
		if(crawledEmail.indexOf("@") == crawledEmail.length()-1){
			crawledEmail = crawledEmail + postToken;
		}
		
		int length = crawledEmail.length();
		String front = crawledEmail.substring(0, crawledEmail.indexOf("@"));
		String back = crawledEmail.substring(crawledEmail.indexOf("@")-1, length);
		
		email = crawledEmail;
		
		// does the back have the suffix?
			if (crawledEmail.contains("@") && back.contains(urlSuffix)) {
				System.out.println(crawledEmail + " is valid");
				return true;
			}
		//
		System.out.println(crawledEmail + " is NOT valid");
		return false;
	}
	/**
	 * same as method above but without postposttoken if the post token is the last token in the string[]
	 * @param crawledEmail
	 * @param preToken
	 * @param postToken
	 * @return
	 */
	private boolean isValidEmail(String crawledEmail, String preToken, String postToken) {
		boolean isValid = false;
		if ( (crawledEmail.contains("@") ) ) {
			isValid = true;
		}
		String[] badSignage = {"[at]", "[@]", "(at)", "(@)"};
		for (int i = 0; i < badSignage.length; i++){
			if (crawledEmail.contains(badSignage[i])) {
				isValid = true;
			}
		}
		if (!isValid) {
			return false;
		}
		System.out.println("Checking to see if " + crawledEmail + " is a valid email...");
		//decrypt shitty encryption for @ sign
		for (int i = 0; i < badSignage.length; i++){
			if(crawledEmail.contains(badSignage[i])){
				crawledEmail.replace(badSignage[i], "@");
			}
		}
		//check if its social media or anything with @bullshit
			if (crawledEmail.indexOf("@") == 0 && ! crawledEmail.contains(urlSuffix) && ! postToken.contains(urlSuffix)) {
				System.out.println(crawledEmail + "is NOT valid");
				return false;
			}
		//check if the thing only picked up the @ sign since they did front @ back.com
		if(crawledEmail.length() == 1){
			crawledEmail = preToken + crawledEmail + postToken;
		}
		//checking to if its @foo.com
			if (crawledEmail.indexOf("@") == 0 && crawledEmail.contains(urlSuffix)) {
				crawledEmail = preToken + crawledEmail;
			}
		//checking to see if its foo@
		if(crawledEmail.indexOf("@") == crawledEmail.length()-1){
			crawledEmail = crawledEmail + postToken;
		}
		
		int length = crawledEmail.length();
		String front = crawledEmail.substring(0, crawledEmail.indexOf("@"));
		String back = crawledEmail.substring(crawledEmail.indexOf("@")-1, length);
		
		email = crawledEmail;
		
		// does the back have the suffix?
				if (crawledEmail.contains("@") && back.contains(urlSuffix)) {
					System.out.println(crawledEmail + " is valid");
					return true;
				}
		//
		System.out.println(crawledEmail + " is NOT valid");
		return false;
	}
	
	/**
	 * same as method above but without posttoken if the post token is the last token in the string[]
	 * @param crawledEmail
	 * @param preToken
	 * @return boolean if its a valid email
	 */
	private boolean isValidEmail(String crawledEmail, String preToken) {
		boolean isValid = false;
		if ((crawledEmail.contains("@"))) {
			isValid = true;
		}
		String[] badSignage = {"[at]", "[@]", "(at)", "(@)"};
		for (int i = 0; i < badSignage.length; i++) {
			if (crawledEmail.contains(badSignage[i])) {
				isValid = true;
			}
		}
		if (! isValid) {
			return false;
		}
		System.out.println("Checking to see if " + crawledEmail + " is a valid email...");
		//decrypt shitty encryption for @ sign
		for (int i = 0; i < badSignage.length; i++) {
			if (crawledEmail.contains(badSignage[i])) {
				crawledEmail.replace(badSignage[i], "@");
			}
		}
		//check if its social media or anything with @bullshit
			if (crawledEmail.indexOf("@") == 0 && ! crawledEmail.contains(urlSuffix)) {
				System.out.println(crawledEmail + "is NOT valid");
				return false;
			}
			//checking to if its @foo.com
			if (crawledEmail.indexOf("@") == 0 && crawledEmail.contains(urlSuffix)) {
				crawledEmail = preToken + crawledEmail;
			}
		
		int length = crawledEmail.length();
		String back = crawledEmail.substring(crawledEmail.indexOf("@") - 1, length);
		
		email = crawledEmail;
		
		// does the back have the suffix?
			if (crawledEmail.contains("@") && back.contains(urlSuffix)) {
				System.out.println(crawledEmail + " is valid");
				return true;
			}
		//
		System.out.println(crawledEmail + " is NOT valid");
		return false;
	}
	
	/**
	 * if the email is found in plain text, this cleans it
	 * @param crawledEmail
	 * @return cleaned email
	 */
	private String plainTextEmailCleaner (String crawledEmail) {
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
				if (! (back.endsWith(urlSuffix))) {
					back = back.substring(0, back.lastIndexOf(urlSuffix) + urlSuffix.length());
				}
		return front + "@" + back;
	}
	
	/**
	 * @return href data if it has email inside
	 * @throws Exception
	 */
    /*
    TODO: test to see if it works
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
					if (back.contains(urlSuffix)) {
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
	 * @param crawledEmail
	 * @return cleaned email
	 */
	private String hrefEmailCleaner (String crawledEmail){
		//split foo@bar.com into foo / bar.com
		String front = crawledEmail.substring(0, crawledEmail.indexOf("@"));
		String back = crawledEmail.substring(crawledEmail.indexOf("@")+1, crawledEmail.length());
		//checking for foo@bar.com/subject? afsfasf or whatever wonky shit comes after the actual address
			if (! (back.indexOf(urlSuffix) == back.length() - urlSuffix.length()))
				back = back.substring(0, back.indexOf(urlSuffix) + urlSuffix.length());
		//checking for mailto: or other prefixes
		if (front.contains(":"))
			front = front.substring(front.indexOf(":") + 1);
		return front + "@" + back;
	}
	
	private String parseSuffix() throws Exception{
			URL url = new URL(this.url);
			String[] domainNameParts = url.getHost().split("\\.");
			String tldString = domainNameParts[domainNameParts.length - 1];
			return tldString;
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
	public String[] getPageText() {
        return pageText;
    }

}

