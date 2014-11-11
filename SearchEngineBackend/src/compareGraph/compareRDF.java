package compareGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

public class compareRDF {
	public final static String macSeparator = "/";
	public final static String windowsSeparator = "\\";
	public final static String separator = macSeparator;
	private static String relativePath = ".." + separator + "ProjetSE"
			+ separator + "testFiles" + separator + "";

	private static String path = "";

	public static String[] listerRepertoire(String repertoireName) {
		File repertoire = new File(repertoireName);
		String[] listeFichiers = repertoire.list();
		ArrayList <String> list = new ArrayList <String>() ;
		int nbElem = 0;
		
		for (String string : listeFichiers){
			if(!string.equals(".DS_Store"))
			{
				list.add(string);
				nbElem++;
			}
		}
		String[] sortie = new String [nbElem];

		sortie = list.toArray(sortie);
		return sortie;
	}

	private static double compterNombreRelations(String inputFileName) {
		// cr�er un mod�le vide
		Model model = ModelFactory.createDefaultModel();
		// utiliser le FileManager pour trouver le fichier d'entr�e
		InputStream in = FileManager.get().open(inputFileName);
		{
			if (in == null) {
				throw new IllegalArgumentException("Fichier: " + inputFileName
						+ " non trouv�");
			}
			// lire le fichier RDF/XML
			model.read(in, "", "RDF/XML");
		}

		StmtIterator iter = model.listStatements();
		int compteur = 0;
		while (iter.hasNext()) {
			compteur++;
			iter.nextStatement(); // obtenir la prochaine d�claration
			/*
			 * Resource subject = stmt.getSubject(); // obtenir le sujet
			 * Property predicate = stmt.getPredicate(); // obtenir le pr�dicat
			 * RDFNode object = stmt.getObject(); // obtenir l'objet
			 * 
			 * System.out.print(subject.toString()); System.out.print(" " +
			 * predicate.toString() + " "); if (object instanceof Resource) {
			 * System.out.print(object.toString()); } else { // l'objet est un
			 * litt�ral System.out.print(" \"" + object.toString() + "\""); }
			 */
		}
		return compteur;
	}

