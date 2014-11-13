package analysis;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import annotation.SearchData;

public class AnalyseResults {
	public Map<SearchData, List<SearchData>> pagesResults;
	public JSONObject objJSON;
	
	public AnalyseResults(Map<SearchData, List<SearchData>> pagesResults,
			JSONObject objJSON) {
		this.pagesResults = pagesResults;
		this.objJSON = objJSON;
	}	
}
