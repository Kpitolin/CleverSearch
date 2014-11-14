package controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import analysis.AnalyseResults;
import annotation.SearchData;
import service.Service;

class SearchAction extends Action {

	public SearchAction() {
	}
	@Override
	public void execute(HttpServletRequest request) {
		Service s = new Service();
		System.out.println("booom execute");
		
		String requete = request.getParameter("q");
		String label = request.getParameter("label");
		
		//ArrayList<AnalyseResults> results =new ArrayList<AnalyseResults>();
		
		AnalyseResults analyseResults =null;
		
		try {
			analyseResults = s.launchSearch(requete,label);
		} catch (IOException e) {
			System.out.println("Erreur au lancement de la m�thode launchSearch");
		}
		
		Map<SearchData, List<SearchData>> results = analyseResults.getPagesResults();
		//exemple
		//AnalyseResults exmple = results.get(0);
		request.setAttribute("pages", results);
	}

}