	private static double compterNombreRelationsApresUnion(
			String inputFileName1, String inputFileName2) {
		// Premier graphe RDF
		// cr�er un mod�le vide
		Model model1 = ModelFactory.createDefaultModel();
		// utiliser le FileManager pour trouver le fichier d'entr�e
		InputStream in1 = FileManager.get().open(inputFileName1);
		{
			if (in1 == null) {
				throw new IllegalArgumentException("Fichier: " + inputFileName1
						+ " non trouv�");
			}
			// lire le fichier RDF/XML
			model1.read(in1, "", "RDF/XML");
		}

		// Deuxieme graphe RDF
		// cr�er un mod�le vide
		Model model2 = ModelFactory.createDefaultModel();
		// utiliser le FileManager pour trouver le fichier d'entr�e
		InputStream in2 = FileManager.get().open(inputFileName2);
		{
			if (in2 == null) {
				throw new IllegalArgumentException("Fichier: " + inputFileName2
						+ " non trouv�");
			}
			// lire le fichier RDF/XML
			model2.read(in2, "", "RDF/XML");
		}
		// fusionne les mod�les
		Model model = model2.union(model1);

		// l'�crire dans un fichier tmp
		FileWriter writer = null;
		try {
			writer = new FileWriter(path + "tmp.xml", true);
			model.write(writer, "RDF/XML");
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// On compte le nombre de relation (cardinal)
		File tmp = new File(path + "tmp.xml");
		double cardinal = compterNombreRelations(path + "tmp.xml");
		tmp.delete();

		return cardinal;
	}

	private static double compterNombreRelationsApresIntersection(
			String inputFileName1, String inputFileName2) {
		// Premier graphe RDF
		// cr�er un mod�le vide
		Model model1 = ModelFactory.createDefaultModel();
		// utiliser le FileManager pour trouver le fichier d'entr�e
		InputStream in1 = FileManager.get().open(inputFileName1);
		{
			if (in1 == null) {
				throw new IllegalArgumentException("Fichier: " + inputFileName1
						+ " non trouv�");
			}
			// lire le fichier RDF/XML
			model1.read(in1, "", "RDF/XML");
		}

		// Deuxieme graphe RDF
		// cr�er un mod�le vide
		Model model2 = ModelFactory.createDefaultModel();
		// utiliser le FileManager pour trouver le fichier d'entr�e
		InputStream in2 = FileManager.get().open(inputFileName2);
		{
			if (in2 == null) {
				throw new IllegalArgumentException("Fichier: " + inputFileName2
						+ " non trouv�");
			}
			// lire le fichier RDF/XML
			model2.read(in2, "", "RDF/XML");
		}
		// fusionne les mod�les
		Model model = model2.intersection(model1);

		// l'�crire dans un fichier tmp
		FileWriter writer = null;
		try {
			writer = new FileWriter(path + "tmp.xml", true);
			model.write(writer, "RDF/XML");
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// On compte le nombre de relation (cardinal)
		File tmp = new File(path + "tmp.xml");
		double cardinal = compterNombreRelations(path + "tmp.xml");
		tmp.delete();

		return cardinal;
	}

	private static double calculerIndiceJaccard(String inputFileName1,
			String inputFileName2) {
		double indice = compterNombreRelationsApresIntersection(inputFileName1,
				inputFileName2)
				/ compterNombreRelationsApresUnion(inputFileName1,
						inputFileName2);
		return indice;
	}

	public static String parseDouble(Double nombre) {

		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);

		return decimalFormat.format(nombre);
	}

	private static void modifierFichierXML(String pathName) {
		String[] listeFichiers = listerRepertoire(pathName);
		for (int x = 0; x < listeFichiers.length; x++) {
			if (listeFichiers[x] !=null){


				if (listeFichiers[x].contains("_")){

					File xml = new File(pathName + separator + listeFichiers[x]);
					String newName = listeFichiers[x].replace("_", "");
					FileWriter writer = null;
					if (xml != null) {
						try {
							writer = new FileWriter(new File(pathName + separator
									+ newName));
							writer.write("<?xml version=" + '"' + "1.0" + '"'
									+ "?>\n <rdf:RDF xmlns:rdf=" + '"'
									+ "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
									+ '"' + " xmlns:j.0=" + '"'
									+ "http://dbpedia.org/resource/" + '"'
									+ " xmlns:j.1=" + '"'
									+ "http://dbpedia.org/resource/" + '"' + "> \n");
							writer.write("<rdf:Description rdf:about=" + '"' + "."
									+ '"' + ">\n");
							// creation d'un constructeur de documents a l'aide d'une
							// fabrique
							DocumentBuilder constructeur = DocumentBuilderFactory
									.newInstance().newDocumentBuilder();
							// lecture du contenu d'un fichier XML avec DOM
							Document document = constructeur.parse(xml);
							Element racine = document.getDocumentElement();

							NodeList listeNoeuds = racine.getChildNodes();
							for (int i = 0; i < listeNoeuds.getLength(); i++) {
								if (listeNoeuds.item(i).getNodeName() == "rdf:Description") {
									NodeList listeSousNoeuds = listeNoeuds.item(i)
											.getChildNodes();
									for (int j = 0; j < listeSousNoeuds.getLength(); j++) {
										if (listeSousNoeuds.item(j).getNodeName() != "#text") {
											NamedNodeMap listeAttributs = listeSousNoeuds
													.item(j).getAttributes();
											writer.write("<"
													+ listeSousNoeuds.item(j)
													.getNodeName());
											for (int k = 0; k < listeAttributs
													.getLength(); k++) {
												writer.write(" "
														+ listeAttributs.item(k)
														.getNodeName()
														+ "="
														+ '"'
														+ listeAttributs.item(k)
														.getNodeValue() + '"'
														+ "/>\n");

											}
										}
									}
								}
							}
							writer.write("</rdf:Description>\n");
							writer.write("</rdf:RDF>");
							xml.delete();
						}


						// todo : traiter les erreurs
						catch (ParserConfigurationException pce) {
							System.out
							.println("Erreur de configuration du parseur DOM");
							System.out
							.println("lors de l'appel a fabrique.newDocumentBuilder();");
						} catch (SAXException se) {
							System.out.println("Erreur lors du parsing du document");
							System.out
							.println("lors de l'appel a constructeur.parse(xml)");
						} catch (IOException ioe) {
							System.out.println("Erreur d'entree/sortie");
							System.out
							.println("lors de l'appel a construteur.parse(xml)");
						} finally {
							try {
								writer.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	public void creerMatriceSimilarite(String pathname) {
		// On supprime le CSV s'il existe d�j�
		File csvSupp = new File(pathname + "" + separator
				+ "matriceSimilarite.csv");
		if (csvSupp.exists()) {
			csvSupp.delete();
		}

		modifierFichierXML(pathname);

		String[] listeFichiers = listerRepertoire(pathname);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					pathname + "" + separator + "matriceSimilarite.csv")));
			writer.write("#;");
			for (int i = 0; i < listeFichiers.length; i++) {
				writer.write(listeFichiers[i] + ";");
			}
			writer.write("\n");
			for (int i = 0; i < listeFichiers.length; i++) {
				String[] tabTmp = new String[100];
				int j = 0;
				while (j <= listeFichiers.length) {
					if (j == i + 1) {
						tabTmp[j] = "1;";
					} else if (j == 0) {
						tabTmp[j] = listeFichiers[i] + ";";
					} else {
						tabTmp[j] = parseDouble(calculerIndiceJaccard(pathname
								+ "" + separator + "" + listeFichiers[i],
								pathname + "" + separator + ""
										+ listeFichiers[j - 1]))
										+ ";";

						System.out
						.println(pathname
								+ ""
								+ separator
								+ ""
								+ listeFichiers[i]
										+ "/"
										+ pathname
										+ ""
										+ separator
										+ ""
										+ listeFichiers[j - 1]
												+ " : "
												+ compterNombreRelationsApresIntersection(
														pathname + "" + separator + ""
																+ listeFichiers[i],
																pathname + "" + separator + ""
																		+ listeFichiers[j - 1])
																		+ ", "
																		+ compterNombreRelationsApresUnion(
																				pathname + "" + separator + ""
																						+ listeFichiers[i],
																						pathname + "" + separator + ""
																								+ listeFichiers[j - 1])
																								+ ", "
																								+ Double.toString(calculerIndiceJaccard(
																										pathname + "" + separator + ""
																												+ listeFichiers[i],
																												pathname + "" + separator + ""
																														+ listeFichiers[j - 1])));
					}
					j++;
				}
				tabTmp[j] = "\n";
				for (int k = 0; k <= j; k++) {
					writer.write(tabTmp[k]);
				}
			}
			writer.close();

//									listeFichiers = listerRepertoire(pathname);
//									for (int k = 0; k < listeFichiers.length; k++) {
//										System.out.println(listeFichiers[k]) ;
//										if (!listeFichiers[k].equals("matriceSimilarite.csv")) {
//											System.out.println(listeFichiers[k]  +  "   SUPPR");
//											File supp = new File(pathname + "" + separator + listeFichiers[k]);
//											supp.delete();
//										}
//									}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
