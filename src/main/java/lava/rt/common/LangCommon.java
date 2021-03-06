package lava.rt.common;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LangCommon {

	
	private static final String CHAR_EN="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	private static final Pattern PATTERN_BLANK = Pattern.compile("\\s*|\t|\r|\n");
	
	public static boolean isAnyBlank( String... strings) {
		boolean ret=false;
		for (String string :strings) {
			if(string==null||string.trim().length()==0)
			{
				ret=true;
				break;
			}
		}
	    return ret;
	  }
	
	
	public static String trimEnd(String value, String... suffixs) {
        
        String v = value.trim();
        for (String suffix : suffixs) {
            
            int index = 0;
            while (v.endsWith(suffix)) {
                index = v.lastIndexOf(suffix);
                v = v.substring(0, index);
            }

        }
        return v;
    }

    public static String trimStart(String value, String... prefixs) {
        
        String v = value.trim();
        for (String prefix : prefixs) {
            
            int index = 0;
            int len = prefix.length();
            while (v.startsWith(prefix)) {
                index = v.indexOf(prefix) + len;
                v = v.substring(index, v.length());
            }
        }
        return v;
    }

    public static String trim(String value, String... fixs) {
        String v = trimStart(value, fixs);
        v = trimEnd(v, fixs);
        return v;
    }
    
    public static List<String> split(String text, String... spars) throws Exception {
        String tempText = text;
        List<String> vals = new ArrayList<String>();
        for (int i = 0; i < spars.length; i++) {
        	String spar = spars[i];
            if (!tempText.contains(spar)) {
                throw new Exception(MessageFormat.format("\"{0}\" don''t contains \"{1}\"", tempText, spar));
            }
            if (i < spars.length - 1) {
                vals.add(tempText.substring(0, tempText.indexOf(spar)));
                tempText = tempText.substring(tempText.indexOf(spar) + 1);
            } else {
            	String[] tempTexts = tempText.split(spar);
                vals.add(tempTexts[0]);
                vals.add(tempTexts[1]);
            }
            
        }
        return vals;
    }
    
    
    public static String firstCharToUpperCase(String str){
		return str.substring(0,1).toUpperCase()+str.substring(1,str.length());
	}
    
    public static List<String> subString(String value, String start, String end) {
        List<String> vals = new ArrayList<String>();
        String tempValue = value;
        while (tempValue.contains(start) && tempValue.contains(end)) {
            int startIndex = tempValue.indexOf(start);
            int endIndex = tempValue.indexOf(end);
            vals.add(tempValue.substring(startIndex + 1, endIndex));
            tempValue = tempValue.substring(endIndex + 1);
        }
        return vals;
    }
    
    public static String repeat(String value,String spar,int times) {
    	String[] values=new String[times];
    	for(int i=0;i<values.length;i++) {
    		values[i]=value;
    	}
    	return String.join(spar, values);
    }
    
    public static String join(String delimiter,Object...vals) {
    	String[] strs=new String[vals.length];
		for(int i=0;i<strs.length;i++) {
			strs[i]=vals[i].toString();
		}
		return String.join(delimiter, strs);
    }
    
    
    public static String replaceBlank(String value) {
    	String ret=null;
    	if(value==null) return ret;
        Matcher m = PATTERN_BLANK.matcher(value);
        ret = m.replaceAll("");
        
        return ret;

    }


	public static String createRandomEn(Random random,int dataLength) {
		// TODO Auto-generated method stub
	     StringBuffer ret=new StringBuffer();
	     for(int i=0;i<dataLength;i++){
	       int number=random.nextInt(62);
	       ret.append(CHAR_EN.charAt(number));
	     }
	     return ret.toString();
	}
	
	
	
}
