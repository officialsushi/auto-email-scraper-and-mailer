import java.time.LocalTime;

public class Main {
    public static void main(String[] args) throws Exception {

        LocalTime startTime = LocalTime.now();


        SpreadsheetReader reader = new SpreadsheetReader("databasecommas.csv", 1);
        printPublishers(reader);
        reader.evaluatePerformance(startTime);
    }
    public static void printPublishers(SpreadsheetReader a){
        for (int i = 0; i < a.publishers.size(); i++) {
            System.out.println(a.publishers.get(i));
        }
    }
}
