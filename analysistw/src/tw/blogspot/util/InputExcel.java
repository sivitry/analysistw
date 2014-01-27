package tw.blogspot.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.blogspot.model.Info;
public class InputExcel {	
	
	private static double lowBound = 0.0;
	private static String nowROCDate = "";
	private static String nowDate = "";
	
	public static void show(List<Info> list) {

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

	
	
	public static List<Info> loadName(List<Info> list) {
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
					//info.setValue(value);
					//info.setIntegration(integration)
//					System.out.println(strLine.substring(start, end));	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		return list;
	}
	

	public static List<Info> loadValueFromOneFile(List<Info> list) {		
		try{
			FileInputStream fis;
			fis = new FileInputStream("data/all.csv");
			DataInputStream in = new DataInputStream(fis);			
			BufferedReader br = new BufferedReader(new InputStreamReader(in,Charset.forName("Big5")));
			String strLine;	
			int count = 0;
			
			Pattern p = Pattern.compile("\"(\\d*(,?)\\.*\\d*)*\"");
			Pattern lineP = Pattern.compile("(\\d{4}),(.*),(\\d*),(\\d*),(\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(.*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*)");
			
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				if(strLine.length()>4 && strLine.charAt(4)==','){
					
					Matcher m = p.matcher(strLine);
				    StringBuffer s = new StringBuffer();
				    while (m.find()){
				    	m.appendReplacement(s, m.group().replaceAll("[,\"]", ""));
				    }
				    m.appendTail(s);
				    
				    Matcher lm = lineP.matcher(s.toString());
				    
				    if(lm.matches()){
				    	String id = lm.group(1);
				    	
				    	System.out.println(lm.group(10));
				    	String target = lm.group(9);
				    	
				    	try {
				    		double number = Double.parseDouble(target);

				    		Iterator<Info> it = list.iterator();
				    		while(it.hasNext()){
								Info info = it.next();
								if(info.getId()==Integer.parseInt(id)){
									info.setValue(number);
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println("value is not value, id="+id+" value="+target);	
						}
				    	
				    }else{
				    	System.out.println(s.toString());
				    }
				}	
			}
			System.out.println("loadValueInOneFile \t"+count);
		}catch(Exception e){
			e.printStackTrace();
		}		
		return list;
	}
	
	public static void downloadValueInOneFile() {
		
		ROCDateTimeFormat rocdf = new ROCDateTimeFormat("eee/MM/dd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
   	    nowROCDate = rocdf.format(new Date()) ;
   	    nowDate =sdf.format(new Date());
		
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

