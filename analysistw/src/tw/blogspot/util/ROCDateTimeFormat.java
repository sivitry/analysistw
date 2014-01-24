package tw.blogspot.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

public class ROCDateTimeFormat extends SimpleDateFormat
{
    private static final long serialVersionUID = 1;
 
    protected String pattern;
 
    protected Vector patternSegment = new Vector();
 
    public ROCDateTimeFormat(String pattern) {
        super();
        this.pattern = pattern;
        segmentPattern();
    }
 
    private void segmentPattern() {
        int index1;
        int index2;
        int previousIndex = 0;
        while ((index1 = pattern.indexOf("'", previousIndex)) != -1) {
            index2 = pattern.indexOf("'", index1 + 1);
            if (index2 == -1) {
                throw new RuntimeException("Illegal Pattern:" + pattern);
            }
            String prePattern = pattern.substring(previousIndex, index1);
            
            String staticWord = pattern.substring(index1 + 1, index2);
            previousIndex = index2 + 1;
            patternSegment.addElement(prePattern);
            patternSegment.addElement(staticWord);
 
        }
 
        String prePattern = pattern.substring(previousIndex, pattern.length());
        String staticWord = "";
        patternSegment.addElement(prePattern);
        patternSegment.addElement(staticWord);
 
    }
 
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
        int size = patternSegment.size();
 
        for (int i = 0; i < size; i = i + 2) {
            String sp = (String) patternSegment.elementAt(i);
            String word = (String) patternSegment.elementAt(i + 1);
            if (!sp.equals("")) {
                format(date, toAppendTo, sp);
            }
            toAppendTo.append(word);
        }
 
        return toAppendTo;
 
    }
 
    private StringBuffer format(Date date, StringBuffer toAppendTo, String segmentPattern) {
 
        int index = 0;
        int length = 0;
        String rocYearPattern = null;
        if ((index = segmentPattern.indexOf("eee")) != -1) {
            length = 3;
            rocYearPattern = "000";
        }
 
        if (index == -1 && (index = segmentPattern.indexOf("ee")) != -1) {
            length = 2;
            rocYearPattern = "00";
        }
 
        if (index == -1) {
            SimpleDateFormat format = new SimpleDateFormat(segmentPattern);
            toAppendTo.append(format.format(date));
            return toAppendTo;
        } else {
            String pt1 = segmentPattern.substring(0, 0 + index);
 
            String pt2 = segmentPattern.substring(0 + index + length, segmentPattern.length());
            ROCDateTimeFormat format1 = new ROCDateTimeFormat(pt1);
            ROCDateTimeFormat format2 = new ROCDateTimeFormat(pt2);
 
            toAppendTo.append(format1.format(date));
            toAppendTo.append(getROCYear(date, rocYearPattern));
            toAppendTo.append(format2.format(date));
 
            return toAppendTo;
 
        }
    }
 
    private String getROCYear(Date date, String rocYearPattern) {
 
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        int rocyear = cal.get(Calendar.YEAR) - 1911;
 
        DecimalFormat format = new DecimalFormat(rocYearPattern);
        return format.format(rocyear);
    }
 
    public Date parse(String text, ParsePosition pos) {
        String parsedText = text;
        String parsedPattern = "";
        int size = patternSegment.size();
        
        for (int i = 0; i < size; i = i + 2) {
            String sp = (String) patternSegment.elementAt(i);
            String word = (String) patternSegment.elementAt(i + 1);
            parsedPattern += sp ;
            if(parsedText.indexOf(word)==-1){
                throw new RuntimeException("Illegal Text:"+text);
            }
            parsedText = parsedText.replaceFirst(word,"");
        }
 
        boolean rocHit = false;
        
        int rocDifference = 1911;
        
        if(parsedPattern.indexOf("eee")!=-1){
            rocHit = true;
            parsedPattern = parsedPattern.replaceAll("eee","yyy");
        }
        if(parsedPattern.indexOf("ee")!=-1){
            rocHit = true;
            parsedPattern = parsedPattern.replaceAll("ee","yy");
            rocDifference = 11;
        }
        
        
        
        
        SimpleDateFormat format = new SimpleDateFormat(parsedPattern);
        Date parsedDate =  format.parse(parsedText,pos);
        
        if(rocHit){
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(parsedDate);
            cal.add(Calendar.YEAR,rocDifference);     
            parsedDate = cal.getTime();
        }
        
        return parsedDate;
 
    }
 
    public static void main(String[] args) {
        try {
            ROCDateTimeFormat format = new ROCDateTimeFormat("'ROC Year : 'ee 年  MM 月 dd 日 ' 時間是在 ' hh:mm:ss ");
            //ROCDateTimeFormat format = new ROCDateTimeFormat("eeeMMdd");
            String formated = format.format(new Date());
            System.out.println(formated);
            Date date = format.parse(formated);
            System.out.println(date);
            
            
            formated = format.format(date);
            System.out.println(formated);
            date = format.parse(formated);
            System.out.println(date);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}