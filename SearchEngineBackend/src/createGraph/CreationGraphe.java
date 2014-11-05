package createGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.jena.riot.Lang;
import org.apache.log4j.BasicConfigurator;

import org.json.JSONArray;
import org.json.JSONObject;

import service.Service;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;

public class CreationGraphe {
	public final static String macSeparator = "/";
	public final static String windowsSeparator = "\\";
	public final static String separator = macSeparator;

	public void extractText(String filePath, ArrayList<String> arrayOfWords)
			throws FileNotFoundException {
		// Recuperation fichier txt
		Scanner scanner = new Scanner(new File(filePath));
		// On boucle sur chaque champ detecté
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			//System.out.println(line + "-------");
			if(!(line.contains("%") ) && !line.equals("")){
				arrayOfWords.add(line);

			}
		}

		scanner.close();
	}
	public ArrayList<ArrayList<String>> extractFromJSON(JSONObject job, int iter){

		ArrayList<ArrayList<String>> resources =  new ArrayList<ArrayList<String>>();

		try{

		JSONArray res=(JSONArray)job.get(""+iter);
		for (int i = 0; i < res.length(); i++){

			ArrayList<String> keywordAndValues =  new ArrayList<String>();
			JSONObject temp = (JSONObject) res.get(i);
			String keyword= (String) temp.get("name");
			JSONArray array=(JSONArray) temp.get("uri");
			keywordAndValues.add(keyword);
			//System.out.println("Name = " + keyword);

			for(int j = 0; j < array.length(); j++){

				JSONObject value = (JSONObject) array.get(j);
				String uri = (String) value.get("value");
				keywordAndValues.add(uri);
				//System.out.println("value = " + uri);
			}

			resources.add(keywordAndValues);
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
		return resources;
	}

	public Model modelCreation(ArrayList <ArrayList <String>> arrayOfWords, String rootUrl) {
		Model m = ModelFactory.createDefaultModel();
		String dbRootUri = "http://dbpedia.org/resource";
		Resource r = m.createResource(rootUrl);
		Property P = null;
		Resource res = null;
		Property P2 = null;
		Resource res2 = null;

		for (int i = 0; i < arrayOfWords.size(); i++) {
			if(i == 0){
				// is a list
				P = m.createProperty(dbRootUri + "/" + arrayOfWords.get(i).get(0));
				res = m.createResource(dbRootUri + "/" + arrayOfWords.get(i).get(0));
				m.add(r, P, res);
			}
			else
			{
				for(int j=1; j<arrayOfWords.get(i).size();j++ ){
					P2 = m.createProperty("" +arrayOfWords.get(i).get(j));
					res2 = m.createResource(""+arrayOfWords.get(i).get(j));
					m.add(r, P, res);
				}

			}

		}

		return m;
	}

	public void writeInFile(Model m) throws IOException {
		// now write the model in XML form to a file
		RDFWriter writer = m.getWriter();
		// writer.setErrorHandler(myErrorHandler);
		writer.setProperty("showXmlDeclaration", "true");
		writer.setProperty("tab", "8");
		writer.setProperty("relativeURIs", "same-document,relative");
		OutputStream outStream = new FileOutputStream("."+separator+"extendedGraph"+separator+"foo_" + Service.increment
				+ ".xml");
		writer.write(m, outStream, "RDF/XML");
		outStream.close();

	}

//	public void createGraph(String filePath, String rootUrl) {
//		ArrayList<String> arrayOfWords = new ArrayList<String>();
//		try {
//			extractText(filePath, arrayOfWords);
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// Model creation
//		BasicConfigurator.configure(); // necessary
//		Model m = modelCreation(arrayOfWords, rootUrl);
//
//		try {
//			writeInFile(m);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public static void main(String[] args) throws IOException {



	}

}
