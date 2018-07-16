package com.soohyunchoi;

import java.time.LocalTime;

/**
 * July 2: all had ~22% efficiency
 * July 3 pre-selenium: all had ~30% efficiency
 * July 5 pre-selenium: all had ~30% efficiency
 * July 10 selenium: all had ~43% efficiency
 * July 12 selenium: all had ~50% efficiency
 */

public class Main {
    public static void main(String[] args) throws Exception {
		LocalTime startTime = LocalTime.now();
		SpreadsheetReader reader = new SpreadsheetReader("databasecommastest.csv", false);
//		SpreadsheetReader reader = new SpreadsheetReader("jstest.csv", 1);
		printPublishers(reader);
		reader.evaluatePerformance(startTime);
    }
	public static void printPublishers(SpreadsheetReader a){
	 	for (int i = 0; i < a.publishers.size(); i++) {
			System.out.println(a.publishers.get(i));
		}
	}
}
