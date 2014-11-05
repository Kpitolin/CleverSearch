package annotation;

import java.util.ArrayList;

public class SearchResults {

	protected String searchQuery;
	public ArrayList<SearchData> searchDatas;
	
	public SearchResults(){
	
		searchQuery = "";
		searchDatas = new ArrayList<SearchData>();
	}
	

}
