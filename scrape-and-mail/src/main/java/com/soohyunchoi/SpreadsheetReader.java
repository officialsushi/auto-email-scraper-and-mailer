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
		JavaScriptScraper scraper = new JavaScriptScraper();
        for (int n = 0; n < userCategories.size(); n++){
        	System.out.println("User's categories #" + n + 1 + " | Category: " + userCategories.get(n).getCategory() + " | Start/End: " + userCategories.get(n).getStart() + "/" + userCategories.get(n).getEnd() + "\n");
			for (int i = userCategories.get(n).getStart(); i < userCategories.get(n).getEnd(); i++){
				publishers.add(new Publisher((lists.get(0)).get(i), (lists.get(1)).get(i), (lists.get(2)).get(i), scraper));
			}
		}
		scraper.quitDriver();
		scraper.stopService();
	
		System.out.println("Converted lists to objects!");
    }
	
	/**
	 * Prints and creates file of performance stats, for logging purposes
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
			outFile.close();
		} catch(Exception e) { }
	}
	
	/**
	 *  *******======= not tested so idk if it works =====*****
	 * @return string in table format
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