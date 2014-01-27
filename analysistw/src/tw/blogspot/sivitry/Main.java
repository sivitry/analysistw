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
import tw.blogspot.util.*;
import tw.blogspot.feature.*;
public class Main {	
	
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
		InputExcel.downloadValueInOneFile();
		list = InputExcel.loadName(list);
		list = InputExcel.loadValueFromOneFile(list);
		list = Integration.doIntegration(list, 5, false);
		Integration.sortByIntegration(list);
		InputExcel.show(list);
		
		//-- todo
		//	revenue(), 計算營收成長? 
		//	dividend(), 股利+股息
		
	}

}

