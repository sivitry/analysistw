package tw.blogspot.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Utility {
	
	public static double getNumber(String str){
		NumberFormat nf = NumberFormat.getInstance(Locale.TAIWAN);
		Number number = null;
		try {
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
}
