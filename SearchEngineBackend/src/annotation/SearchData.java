package annotation;
import java.util.ArrayList;


public class SearchData {
	public String url;
	public String title;
	public String description;
	protected ArrayList<String> keyWords;
	
	public SearchData(){
		keyWords = new ArrayList<String>();
	}
}
