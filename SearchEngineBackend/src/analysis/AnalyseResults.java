package analysis;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import annotation.SearchData;

public class AnalyseResults {
	private Map<SearchData, List<SearchData>> pagesResults;
	private JSONObject objJSON;
	
	public Map<SearchData, List<SearchData>> getPagesResults() {
		return pagesResults;
	}

	public JSONObject getObjJSON() {
		return objJSON;
	}

	public AnalyseResults(Map<SearchData, List<SearchData>> pagesResults,
			JSONObject objJSON) {
		this.pagesResults = pagesResults;
		this.objJSON = objJSON;
	}	
}
