package tw.blogspot.feature;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import tw.blogspot.model.Info;
import tw.blogspot.util.Utility;

public class Integration {
	

	
	public static List<Info> doIntegration(List<Info> list) {
		Iterator<Info> it = list.iterator();
		while(it.hasNext()){
			// save integration into list
			Info info = it.next();			
			try {
				FileInputStream fis = new FileInputStream("data/historyPrice/"+info.getId()+".csv");			
				DataInputStream in = new DataInputStream(fis);			
				String strLine;
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				while ((strLine = br.readLine()) != null){					
					String value = strLine.substring(strLine.lastIndexOf(",")+1);	
					value = Utility.removeQuote(value);		
					if(Utility.isValue(value)){
						double tmp = Utility.getNumber(value)-info.getValue();
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
	
	
	
	
	public static List<Info> doIntegration(List<Info> list, int recentyear, boolean avg) {
		Iterator<Info> it = list.iterator();
		while(it.hasNext()){
			Info info = it.next();			
			try {
				FileInputStream fis = new FileInputStream("data/historyPrice/"+info.getId()+".csv");			
				DataInputStream in = new DataInputStream(fis);			
				String strLine;
				BufferedReader br = new BufferedReader(new InputStreamReader(in));

				ArrayList<Double> al = new ArrayList<Double>();
				
				while ((strLine = br.readLine()) != null){
					
					String value = strLine.substring(strLine.lastIndexOf(",")+1);	
					value = Utility.removeQuote(value);		
					if(Utility.isValue(value)){
						al.add(Utility.getNumber(value));
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
	
	
	public static void sortByIntegration(List<Info> list) {
		Collections.sort(list, new Comparator<Info>(){
			public int compare(Info o1, Info o2){
				Double d1 = o1.getIntegration();
				Double d2 = o2.getIntegration();
				return(d2.compareTo(d1));
			}
		});
	}
	
	
}
