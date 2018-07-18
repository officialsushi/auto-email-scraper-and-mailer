package com.soohyunchoi;
import java.util.ArrayList;
public class SpreadsheetRow {
	private ArrayList<String> columnValues = new ArrayList();
	public SpreadsheetRow(){
	}
	public void addColumnVal(String value){
		columnValues.add(value);
	}
	public String getColumn(int index){
		return columnValues.get(index);
	}
	public int getNumColumns(){
		return columnValues.size();
	}
}
