package search;

import java.net.*;
import java.io.*;

public class DuckDuckGoSearch {

	// DuckDuckGo APIs URL
	final private String DUCKDUCKGO_SEARCH_URL = "http://api.duckduckgo.com/?";

	final private String format = "json";
	final private String pretty = "1";
	final private String FINAL_URL = DUCKDUCKGO_SEARCH_URL + "&format="
			+ format + "&pretty=" + pretty + "&q=";

	// Complete request URL
	private URL searchUrl;

	// Object of the query
	@SuppressWarnings("unused")
	private String query;

	// Constructor
	public DuckDuckGoSearch() throws MalformedURLException {

		super();
		this.query = "";

	}

	public void GenerateJsonFile(String filename, String query)
			throws IOException {

		File file = new File(filename);
		FileWriter fw;
		fw = new FileWriter(file);
		String stringUrl = FINAL_URL + query.replaceAll(" ", "+");

		// URL to be sent
		URL tempUrl = new URL(stringUrl);
		this.searchUrl = tempUrl;

		// Connection initialization
		URLConnection urlConnection = this.searchUrl.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlConnection.getInputStream()));

		// Writing in the previously created file
		String inputLine;
		while ((inputLine = in.readLine()) != null) {

			fw.write(inputLine + "\n");
			System.out.println(inputLine);

		}
		in.close();
		fw.close();
	}

	public URL getSearchUrl() {

		return searchUrl;
	}

	public void setSearchUrl(URL searchUrl) {

		this.searchUrl = searchUrl;
	}
}
