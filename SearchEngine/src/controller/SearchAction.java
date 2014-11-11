package controller;

import java.io.IOException;
import java.util.ArrayList;
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
		
		Map<SearchData,ArrayList<SearchData>> results = null ;
		
//		try {
//			results = s.launchSearch(requete,label);
//		} catch (IOException e) {
//			System.out.println("Erreur au lancement de la m�thode launchSearch");
//		}
		
		//exemple
		//AnalyseResults exmple = results.get(0);
		request.setAttribute("pages", results);
	}

}
