package tw.blogspot.jimmy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import tw.blogspot.model.Info;
import tw.blogspot.util.InputExcel;

import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.ServiceException;




public class JimmyTestBackup {


	public static void main(String[] args) throws IOException, ServiceException  {

		//SpreadsheetService service = new SpreadsheetService("example");
		//service.setUserCredentials(username, password);		
//		URL url;
//		try {
//			url = new URL("https://docs.google.com/spreadsheet/pub?key=0Av-Q3ZCgDIdGdGEyekt3WHpPQTFwQTVwTldYUlhIM1E");		
//			SpreadsheetQuery query = new SpreadsheetQuery(url);
//			//SpreadsheetFeed feed = query.getFields();
//			String list = query.getFields();
//			String re = query.getFields();
//			System.out.println(list);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		List<Info> list = new LinkedList<Info>();
		Info info = new Info();
		info.setId(2884);
		list.add(info);
		
		List lis = InputExcel.loadName(list);
		
		Iterator it = lis.iterator();
		
		while(it.hasNext()){
			Info i = (Info)it.next();
		    System.out.println(i.getIntegration());
		    System.out.println(i.getName());
		    System.out.println(i.getValue());
		}
		
		

		

		

	}

}
