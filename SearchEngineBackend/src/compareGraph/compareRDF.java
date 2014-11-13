package compareGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

public class compareRDF {
	public final static String macSeparator = "/";
	public final static String windowsSeparator = "\\";
	public final static String separator = macSeparator;
	private static String path = "";

	public static String[] listerRepertoire(String repertoireName) {
		File repertoire = new File(repertoireName);
		String[] listeFichiers = repertoire.list();
		ArrayList<String> list = new ArrayList<String>();
		int nbElem = 0;

		for (String string : listeFichiers) {
			if (!string.equals(".DS_Store")) {
				list.add(string);
				nbElem++;
			}
		}
		String[] sortie = new String[nbElem];

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

		double indice = 0;
		if (compterNombreRelationsApresIntersection(inputFileName1,
				inputFileName2) != 0
				&& compterNombreRelationsApresUnion(inputFileName1,
						inputFileName2) != 0) {
			indice = compterNombreRelationsApresIntersection(inputFileName1,
					inputFileName2)
					/ compterNombreRelationsApresUnion(inputFileName1,
							inputFileName2);
		}
		return indice;
	}

	public static String parseDouble(Double nombre) {

		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(2);

		return decimalFormat.format(nombre);
	}
	
	public void creerMatriceSimilarite(String pathname) {
		// On supprime le CSV s'il existe d�j�
		File csvSupp = new File(pathname + "" + separator
				+ "matriceSimilarite.csv");
		if (csvSupp.exists()) {
			csvSupp.delete();
		}

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
					}
					j++;
				}
				tabTmp[j] = "\n";
				for (int k = 0; k <= j; k++) {
					writer.write(tabTmp[k]);
				}
			}
			writer.close();

			listeFichiers = listerRepertoire(pathname);
			for (int k = 0; k < listeFichiers.length; k++) {
				if (!listeFichiers[k].equals("matriceSimilarite.csv")) {
					File supp = new File(pathname + "" + separator
							+ listeFichiers[k]);
					supp.delete();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
