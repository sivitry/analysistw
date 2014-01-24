package tw.blogspot.sivitry;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

import tw.blogspot.util.ROCDateTimeFormat;
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
//		downloadValue(list);
		downloadValueInOneFile();
		list = loadName(list);
//		list = loadValue(list);		
		list = loadValueInOneFile(list);
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
				fis = new FileInputStream("data/"+info.getId()+".csv");
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
	
	
	
	

	private static List<Info> loadValueInOneFile(List<Info> list) {		
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
					if(isValue(id)){
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
						
//						System.out.println(strLine);
						
						int start = 0;
						String tmp;
						tmp = strLine.substring(0, end);
//						System.out.println(tmp);
						
						/*if(tmp.charAt(tmp.length()-1)==','){
							System.out.println("last is , error!!!");
						}*/
						
						String value;
						if(tmp.charAt(tmp.length()-1)=='"'){
							tmp = tmp.substring(0, tmp.length()-1);
							value = tmp.substring(tmp.lastIndexOf('"')+1,tmp.length());
						}else{
							value = tmp.substring(tmp.lastIndexOf(',')+1,tmp.length());
						}
//						System.out.println(value);
						if(isValue(value)  ||  value.contains(",")){
							double num = getNumber(value);
							//-- In here, "num" is the today price of stock "id"
							//-- Save to list
							Iterator<Info> it = list.iterator();
							while(it.hasNext()){
								Info info = it.next();
								if(info.getId()==Integer.parseInt(id)){
									info.setValue(num);
//									System.out.println(num);
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

	
	
	
	public static List<Info> loadValue(List<Info> list){
		Iterator<Info> it = list.iterator();		
		int count = 0;
		while(it.hasNext()){
			// save value into list
			Info info = it.next();
			try {
				FileInputStream fis = new FileInputStream("data/value_"+info.getId()+".csv");
			
				DataInputStream in = new DataInputStream(fis);
			
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;

				while ((strLine = br.readLine()) != null)   {
					// Print the content on the console
					System.out.println(strLine);
					if(strLine.contains(nowROCDate)){
						String value = (String) strLine.subSequence(nowROCDate.length()+2, strLine.length());						
						value = removeQuote(value);	
						if(isValue(value)){
							NumberFormat nf = NumberFormat.getInstance(Locale.TAIWAN);						
							Number number = nf.parse(value);
//							System.out.println(value+"\t"+number);
							info.setValue(number.doubleValue());
							info.setIntegration(0.0);
							count++;
							break;
						}
					}					  
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(count);
		return list;
	}
	
		
	
	
	public static double getNumber(String str){
		NumberFormat nf = NumberFormat.getInstance(Locale.TAIWAN);
		Number number = null;
		try {
//			System.out.println(str);
			number = nf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return number.doubleValue();		
	}
	
	
	public static String removeQuote(String str){
		if(str.contains("\"")){
			str = str.substring(1, str.length()-1);
		}
		return str;
	}
	
	
	public static boolean isValue(String str){
		boolean valid = true;
		for (int i = str.length() - 1; i > 0; i--) {
			if (!Character.isDigit(str.charAt(i))) {
				 if(str.charAt(i)!='.'){
					valid = false;
				 	break;
				 }
			}
		}
		return valid;
	}
	
	
	
	public static List<Info> integration(List<Info> list, int recentyear, boolean avg) {
		Iterator<Info> it = list.iterator();
		while(it.hasNext()){
			Info info = it.next();			
			try {
				FileInputStream fis = new FileInputStream("data/"+info.getId()+".csv");			
				DataInputStream in = new DataInputStream(fis);			
				String strLine;
				BufferedReader br = new BufferedReader(new InputStreamReader(in));

				ArrayList<Double> al = new ArrayList<Double>();
				
				while ((strLine = br.readLine()) != null){
					
					String value = strLine.substring(strLine.lastIndexOf(",")+1);	
					value = removeQuote(value);		
					if(isValue(value)){
						al.add(getNumber(value));
					}						
				}
				
				
				if(al.size()>recentyear){
					int deletecount = al.size()-recentyear;
					// Shrink list to # of recentyear
					for(int i=0 ; i<deletecount ; i++){
						al.remove(0);
					}
				}
				
				Iterator<Double> alit = al.iterator();
				while(alit.hasNext()){					
					double d = alit.next();
					double tmp = d-info.getValue();
					info.setIntegration(info.getIntegration()+tmp);
				}
				
				// avg
				if(avg){
					info.setIntegration(info.getIntegration()/(double)al.size());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	
	public static List<Info> integration(List<Info> list) {
		Iterator<Info> it = list.iterator();
		while(it.hasNext()){
			// save integration into list
			Info info = it.next();			
			try {
				FileInputStream fis = new FileInputStream("data/"+info.getId()+".csv");			
				DataInputStream in = new DataInputStream(fis);			
				String strLine;
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				while ((strLine = br.readLine()) != null){					
					String value = strLine.substring(strLine.lastIndexOf(",")+1);	
					value = removeQuote(value);		
					if(isValue(value)){
						double tmp = getNumber(value)-info.getValue();
						info.setIntegration(info.getIntegration()+tmp);
//						System.out.println(getNumber(value));					
					}						
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	

	
	/** downloadvalue() is to download value with input id in list
	 * 
	 * @param list		a list than contains all ids in Taiwan. 
	 */	
	public static void downloadValue(List<Info> list){
		Iterator<Info> it = list.iterator();		
		while(it.hasNext()){
			int id = it.next().getId();
			try{ URL website = new URL("http://www.twse.com.tw/ch/trading/exchange/STOCK_DAY_AVG/STOCK_DAY_AVG2.php?STK_NO="+id+"&myear=2014&mmon=01&type=csv");
				ReadableByteChannel rbc = Channels.newChannel(website.openStream()); 
				FileOutputStream fos = new FileOutputStream("data/value_"+id+".csv"); 
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); 
				System.out.println("value_"+id+".csv got"); 
			}catch(Exception e){	
				e.printStackTrace();
			} 					
		}		
	}
	
	
	/** downloadcsv() is to download history value with input id in list
	 * 
	 * @param list		a list than contains all ids in Taiwan. 
	 */		
	public static void downloadCsv(List<Info> list){
		Iterator<Info> it = list.iterator();		
		while(it.hasNext()){
			int id = it.next().getId();
			try{ URL website = new URL("http://www.twse.com.tw/ch/trading/exchange/FMNPTK/FMNPTK2.php?STK_NO="+id+"&myear=2014&mmon=01&type=csv"); 
				ReadableByteChannel rbc = Channels.newChannel(website.openStream()); 
				FileOutputStream fos = new FileOutputStream("data/"+id+".csv"); 
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); 
				System.out.println(id+".csv got"); 
			}catch(Exception e){				
			} 					
		}		
	}
	
	

}

