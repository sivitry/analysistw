package tw.blogspot.sivitry;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import tw.blogspot.util.ROCDateTimeFormat;
import tw.blogspot.model.Info;
import tw.blogspot.util.Utility;
public class InputExcel {	
	
	private static double lowBound = 0.0;
	private static String nowROCDate = "";
	private static String nowDate = "";
	
	public static void main(String[] args) throws ParseException {
		
		ROCDateTimeFormat rocdf = new ROCDateTimeFormat("eee/MM/dd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
   	    nowROCDate = rocdf.format(new Date()) ;
   	    nowDate =sdf.format(new Date());
		
		String filename = "data/BWIBBU_d20140108_utf8.csv";
		List<Info> list = new LinkedList<Info>();
		String tmp;
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			boolean valid;
			while ((tmp = br.readLine()) != null) {
				if (tmp.length() < 6) {
					continue;
				}
				String str = tmp.substring(1, 5);
				valid = true;
				for (int i = str.length() - 1; i > 0; i--) {
					if (!Character.isDigit(str.charAt(i))) {
						valid = false;
						break;
					}
				}
				if (valid) {
					int id = Integer.parseInt(str);
					if (id > 0 && id < 9999) {
						Info info = new Info();
						info.setId(id);
						list.add(info);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		downloadCsv(list);
		downloadValueInOneFile();
		list = loadName(list);
		list = loadValueFromOneFile(list);
		list = Integration.doIntegration(list, 5, false);
		Integration.sortByIntegration(list);
		show(list);
		
		//-- todo
		//	revenue(), 計算營收成長? 
		//	dividend(), 股利+股息
		
	}

	private static void show(List<Info> list) {

		System.out.println("count \t id \t name \t value \t integration");
		
		Iterator<Info> it = list.iterator();
		int count = 0;
		while(it.hasNext()){
			Info info = it.next();	
			if(info.getIntegration()>lowBound){
				count++;
				System.out.println(count+"\t"+info.getId() +"\t"+ info.getName() + "\t" +info.getValue()+"\t"+info.getIntegration());
			}
//			System.out.println(count+"\t"+info.getId() +"\t"+ info.getName() + "\t" +info.getValue()+"\t"+info.getIntegration());
		}
		System.out.println("show \t total = "+count);
		System.out.println("show \t "+list.size());
	}

	
	
	private static List<Info> loadName(List<Info> list) {
		Iterator<Info> it = list.iterator();
		while(it.hasNext()){
			// save integration into list
			Info info = it.next();	
			try {
				FileInputStream fis;
				fis = new FileInputStream("data/historyPrice/"+info.getId()+".csv");
				DataInputStream in = new DataInputStream(fis);			
				BufferedReader br = new BufferedReader(new InputStreamReader(in,Charset.forName("Big5")));
				String strLine;	
				if ((strLine = br.readLine()) != null){
					int start = 6;
					int end = strLine.length()-13;
					String name = strLine.substring(start, end);
					info.setName(name);
//					System.out.println(strLine.substring(start, end));	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		return list;
	}
	

	private static List<Info> loadValueFromOneFile(List<Info> list) {		
		try{
			FileInputStream fis;
			fis = new FileInputStream("data/all.csv");
			DataInputStream in = new DataInputStream(fis);			
			BufferedReader br = new BufferedReader(new InputStreamReader(in,Charset.forName("Big5")));
			String strLine;	
			int count = 0;
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				if(strLine.length()>4 && strLine.charAt(4)==','){
//					System.out.println(strLine+"\t"+strLine.length());
					String id = strLine.substring(0,4);
//					System.out.println("id="+id);
					if(Utility.isValue(id)){
						int end = 0;
						if(strLine.contains("X")){
							end = strLine.indexOf('X')-1;
						}else if(strLine.contains("－")){
							end = strLine.indexOf('－')-1;
						}else if(strLine.contains("＋")){
							end = strLine.indexOf('＋')-1;
						}else if(strLine.contains(",,")){
							end = strLine.indexOf(",,");
						}else{
						}

						int start = 0;
						String tmp;
						tmp = strLine.substring(0, end);
						
						String value;
						if(tmp.charAt(tmp.length()-1)=='"'){
							tmp = tmp.substring(0, tmp.length()-1);
							value = tmp.substring(tmp.lastIndexOf('"')+1,tmp.length());
						}else{
							value = tmp.substring(tmp.lastIndexOf(',')+1,tmp.length());
						}
//						System.out.println(value);
						if(Utility.isValue(value)  ||  value.contains(",")){
							double num = Utility.getNumber(value);
							//-- In here, "num" is the today price of stock "id"
							//-- Save to list
							Iterator<Info> it = list.iterator();
							while(it.hasNext()){
								Info info = it.next();
								if(info.getId()==Integer.parseInt(id)){
									info.setValue(num);
								}
							}
							count++;
						}else{
							System.out.println("value is not value, id="+id+" value="+value);	
						}
					}
				}	
			}
			System.out.println("loadValueInOneFile \t"+count);
		}catch(Exception e){
			e.printStackTrace();
		}		
		return list;
	}
	
	private static void downloadValueInOneFile() {
		try{ 
			URL website = new URL("http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/MI_INDEX3_print.php?genpage=genpage/Report201401/A112"+nowDate+"ALLBUT0999_1.php&type=csv"); 
			ReadableByteChannel rbc = Channels.newChannel(website.openStream()); 
			FileOutputStream fos = new FileOutputStream("data/all.csv"); 
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); 
			System.out.println("all.csv got"); 
		}catch(Exception e){		
		}		
	}
	
	public static void downloadCsv(List<Info> list){
		Iterator<Info> it = list.iterator();		
		while(it.hasNext()){
			int id = it.next().getId();
			try{ URL website = new URL("http://www.twse.com.tw/ch/trading/exchange/FMNPTK/FMNPTK2.php?STK_NO="+id+"&myear=2014&mmon=01&type=csv"); 
				ReadableByteChannel rbc = Channels.newChannel(website.openStream()); 
				FileOutputStream fos = new FileOutputStream("data/historyPrice/"+id+".csv"); 
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); 
				System.out.println(id+".csv got"); 
			}catch(Exception e){				
			} 					
		}		
	}

}

