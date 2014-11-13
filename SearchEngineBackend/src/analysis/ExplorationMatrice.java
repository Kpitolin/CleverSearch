package analysis;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import annotation.SearchData;
import au.com.bytecode.opencsv.CSVReader;

public class ExplorationMatrice {
	private final static char SEPARATOR = ';';

	@SuppressWarnings("unchecked")
	public JSONObject exploreSimiliratyFromCSV(String pathnameCSV,
			double seuilJaccardInf, ArrayList<SearchData> searchDatas)
			throws IOException {

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
			double indiceJaccard = Double.parseDouble(oneData[i].replace(",",
					"."));

			String noeud1 = oneData[0]; // premiere colonne du csv contenant
										// egalement que le nom de chaque page

			// remplissage de notre liste de noeud
			JSONObject aNode = new JSONObject();
			for (int j = 0; j < searchDatas.size(); j++) {
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

				if (indiceJaccard >= seuilJaccardInf) {
					JSONObject aLink = new JSONObject();

					int indexNoeud1 = data.indexOf(oneData) - 1;
					int indexNoeud2 = i - 1;

					aLink.put("source", indexNoeud1);
					aLink.put("target", indexNoeud2);
					listLinks.add(aLink);
				}
				i++;
				indiceJaccard = Double
						.parseDouble(oneData[i].replace(",", "."));
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

	public Map<SearchData, List<SearchData>> creerMapSimilarite(
			String pathnameCSV, Double seuilMinimal,
			ArrayList<SearchData> searchDatas) throws IOException {

		Map<SearchData, List<SearchData>> mapSimilarite = new LinkedHashMap<SearchData, List<SearchData>>();

		File file = new File(pathnameCSV);
		FileReader fr = new FileReader(file);
		CSVReader csvReader = new CSVReader(fr, SEPARATOR);

		String[] nextLine = null;
		String[] firstLine = null;
		firstLine = csvReader.readNext();

		// On initialise toutes les cles possibles
		List<SearchData> cleMap = new ArrayList<SearchData>();
		for (int i = 0; i < firstLine.length - 1; i++) {
			if (!firstLine[i].equals("#")) {
				cleMap.add(searchDatas.get(i));
			}
		}

		int j = 0;
		while ((nextLine = csvReader.readNext()) != null) {
			List<String> seuilEtPagesRetenues = new ArrayList<String>();
			ArrayList<SearchData> pagesRetenuesTrie = new ArrayList<SearchData>();

			// Recuperation des seuil + indice en fonction du seuil
			for (int i = 1; i < nextLine.length - 1; i++) {
				if (!(1.0 == Double.parseDouble(nextLine[i].replace(",", ".")))) {
					Double seuil = Double.parseDouble(nextLine[i].replace(",",
							"."));
					if (seuil > seuilMinimal) {
						seuilEtPagesRetenues.add(seuil + ";" + i);
					}
				}
			}
			// Tri le tableau
			Collections.sort(seuilEtPagesRetenues, Collections.reverseOrder());

			// Remplissage du tableau avec les searchDatas à partir de l'indice
			for (int i = 0; i < seuilEtPagesRetenues.size(); i++) {
				pagesRetenuesTrie.add(searchDatas.get(Integer
						.parseInt(seuilEtPagesRetenues.get(i).split(";")[1])));
			}

			// Remplissage de la map
			mapSimilarite.put(cleMap.get(j), pagesRetenuesTrie);
			j++;
		}
		csvReader.close();

		Map<SearchData, List<SearchData>> mapSimilariteBis = new LinkedHashMap<SearchData, List<SearchData>>(
				mapSimilarite);

		// Suppression des doublons
		int item = 0;
		Set<SearchData> cles = mapSimilarite.keySet();
		Iterator<SearchData> it = cles.iterator();
		List<SearchData> removedKey = new ArrayList<SearchData>();
		while (it.hasNext()) {
			SearchData cle = (SearchData) it.next();
			if (!removedKey.contains(cle)) {
				List<SearchData> similaires = (List<SearchData>) mapSimilarite
						.get(cle);
				item++;
				for (int i = 0; i < similaires.size(); i++) {
					item++;
					if (mapSimilarite.containsKey(similaires.get(i))
							&& item <= j) {
						removedKey.add(similaires.get(i));
						mapSimilariteBis.remove(similaires.get(i));
					}
				}
			}
		}

		// Affichage de la map
		Set<SearchData> clesBis = mapSimilariteBis.keySet();
		Iterator<SearchData> itBis = clesBis.iterator();
		while (itBis.hasNext()) {
			SearchData cle = (SearchData) itBis.next();
			List<SearchData> similaires = (List<SearchData>) mapSimilariteBis
					.get(cle);
			System.out.println(cle.title);
			for (int i = 0; i < similaires.size(); i++) {
				System.out.println("	>> " + similaires.get(i).title);
			}
		}
		return mapSimilariteBis;
	}
};
