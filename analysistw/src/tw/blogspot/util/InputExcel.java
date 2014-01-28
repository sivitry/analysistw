package tw.blogspot.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import tw.blogspot.util.ROCDateTimeFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import tw.blogspot.model.Info;
import tw.blogspot.util.Utility;
import tw.blogspot.feature.*;

public class InputExcel {

	private static double lowBound = 0.0;
	private static String nowROCDate = "";
	private static String nowDate = "";

	/*
	public static void main(String[] args) throws ParseException {
		downloadRevenue();
	}*/

	/*
	 * download as a zip file, save into path - "data/revenue" unzip file to
	 * path - "data/revenue"
	 */
	public static void downloadRevenue() {
		// http://www.twse.com.tw/ch/statistics/statistics_list.php?tm=05&stm=001
		// http://www.twse.com.tw/ch/inc/download.php?l1=%A4W%A5%AB%A4%BD%A5q%A9u%B3%F8&l2=%A4W%A5%AB%AA%D1%B2%BC%A4%BD%A5q%B0%5D%B0%C8%B8%EA%AE%C6%C2%B2%B3%F8&url=/ch/statistics/download/05/001/1999Q1_C05001.zip
		// http://www.twse.com.tw/ch/inc/download.php?l1=%A4W%A5%AB%A4%BD%A5q%A9u%B3%F8&l2=%A4W%A5%AB%AA%D1%B2%BC%A4%BD%A5q%B0%5D%B0%C8%B8%EA%AE%C6%C2%B2%B3%F8&url=/ch/statistics/download/05/001/1999Q2_C05001.zip
		// ...
		// http://www.twse.com.tw/ch/inc/download.php?l1=%A4W%A5%AB%A4%BD%A5q%A9u%B3%F8&l2=%A4W%A5%AB%AA%D1%B2%BC%A4%BD%A5q%B0%5D%B0%C8%B8%EA%AE%C6%C2%B2%B3%F8&url=/ch/statistics/download/05/001/2013Q3_C05001.zip
		final String OUTPUT_FOLDER = "data/revenue";
		final int year_start = 1999;
		final int year_end = 2013;
		final String[] QARAAY = { "Q1", "Q2", "Q3", "Q4" };

		for (int i = year_start; i <= year_end; i++) {
			for (String q : QARAAY) {
				String target = i + q;
				try {
					URL website = new URL(
							"http://www.twse.com.tw/ch/inc/download.php?l1=%A4W%A5%AB%A4%BD%A5q%A9u%B3%F8&l2=%A4W%A5%AB%AA%D1%B2%BC%A4%BD%A5q%B0%5D%B0%C8%B8%EA%AE%C6%C2%B2%B3%F8&url=/ch/statistics/download/05/001/"
									+ target + "_C05001.zip");
					ReadableByteChannel rbc = Channels.newChannel(website
							.openStream());
					FileOutputStream foszip = new FileOutputStream(target
							+ ".zip");
					foszip.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

					foszip.close();
					File folder = new File(OUTPUT_FOLDER);

					ZipInputStream zis = new ZipInputStream(
							new FileInputStream(target+".zip"));

					ZipEntry ze = zis.getNextEntry();
					byte[] buffer = new byte[1024];
					while (ze != null) {

						String fileName = ze.getName();
						File newFile = new File(OUTPUT_FOLDER + File.separator
								+ target + ".xls");

						System.out.println("file unzip : "
								+ newFile.getAbsoluteFile());

						new File(newFile.getParent()).mkdirs();

						FileOutputStream fos = new FileOutputStream(newFile);

						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}

						fos.close();
						ze = zis.getNextEntry();
					}

					zis.closeEntry();
					zis.close();

					File zipfile = new File(target+".zip");
					zipfile.delete();
					
					System.out.println("Done");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}

	}

	public static void loadAllRevenue() {
		for (int year = 1999; year < 2013; year++) {
			for (int quarter = 1; quarter < 5; quarter++) {
				loadRevenue(year, quarter);
			}
		}
	}

	public static void loadRevenue(int year, int quarter) {
		// FileInputStream file = new FileInputStream(new
		// File("data\revenue\2013Q1.XLS"));
		System.out.println("into loadRevenue...");
		String filename = "data/revenue/2013Q1.XLS";
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);

			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			HSSFSheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				boolean flag = false;
				int cellIndex = 0;

				// For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						// System.out.print(cell.getBooleanCellValue() +
						// "\t\t");
						break;
					case Cell.CELL_TYPE_NUMERIC:
						// System.out.print(cell.getNumericCellValue() +
						// "\t\t");
						break;
					case Cell.CELL_TYPE_STRING:
						// System.out.print(cell.getStringCellValue() + "\t\t");
						// is String is a stock number
						if (Utility.isValue(cell.getStringCellValue())
								&& cell.getStringCellValue().length() == 4) {
							if (Utility.isStock(Integer.parseInt(cell
									.getStringCellValue()))) {
								flag = true;
							}
						}
						break;
					}
				}
				if (flag) {
					System.out.println(row.getCell(0) + "\t" + row.getCell(13));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void show(List<Info> list) {

		System.out.println("count \t id \t name \t value \t integration");

		Iterator<Info> it = list.iterator();
		int count = 0;
		while (it.hasNext()) {
			Info info = it.next();
			if (info.getIntegration() > lowBound) {
				count++;
				System.out.println(count + "\t" + info.getId() + "\t"
						+ info.getName() + "\t" + info.getValue() + "\t"
						+ info.getIntegration());
			}
			// System.out.println(count+"\t"+info.getId() +"\t"+ info.getName()
			// + "\t" +info.getValue()+"\t"+info.getIntegration());
		}
		System.out.println("show \t total = " + count);
		System.out.println("show \t " + list.size());
	}

	public static List<Info> loadName(List<Info> list) {
		Iterator<Info> it = list.iterator();
		while (it.hasNext()) {
			// save integration into list
			Info info = it.next();
			try {
				FileInputStream fis;
				fis = new FileInputStream("data/historyPrice/" + info.getId()
						+ ".csv");
				DataInputStream in = new DataInputStream(fis);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						in, Charset.forName("Big5")));
				String strLine;
				if ((strLine = br.readLine()) != null) {
					int start = 6;
					int end = strLine.length() - 13;
					String name = strLine.substring(start, end);
					info.setName(name);
					// System.out.println(strLine.substring(start, end));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static List<Info> loadValueFromOneFile(List<Info> list) {
		try {
			FileInputStream fis;
			fis = new FileInputStream("data/all.csv");
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					Charset.forName("Big5")));
			String strLine;
			int count = 0;

			Pattern p = Pattern.compile("\"(\\d*(,?)\\.*\\d*)*\"");
			Pattern lineP = Pattern
					.compile("(\\d{4}),(.*),(\\d*),(\\d*),(\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(.*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*),(\\d*\\.*\\d*)");

			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				if (strLine.length() > 4 && strLine.charAt(4) == ',') {

					Matcher m = p.matcher(strLine);
					StringBuffer s = new StringBuffer();
					while (m.find()) {
						m.appendReplacement(s, m.group()
								.replaceAll("[,\"]", ""));
					}
					m.appendTail(s);

					Matcher lm = lineP.matcher(s.toString());

					if (lm.matches()) {
						String id = lm.group(1);

						// System.out.println(lm.group(10));
						String target = lm.group(9);

						try {
							double number = Double.parseDouble(target);

							Iterator<Info> it = list.iterator();
							while (it.hasNext()) {
								Info info = it.next();
								if (info.getId() == Integer.parseInt(id)) {
									info.setValue(number);
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println("value is not value, id=" + id
									+ " value=" + target);
						}

					} else {

						// System.out.println(s.toString());
					}
				}
			}
			System.out.println("loadValueInOneFile \t" + count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void downloadValueInOneFile() {

		ROCDateTimeFormat rocdf = new ROCDateTimeFormat("eee/MM/dd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		nowROCDate = rocdf.format(new Date());
		nowDate = sdf.format(new Date());

		try {
			URL website = new URL(
					"http://www.twse.com.tw/ch/trading/exchange/MI_INDEX/MI_INDEX3_print.php?genpage=genpage/Report201401/A112"
							+ nowDate + "ALLBUT0999_1.php&type=csv");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream("data/all.csv");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			System.out.println("all.csv got");
		} catch (Exception e) {
		}
	}

	public static void downloadCsv(List<Info> list) {
		Iterator<Info> it = list.iterator();
		while (it.hasNext()) {
			int id = it.next().getId();
			try {
				URL website = new URL(
						"http://www.twse.com.tw/ch/trading/exchange/FMNPTK/FMNPTK2.php?STK_NO="
								+ id + "&myear=2014&mmon=01&type=csv");
				ReadableByteChannel rbc = Channels.newChannel(website
						.openStream());
				FileOutputStream fos = new FileOutputStream(
						"data/historyPrice/" + id + ".csv");
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				System.out.println(id + ".csv got");
			} catch (Exception e) {
			}
		}
	}

}
