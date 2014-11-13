package createGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class CreationGraphe {
	public final static String macSeparator = "/";
	public final static String windowsSeparator = "\\";
	public final static String separator = macSeparator;
	public final static String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	
	private boolean isGoodUrl(String s) {
        try {
            Pattern patt = Pattern.compile(regex);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
        return false;
    }      
	}
   
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
					if (isGoodUrl( uri) && !uri.contains(" ") && !uri.equals("") && !uri.contains("%") && uri!=null){
						keywordAndValues.add(uri);
					}
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
		Resource res = null;
		Resource res2 = null;
		Property P2 = null;
		Property P = null;
		ArrayList<Resource> linkedResources = new ArrayList<Resource>();
		for (int i = 0; i < arrayOfWords.size(); i++) {


			for(int j=0; j<arrayOfWords.get(i).size(); j++){
				if(!arrayOfWords.get(i).get(j).contains("%")){
					if(j == 0 &&  arrayOfWords.get(i).get(j)!=null){
						// is a list
						P = m.createProperty(dbRootUri + "/" + arrayOfWords.get(i).get(j));

						res = m.createResource(dbRootUri + "/" + arrayOfWords.get(i).get(j));

					}
					else if ( arrayOfWords.get(i).get(j)!=null)
					{

						P2 = m.createProperty(""+arrayOfWords.get(i).get(j));

						res2 = m.createResource(""+arrayOfWords.get(i).get(j));
						res.addProperty(P2, res2);
						linkedResources.add(res2);
					}
				}


			}

			if(!linkedResources.isEmpty()){
				r.addProperty(P, res);

			}


		}

		//m.write(System.out);
		m.write(System.out, "TTL");

		return m;
	}

	public void writeInFile(Model m) throws IOException {
		// now write the model in XML form to a file
		RDFWriter writer = m.getWriter();
		writer.setProperty("showXmlDeclaration", "true");
		writer.setProperty("tab", "8");
		writer.setProperty("relativeURIs", "same-document,relative");
		OutputStream outStream = new FileOutputStream("."+separator+"extendedGraph"+separator+"foo_" + Service.increment
				+ ".xml");
		writer.write(m, outStream, "RDF/XML");
		outStream.close();

	}



	public static void main(String[] args) throws IOException {



	}

}
