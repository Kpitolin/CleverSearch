package analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import annotation.SearchData;

public class AnalyseResults {
	public Map<SearchData,ArrayList<SearchData>> pagesResults;
	public JSONObject objJSON ;
	
	public AnalyseResults(){
		pagesResults= new HashMap<SearchData, ArrayList<SearchData>>();
	}
	
	public AnalyseResults(Map<SearchData, ArrayList<SearchData>> pR, JSONObject obj){
		pagesResults= pR;
		objJSON = obj;
	}
}
