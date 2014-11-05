package annotation;

import java.io.FileReader;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Class that parses a Json file.
 *
 */

public class JsonParser {

	public SearchResults searchResults = new SearchResults();
	
	/**
	 * Function that parses the JSON search result file and create the searchResults object
	 */
	public void parseJsonDBpediaFile(String filename){
		
		try{
			// read the json file
			FileReader reader = new FileReader(filename);

			// parsing the JSON file
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			
			
			// get the search term from the JSON
			JSONObject queries= (JSONObject) jsonObject.get("queries");
			JSONArray nextPage= (JSONArray) queries.get("nextPage");
			JSONObject searchQuery = (JSONObject) nextPage.get(0);
			
			// get the url and description from the JSON file
			JSONArray items= (JSONArray) jsonObject.get("items");
			
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				
				JSONObject innerObj = (JSONObject) iter.next();
				SearchData tempData = new SearchData();
				tempData.url = (String) innerObj.get("formattedUrl");
				tempData.title = (String) innerObj.get("title");
				tempData.description = (String) innerObj.get("snippet");
				tempData.description = tempData.description.replaceAll("\n", "");
				
				//insert into the list of the result data 
				searchResults.searchDatas.add(tempData);
			}
			
			searchResults.searchQuery = (String) searchQuery.get("searchTerms");
			
			//Display the research results
			displaySearchResults(searchResults);

		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Function that displays the search results from the JSON
	 * @param sr : the searchResults Object
	 */
	public void displaySearchResults(SearchResults sr){
		System.out.println("Search Query : " + sr.searchQuery);
		
		Iterator<SearchData> iterator = sr.searchDatas.iterator();
		while (iterator.hasNext()) {
			
			SearchData s = (SearchData) iterator.next();
			
			System.out.println("\nURL = " + s.url + " \nTITLE = " + s.title + " \nDESCRIPTION = " + s.description);
			System.out.println("\n--------------------------------------------------------------------------------\n");
		}
		
	}
}
