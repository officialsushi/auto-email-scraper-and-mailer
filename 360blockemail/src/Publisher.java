/**
 * publisher object
 *
 * @author sushi
 * @version 0.1
 */

public class Publisher {
    private final String category, url, email, submission;
	
	//stats
	
	public boolean failedConnection;
    public Publisher(String category, String url, String submission) throws Exception{
        this.category = category;
        this.url = url;
        this.submission = submission;
        this.email = scrapeForEmail();
    }
	
    private String scrapeForEmail() throws Exception{
        if (submission != null) {
            WebScraper webScraper = new WebScraper(submission);
            String emailFromScraper = webScraper.getEmail();
            this.failedConnection = webScraper.isFailedConnection();
            return emailFromScraper;
        }
        else {
            throw new IllegalArgumentException("Submission url is NULL");
        }
    }
	
    /*
    private String genSuffix(){
        String tldString = null;
        try {
            URL urlObject = new URL(url);
            String[] domainNameParts = urlObject.getHost().split("\\.");
            tldString = domainNameParts[domainNameParts.length-1];
        }
        catch (MalformedURLException e) {
            System.out.println("!!!!! Could not parse top level domain !!!!!");
        }
        return tldString;
    }
        */
	public boolean isFailedConnection() {
		return failedConnection;
	}
	
    public String getUrl(){
        return url;
    }
	public String getSubmission(){
        return submission;
    }
	public String getCategory() {
        return category;
    }
	public String getEmail() { return email;}


    @Override
    public String toString(){
        return String.format("%23s  |%34s  |%40s  |%2s%-40s", category, url, email,"", submission);
    }
}
