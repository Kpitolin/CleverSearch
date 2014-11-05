package annotation;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dbpedia.spotlight.exceptions.AnnotationException;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileWriter;
import java.io.IOException;

//import org.jdom2.Attribute;
//import org.jdom2.Document;
//import org.jdom2.Element;
//import org.jdom2.output.Format;
//import org.jdom2.output.XMLOutputter;

import scala.util.parsing.json.JSON;

import com.sun.research.ws.wadl.Resource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that annotates text with DBpedia spotlight
 */

public class DBpediaSpotlightClient extends AnnotationClient {

	private final static String API_URL = "http://spotlight.dbpedia.org/";
	private static final double CONFIDENCE = 0.2;
	private static final int SUPPORT = 20;

	public String extractFromSparqlQuery(String query) throws AnnotationException {
		LOG.info("Querying API.");
		String spotlightResponse;

		try {

			//Comment one of the two line, the first uses dbpedia, the second uses factforge
			GetMethod getMethod = new GetMethod("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=select+distinct+*+where+%7B%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2F"+ URLEncoder.encode(query, "utf-8") + "%3E+%3Fp+%3FConcept%7DLIMIT+10%0D%0A%0D%0A&format=json&timeout=30000&debug=on");
			//GetMethod getMethod = new GetMethod("http://factforge.net/sparql.json?query=select+distinct+*+where+%7B+%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2F" + URLEncoder.encode(query, "utf-8") + "%3E+%3Fp+%3FConcept%7D+LIMIT+100%0D%0A&_implicit=false&implicit=true&_equivalent=false&_form=%2Fsparql");

			spotlightResponse = request(getMethod);
		} catch (UnsupportedEncodingException e) {
			throw new AnnotationException("Could not encode text.", e);
		}

		assert spotlightResponse != null;

		JSONObject resultJSON = null;
		JSONArray entities = null;

		/*try {
			resultJSON = new JSONObject(spotlightResponse);
			entities = resultJSON.getJSONArray("Resources");
		} catch (JSONException e) {
			throw new AnnotationException(
					"Received invalid response from DBpedia Spotlight API.");
		}

		LinkedList<DBpediaResource> resources = new LinkedList<DBpediaResource>();
		/*for (int i = 0; i < entities.length(); i++) {
			try {
				JSONObject entity = entities.getJSONObject(i);
				resources.add(new DBpediaResource(entity.getString("@URI"),
						Integer.parseInt(entity.getString("@support"))));

			} catch (JSONException e) {
				LOG.error("JSON exception " + e);
			}

		}

		System.out.println(resources);
		return resources;*/
		
		//System.out.println(spotlightResponse+"\n-------------------------------\n");
		return spotlightResponse;

	
	}
	@Override
	public List<DBpediaResource> extract(Text text) throws AnnotationException {

		LOG.info("Querying API.");
		String spotlightResponse;
		try {
			
			GetMethod getMethod = new GetMethod(API_URL + "rest/annotate/?"
			+ "confidence=" + CONFIDENCE + "&support=" + SUPPORT
			+ "&text=" + URLEncoder.encode(text.text(), "utf-8"));
			
			getMethod.addRequestHeader(new Header("Accept", "application/json"));

			spotlightResponse = request(getMethod);
		} catch (UnsupportedEncodingException e) {
			throw new AnnotationException("Could not encode text.", e);
		}

		assert spotlightResponse != null;

		JSONObject resultJSON = null;
		JSONArray entities = null;

		try {
			resultJSON = new JSONObject(spotlightResponse);
			entities = resultJSON.getJSONArray("Resources");
		} catch (JSONException e) {
			throw new AnnotationException(
					"Received invalid response from DBpedia Spotlight API.");
		}

		LinkedList<DBpediaResource> resources = new LinkedList<DBpediaResource>();
		for (int i = 0; i < entities.length(); i++) {
			try {
				JSONObject entity = entities.getJSONObject(i);
				resources.add(new DBpediaResource(entity.getString("@URI"),
						Integer.parseInt(entity.getString("@support"))));

			} catch (JSONException e) {
				LOG.error("JSON exception " + e);
			}

		}

		//System.out.println(resources);
		return resources;
	}
	
	/**
	 * 
	 * @param name : the name of the resource
	 * @param resource : the json file which contains all the uri related to the name resource.
	 * @return Json object formatted
	 */
	public JSONObject parseJsonResource(String name, String resource){
		
		try {
			JSONObject jsonObjIn = new JSONObject(resource);
			JSONObject results = jsonObjIn.getJSONObject("results");	
			JSONArray bindings = results.getJSONArray("bindings");
			
			JSONObject jsonObjOut = new JSONObject();
			JSONArray outputArray = new JSONArray();
			
			
			for(int i = 0; i < bindings.length(); i++){
				
				JSONObject triple = bindings.getJSONObject(i);
				JSONObject concept = triple.getJSONObject("Concept");
				String uri = (String) concept.get("value");
				
				if(uri.contains("http")){
					JSONObject outputObj = new JSONObject();
					outputObj.put("value", uri);
					outputArray.put(outputObj);
				}

			}
			
			jsonObjOut.put("name", name);
			jsonObjOut.put("uri",outputArray);
		
			
			//System.out.println(jsonObjOut);
			
			return jsonObjOut;
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Function that writes text in a file 
	 * @param text : the text to write in the file
	 * @return : the name of the file
	 */
	public String writeInFile(String text) {

		try {

			File file = new File("filename.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return ("filename.txt");

	}

	public void deleteFile(String path) {

		try {

			File file = new File(path);
			file.delete();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		DBpediaSpotlightClient c = new DBpediaSpotlightClient();
		JSONObject jsonObjOut = new JSONObject();
		
		//Parsing the results of a research given by Google API in JSON format and creating the related object
		JsonParser jsonParser = new JsonParser();
		jsonParser.parseJsonDBpediaFile("searchResults.json");
		
		//jsonParser.displaySearchResults(jsonParser.searchResults);
		
		
		//Creating the annotations from the description + creating the json output file with all the dbpedia ressources links
		for(int i = 0; i < jsonParser.searchResults.searchDatas.size(); i++){
			
			JSONArray outputArray = new JSONArray();
			String txt = jsonParser.searchResults.searchDatas.get(i).description;
			String inputFile = c.writeInFile(txt);
			File input = new File(inputFile);
			File output = new File("output.json");

			c.evaluate(input, output);

			
			try
			  {
			    BufferedReader reader = new BufferedReader(new FileReader("output.json"));
			    String line;
			    while ((line = reader.readLine()) != null && !line.equals(""))
			    {
				     String tmp = c.extractFromSparqlQuery(line);
				     JSONObject obj = c.parseJsonResource(line,tmp);
				     outputArray.put(obj);
			    }
			    
			    jsonObjOut.put(""+i, outputArray);
			    reader.close();
			  }
			  catch (Exception e)
			  {
			    e.printStackTrace();
			  }
			
			c.deleteFile(inputFile);
		}

		//The output json file with all the extended dbpedia relations
		try {
			 
			File file = new File("jsonOut.json");
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(jsonObjOut.toString());
			bw.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
				
		     
		System.out.println(jsonObjOut);
		//System.out.println(obj2);
			
		}
	
	

}