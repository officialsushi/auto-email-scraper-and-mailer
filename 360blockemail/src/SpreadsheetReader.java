/**
 * @author: sush soohyun choi
 *
 */

import java.io.File;
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
		System.out.println("Current local time: " + LocalTime.now());
		LocalTime startTime = LocalTime.now();
        this.directory = directory;
		spreadsheetReader(readOption);
    }
	
	/**
	 * read spreadsheets
	 * @throws Exception
	 */
	public void spreadsheetReader(int readOption) throws Exception {
		if(readOption == 0){
			ArrayList<ArrayList> lists = whiteSpaceSeparatedFileToLists();
			genCategoryIndices(lists.get(0));
			userChooseCategories();
			listsToObjects(lists.get(0), lists.get(1), lists.get(2));
		}
		else if (readOption == 1){
			ArrayList<ArrayList> lists = commaSeparatedFileToLists();
			genCategoryIndices(lists.get(0));
			userChooseCategories();
			listsToObjects(lists.get(0), lists.get(1), lists.get(2));
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
				return;
			}
			if (input == 0){
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

    /**
     * gets file with white space separated values (must be scrubbed)
     * turns each column into list
     *
	 * @returns ArrayList[0] = Categories, [1] = Website top level url, [2] = Website submission page url
	 *
     * @throws Exception
     */
    private ArrayList<ArrayList> whiteSpaceSeparatedFileToLists() throws Exception {
        System.out.print("Converting database file to list... ");

        final int numberDatabaseColumns = 3;

        String token;
        File file = new File(directory);
        Scanner inFile = new Scanner(file);
        ArrayList<ArrayList> lists = new ArrayList();
        lists.add(new ArrayList<String>());
        lists.add(new ArrayList<String>());
        lists.add(new ArrayList<String>());
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
	private ArrayList<ArrayList> commaSeparatedFileToLists() throws Exception {
		System.out.print("Converting database file to list... ");
		
		final int numberDatabaseColumns = 3;
		
		String token = "";
		File file = new File(directory);
		Scanner inFile = new Scanner(file);
		ArrayList<ArrayList> lists = new ArrayList();
		lists.add(new ArrayList<String>());
		lists.add(new ArrayList<String>());
		lists.add(new ArrayList<String>());
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
	 * @param category
	 * @param url
	 * @param submission
	 * @throws Exception
	 */
    private void listsToObjects(ArrayList<String> category, ArrayList<String> url, ArrayList<String> submission) throws Exception {
        System.out.print("Converting lists to objects... ");
        for (int n = 0; n < userCategories.size(); n++){
        	System.out.println("User's categories #" + n + 1 + " | Category: " + userCategories.get(n).getCategory() + " | Start/End: " + userCategories.get(n).getStart() + "/" + userCategories.get(n).getEnd() + "\n");
			for (int i = userCategories.get(n).getStart(); i < userCategories.get(n).getEnd(); i++){
				publishers.add(new Publisher(category.get(i), url.get(i), submission.get(i)));
			}
		}
		System.out.println("Converted lists to objects!");
    }
    
    public void evaluatePerformance(LocalTime startTime){
    	int emailsFound = 0;
    	int failedConnections = 0;
    	for (int i = 0; i < publishers.size() ; i++){
    		if (!(publishers.get(i).getEmail() == null))
    			emailsFound++;
			if (!(publishers.get(i).isFailedConnection()))
				failedConnections++;
		}
  
		System.out.println("--------------------------------------------------------------------");
		System.out.println("     \u001b[37mPERFORMANCE ANALYSIS\u001b[0m");
		System.out.println();
		System.out.println("Start time: " + startTime + "\t\t\t  End time: " + LocalTime.now());
		System.out.println("# of publishers processed: " + publishers.size() + "\t\t  # of emails found: " + emailsFound);
		System.out.printf("# failed connections: %3d \t\t      Out of: %4d (%4.2f%%)\n", failedConnections, publishers.size(), ((double)failedConnections/publishers.size())*100);
		System.out.printf("\n%% of total emails found: %4.2f%%\n", (double)emailsFound / publishers.size() * 100);
		System.out.printf("%% of emails from working connections found: %4.2f%%\n", (double)emailsFound / (publishers.size() - failedConnections) * 100);
		System.out.println("--------------------------------------------------------------------");
		
	}
}