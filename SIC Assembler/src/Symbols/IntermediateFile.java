package Symbols;

import java.util.ArrayList;
import java.util.Objects;

public class IntermediateFile {
	private ArrayList<ArrayList<String>> file;
	
	public IntermediateFile() {
		file = new ArrayList<>();
	}
	
	public void add(String NewRow, String value ){
		if(Objects.equals(NewRow, "FirstElement")){
			ArrayList<String> newList = new ArrayList<>();
			file.add(newList);
		}//if
		
			file.get(file.size()-1).add(value);
		
	}//add
	
	public String get(int indx1, int indx2){
		return file.get(indx1).get(indx2);
	}//get
	
	public int size(){
		return file.size();
	}
	public int lastRow(){
		return file.size()-1;
	}
}//class
