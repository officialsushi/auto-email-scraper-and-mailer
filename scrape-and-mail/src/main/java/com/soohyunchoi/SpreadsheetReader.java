package com.soohyunchoi; /**
 * @author: sush soohyun choi
 *
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.*;

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
    public SpreadsheetReader(String directory, boolean emailsInc) throws Exception {
        this.directory = directory;
		spreadsheetReader(emailsInc);
    }
	
	/**
	 * read spreadsheets
	 * @throws Exception
	 */
	public void spreadsheetReader(boolean emailsIncluded) throws Exception {
		if(emailsIncluded){
			ArrayList<SpreadsheetRow> rows = csvToRows();
			userChooseCategories();
			rowsToPublishersWithScrape(rows);
		}
		else {
			ArrayList<SpreadsheetRow> lists = csvToRows();
			userChooseCategories();
			rowsToPublishersWithScrape(lists);
		}
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
	 * gets file with comma separated values (must be scrubbed)
	 * finds indices per category
	 *
	 * @returns ArrayList[0] = Categories, [1] = Website top level url, [2] = Website submission page url
	 *
	 * @throws Exception
	 */
	private ArrayList<SpreadsheetRow> csvToRows() throws IOException {
		System.out.print("Converting database file to list... ");
		String token;
		File file = new File(directory);
		Scanner inFile = new Scanner(file);
		ArrayList<SpreadsheetRow> rows = new ArrayList();
		String category = "";
		String newCategory;
		int indexStart = 0;
		int indexEnd = 0;
		while (inFile.hasNextLine()){
			token = inFile.nextLine();
			SpreadsheetRow newRow = new SpreadsheetRow();
			rows.add(newRow);
			while(token.contains(",")){
				String nextToken = token.substring(0, token.indexOf(","));
				token = token.substring(token.indexOf(",")+1);
				newRow.addColumnVal(nextToken);
			}
			// create Index objects for each category type
			newRow.addColumnVal(token);
			newCategory = newRow.getColumn(0);
			if (!(category.equals(newCategory))) {
				indices.add(new Index(category, indexStart, indexEnd));
				category = newCategory;
				indexStart = indexEnd + 1;
			}
			indexEnd++;
		}
		indices.add(new Index(category, indexStart, indexEnd));
		indices.remove(0);
		inFile.close();
		System.out.println("Database file converted to lists!");
		
		return rows;
	}
	
	/**
	 * Uses indices ArrayList to only parse the chosen categories
	 * finds email thru webscrape
	 * @param rows list of lists of the different data value types
	 * @throws Exception
	 */
    private void rowsToPublishersWithScrape(ArrayList<SpreadsheetRow> rows) throws Exception {
        System.out.print("Converting lists to objects... ");
        // for the scraper
		JavaScriptScraper.createAndStartService();
		// for logging purposes
		String currentDateAndTime = java.time.LocalDate.now().toString() + " " + LocalTime.now().toString();
		PrintWriter outFile = new PrintWriter(new File("Results for " + currentDateAndTime + ".csv"));
		for (int n = 0; n < userCategories.size(); n++){
			System.out.println("User's categories #" + n + 1 + " | Category: " + userCategories.get(n).getCategory() + " | Start/End: " + userCategories.get(n).getStart() + "/" + userCategories.get(n).getEnd() + "\n");
			for (int i = userCategories.get(n).getStart(); i < userCategories.get(n).getEnd(); i++){
				// category, website domain, submission url
				Publisher publisher = new Publisher(rows.get(i).getColumn(0), rows.get(i).getColumn(1), rows.get(i).getColumn(2));
				publishers.add(publisher);
				// for logging
				outFile.println(publisher.getCategory() + "," + publisher.getSubmission() + "," + publisher.getEmail() + "," + publisher.getUrl());
			}
		}
		outFile.close();
		JavaScriptScraper.stopService();
		System.out.println("Converted lists to objects!");
    }
	
	/**
	 * Prints and creates file of performance stats, only works if program goes thru w/o crashing
	 * @param startTime
	 * @throws IOException
	 */
	public void evaluatePerformance(LocalTime startTime) {
    	LocalTime endTime = LocalTime.now();
    	int emailsFound = 0;
    	int failedConnections = 0;
    	for (int i = 0; i < publishers.size() ; i++){
    		if (!(publishers.get(i).getEmail() == null))
    			emailsFound++;
			if ((publishers.get(i).isFailedConnection()))
				failedConnections++;
		}
		System.out.println("--------------------------------------------------------------------");
		System.out.println("     \u001b[37mPERFORMANCE ANALYSIS\u001b[0m");
		System.out.println();
		System.out.println("Start time: " + startTime + "\t\t\t  End time: " + endTime);
		System.out.println("# of publishers processed: " + publishers.size() + "\t\t  # of emails found: " + emailsFound);
		System.out.printf("# failed connections: %3d \t\t      Out of: %4d (%4.2f%%)\n", failedConnections, publishers.size(), ((double)failedConnections/publishers.size())*100);
		System.out.printf("\n%% of total emails found: %4.2f%%\n", (double)emailsFound / publishers.size() * 100);
		System.out.printf("%% of emails from working connections found: %4.2f%%\n", (double)emailsFound / (publishers.size() - failedConnections) * 100);
		System.out.println("--------------------------------------------------------------------");
		String currentDateAndTime = java.time.LocalDate.now().toString() + " " + LocalTime.now().toString();
		
		//create new file with performance info
		try {
			PrintWriter outFile = new PrintWriter(new File(currentDateAndTime + ".txt"));
			outFile.println("------------------------------------------------------------------------------");
			outFile.println("     PERFORMANCE ANALYSIS" + currentDateAndTime);
			outFile.println();
			outFile.println("Start time: " + startTime + "\t\t\t  End time: " + endTime);
			outFile.println("# of publishers processed: " + publishers.size() + "\t\t  # of emails found: " + emailsFound);
			outFile.printf("# failed connections: %3d \t\t      Out of: %4d (%4.2f%%)\n", failedConnections, publishers.size(), ((double)failedConnections/publishers.size())*100);
			outFile.printf("\n%% of total emails found: %4.2f%%\n", (double)emailsFound / publishers.size() * 100);
			outFile.printf("%% of emails from working connections found: %4.2f%%\n", (double)emailsFound / (publishers.size() - failedConnections) * 100);
			outFile.println("------------------------------------------------------------------------------");
			
			for (Publisher a : publishers){
				outFile.println(a.getCategory() + "," + a.getEmail() + "," + a.getSubmission() + "," + a.getUrl());
			}
			outFile.close();
		} catch(Exception e) { }
	}
	/**
	 * @return long string in table format
	 */
	@Override
	public String toString() {
		String string = "";
		for (int i = 0; i < publishers.size(); i++) {
			string += String.format("%-30s%-50s%20s%n", publishers.get(i).getCategory(), publishers.get(i).getUrl(), publishers.get(i).getSubmission());
		}
		return string;
	}
}