package com.soohyunchoi;
import java.util.ArrayList;
public class SpreadsheetRow {
	private ArrayList<String> columns;
	private ArrayList<String> columnValues;
	public SpreadsheetRow(){
	
	}
	public void addColumn(String value){
		columnValues.add(value);
	}
}
