package lava.lang;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public enum String {
	tab("\t"),ln("\n")
	;
	
	private java.lang.String string;
	
	private String(java.lang.String string){
		this.string=string;
	}
	
	@Override
	public java.lang.String toString(){return string;}
	
	
	public static java.lang.String firstCharToUpperCase(java.lang.String str){
		return str.substring(0,1).toUpperCase()+str.substring(1,str.length());
	}
	
	
	public static List<java.lang.String> split(java.lang.String text, java.lang.String... spars) throws Exception {
        java.lang.String tempText = text;
        List<java.lang.String> vals = new ArrayList<java.lang.String>();
        for (int i = 0; i < spars.length; i++) {
        	java.lang.String spar = spars[i];
            if (!tempText.contains(spar)) {
                throw new Exception(MessageFormat.format("\"{0}\" don''t contains \"{1}\"", tempText, spar));
            }
            if (i < spars.length - 1) {
                vals.add(tempText.substring(0, tempText.indexOf(spar)));
                tempText = tempText.substring(tempText.indexOf(spar) + 1);
            } else {
            	java.lang.String[] tempTexts = tempText.split(spar);
                vals.add(tempTexts[0]);
                vals.add(tempTexts[1]);
            }
            
        }
        return vals;
    }
	
	 public static java.lang.String trimEnd(java.lang.String value, java.lang.String... suffixs) {
	       
		 java.lang.String v = value;
	        for (java.lang.String suffix : suffixs) {
	            
	            int index = 0;
	            while (v.endsWith(suffix)) {
	                index = v.lastIndexOf(suffix);
	                v = v.substring(0, index);
	            }

	        }
	        return v;
	    }

	    public static java.lang.String trimStart(java.lang.String value, java.lang.String... prefixs) {
	       
	    	java.lang.String v = value;
	        for (java.lang.String prefix : prefixs) {
	            int index = 0;
	            int len = prefix.length();
	            while (v.startsWith(prefix)) {
	                index = v.indexOf(prefix) + len;
	                v = v.substring(index, v.length());
	            }
	        }
	        return v;
	    }

	    public static java.lang.String trim(java.lang.String value, java.lang.String... fixs) {
	    	java.lang.String v = trimStart(value, fixs);
	        v = trimEnd(v, fixs);
	        return v;
	    }
	
}
