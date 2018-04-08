package lava.rt.common;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class TextCommon {

	public static boolean isNullOrEmpty( String... strings) {
		for (String string :strings) {
			if(string!=null&&string.trim().length()>0)
				return false;
		}
	    return true;
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
    
    public static List<java.lang.String> split(String text, String... spars) throws Exception {
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
}
