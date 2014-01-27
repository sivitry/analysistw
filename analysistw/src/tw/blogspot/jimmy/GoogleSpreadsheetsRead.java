package tw.blogspot.jimmy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.ServiceException;




public class GoogleSpreadsheetsRead {


	public static void main(String[] args) throws IOException, ServiceException  {

		SpreadsheetService service = new SpreadsheetService("example");
		//service.setUserCredentials(username, password);
		
		URL url;
		try {
			url = new URL("https://docs.google.com/spreadsheet/pub?key=0Av-Q3ZCgDIdGdGEyekt3WHpPQTFwQTVwTldYUlhIM1E");
			
			SpreadsheetQuery query = new SpreadsheetQuery(url);
			SpreadsheetFeed feed = service.query(query,null, "");
			List list =feed.getEntries();
			
			
			
			String re = query.getFields();
			
			System.out.println(list);
				
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		

	}

}
