package tw.blogspot.sivitry;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class SivitryBackup {
	
	
	/** downloadvalue() is to download value with input id in list
	 * 
	 * @param list		a list than contains all ids in Taiwan. 
	 */	
/*	public static void downloadValue(List<Info> list){
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
	}*/	
	
/*	
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
	}*/
	
	
	
}
