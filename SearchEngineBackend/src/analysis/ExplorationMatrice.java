package analysis;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import annotation.SearchData;
import au.com.bytecode.opencsv.CSVReader;

public class ExplorationMatrice {
	private final static char SEPARATOR = ';';

	@SuppressWarnings("unchecked")
	public JSONObject exploreSimiliratyFromCSV(String pathnameCSV, double seuilJaccardInf, double seuilJaccardSup, ArrayList<SearchData> searchDatas) throws IOException {

		File file = new File(pathnameCSV);
		FileReader fr = new FileReader(file);
		CSVReader csvReader = new CSVReader(fr, SEPARATOR);

		List<String[]> data = new ArrayList<String[]>();
		String[] nextLine = null;
		String[] firstLine = null;
		// parcours du fichier csv ligne � ligne
		while ((nextLine = csvReader.readNext()) != null) {
			int size = nextLine.length;

			if (size == 0) { // ligne vide
				continue;
			}

			String debut = nextLine[0].trim();

			// recupere en particulier la premiere ligne contenant juste le noms
			// de chaque page
			// et on insere les autres ligne dans la liste data
			if (debut.startsWith("#")) {
				firstLine = nextLine;
			} else {
				if (debut.length() == 0 && size == 1) {
					continue;
				}
				data.add(nextLine);
			}
		}

		JSONObject objJSON = new JSONObject(); // objet JSON final contenant les
												// deux sous objets nodes et
												// links

		JSONArray listNodes = new JSONArray();
		JSONArray listLinks = new JSONArray();

		// traitement des donn�es
		for (String[] oneData : data) {
			int i = 1;
			double indiceJaccard = Double.parseDouble(oneData[i]);

			String noeud1 = oneData[0]; // premiere colonne du csv contenant
										// egalement que le nom de chaque page

			// remplissage de notre liste de noeud
			JSONObject aNode = new JSONObject();
			for(int j =0; j <searchDatas.size() ; j++){
				aNode.put("name", searchDatas.get(j).url);

				if (!listNodes.contains(aNode)) {
					listNodes.add(aNode);
				}
			}
			

			// remplissage de la liste de lien en fonction de l'indice de
			// similarit� entre deux noeud
			while (indiceJaccard != 1) {
				@SuppressWarnings("unused")
				String noeud2 = firstLine[i];

				if (indiceJaccard >= seuilJaccardInf && indiceJaccard < seuilJaccardSup) {
					JSONObject aLink = new JSONObject();

					int indexNoeud1 = data.indexOf(oneData) - 1;
					int indexNoeud2 = i - 1;

					aLink.put("source", indexNoeud1);
					aLink.put("target", indexNoeud2);
					listLinks.add(aLink);
				}
				i++;
				indiceJaccard = Double.parseDouble(oneData[i]);
			}
		}
		// merge des deux liste(deux objets) nodes et links dans l'objet json
		// final objJSON
		objJSON.put("links", listLinks);
		objJSON.put("nodes", listNodes);

		csvReader.close();

		// ecriture d'un fichier (au cas o�)
		File output = new File("similarity_graph.json");
		FileWriter fileWriter = new FileWriter(output);
		fileWriter.write(objJSON.toJSONString());
		fileWriter.flush();
		fileWriter.close();

		return objJSON;
	}
}
