package analysis;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import annotation.SearchData;

public class AnalyseResults {
	public ArrayList<SearchData> pagesResults;
	public JSONObject objJSON ;
	
	public AnalyseResults(){
		pagesResults= new ArrayList<SearchData>();
	}
	
	public AnalyseResults(ArrayList<SearchData> pR, JSONObject obj){
		pagesResults= pR;
		objJSON = obj;
	}
}
