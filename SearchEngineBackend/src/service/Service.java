package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;

import search.GoogleSearch;
import analysis.AnalyseResults;
import analysis.ExplorationMatrice;
import annotation.DBpediaSpotlightClient;
import annotation.JsonParser;
import annotation.SearchData;
import compareGraph.compareRDF;
import createGraph.CreationGraphe;

public class Service {
	public final static String macSeparator = "/";
	public final static String windowsSeparator = "\\";
	public final static String separator = macSeparator;
	private final static double SEUILJACCARDFORT = 0.6;
	private final static double SEUILJACCARDMOYEN = 0.4;
	private final static double SEUILJACCARDFAIBLE = 0.2;
	public static int increment = 0;

	public ArrayList<AnalyseResults> launchSearch(String query, String label)
			throws IOException {
		
		GoogleSearch search = new GoogleSearch();
		//String jsonGenere = search.search(query, label);
		String jsonGenere = "searchResults.json";
		DBpediaSpotlightClient annotation = new DBpediaSpotlightClient();
		CreationGraphe creator = new CreationGraphe();

		ArrayList<SearchData> searchDatas = new ArrayList<SearchData>();

		try {
			searchDatas = annotateAndCreateGraph(jsonGenere, annotation,
					creator);
		} catch (Exception e) {
			System.out
					.println("probleme d'annotation ou de creation des graphes rdf");
		}

		compareRDF compare = new compareRDF();
		compare.creerMatriceSimilarite("." + separator + "extendedGraph");

		ExplorationMatrice explorer = new ExplorationMatrice();
		org.json.simple.JSONObject jsonObj1 = explorer.exploreSimiliratyFromCSV("extendedGraph"
				+ separator + "matriceSimilarite.csv", 0, 0.02,searchDatas);
		/*
		org.json.simple.JSONObject jsonObj2 = explorer.exploreSimiliratyFromCSV("extendedGraph"
				+ separator + "matriceSimilarite.csv", 0.02,0.1,searchDatas);

		org.json.simple.JSONObject jsonObj3 = explorer.exploreSimiliratyFromCSV("extendedGraph"
				+ separator + "matriceSimilarite.csv", 0.1,0.2,searchDatas);
	*/

		AnalyseResults results1 = new AnalyseResults(searchDatas, jsonObj1);
		//System.out.println(jsonObj1.toJSONString());
		
		/*AnalyseResults results2 = new AnalyseResults(searchDatas, jsonObj2);
		AnalyseResults results3 = new AnalyseResults(searchDatas, jsonObj3);
		
		ArrayList<AnalyseResults> results = new ArrayList<AnalyseResults>();
		
		results.add(results1);
		results.add(results2);
		results.add(results3);
		
		return results;
		*/
		return null;
	}

	public static ArrayList<SearchData> annotateAndCreateGraph(
			String inputSearchResults, DBpediaSpotlightClient c,
			CreationGraphe creator) throws Exception {
		JsonParser jsonParser = new JsonParser();

		JSONObject jsonResult = annotateAndExtendResults(inputSearchResults,
				jsonParser);
		for (int i = 0; i < 9; i++) {
			ArrayList<ArrayList<String>> myArray = creator.extractFromJSON(
					jsonResult, i);
			BasicConfigurator.configure(); // necessary
			Model m = creator.modelCreation(myArray,"http://"+
					jsonParser.searchResults.searchDatas.get(i).url);
			
			try {
				creator.writeInFile(m);
			} catch (IOException e) {
				e.printStackTrace();
			}
			increment++;
		}
		
	
		
		return jsonParser.searchResults.searchDatas;
	}
	/*
	public String exportJSON() {
		ExplorationMatrice explorer = new ExplorationMatrice();
		JSONObject jsonObj = null;
		try {
			jsonObj = explorer.exploreSimiliratyFromCSV("extendedGraph"
					+ separator + "matriceSimilarite.csv", SEUILJACCARDMOYEN, SEUILJACCARDFORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObj.toJSONString();
	}
	*/
	public static JSONObject annotateAndExtendResults(String filename,
			JsonParser jsonParser) {
		DBpediaSpotlightClient c = new DBpediaSpotlightClient();
		JSONObject jsonObjOut = new JSONObject();

		// Parsing the results of a research given by Google API in JSON format
		// and creating the related object
		jsonParser.parseJsonDBpediaFile(filename);

		// jsonParser.displaySearchResults(jsonParser.searchResults);

		// Creating the annotations from the description + creating the json
		// output file with all the dbpedia ressources links
		for (int i = 0; i < jsonParser.searchResults.searchDatas.size(); i++) {

			JSONArray outputArray = new JSONArray();
			String txt = jsonParser.searchResults.searchDatas.get(i).description;
			String inputFile = c.writeInFile(txt);
			File input = new File(inputFile);
			File output = new File("output.json");

			try {
				c.evaluate(input, output);
				BufferedReader reader = new BufferedReader(new FileReader(
						"output.json"));
				String line;
				while ((line = reader.readLine()) != null && !line.equals("")) {
					String tmp = c.extractFromSparqlQuery(line);
					org.json.JSONObject obj = c.parseJsonResource(line, tmp);
					outputArray.put(obj);
				}

				jsonObjOut.put("" + i, outputArray);
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			c.deleteFile(inputFile);

		}

		// The output json file with all the extended dbpedia relations
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
		return jsonObjOut;
	}

	public static void main(String args[]) throws IOException {
		Service service = new Service();
		service.launchSearch("obama", null);

	}
}
