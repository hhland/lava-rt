package lava.rt.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lava.rt.instance.MethodInstance;



public class IOCommon {

	
	public static String get(String url) throws Exception{
	    String result = "";
	    BufferedReader in = null;
	    
	      String urlNameString = url ;
	      URL realUrl = new URL(urlNameString);
	      
	      URLConnection connection = realUrl.openConnection();
	  
	      connection.setRequestProperty("accept", "*/*");
	      connection.setRequestProperty("connection", "Keep-Alive");
	      connection.setRequestProperty("user-agent",
	          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	      
	      connection.connect();
	      in = new BufferedReader(new InputStreamReader(
	          connection.getInputStream()));
	      String line;
	      while ((line = in.readLine()) != null) {
	        result += line; 
	      }
	      MethodInstance.close.invoke(in);
	      return result;
	  }
	 
	 
	  public static String post(String url, Map<String, Object> paramMap)throws Exception {
	    PrintWriter out = null;
	    BufferedReader in = null;
	    String result = "";
	    
	      URL realUrl = new URL(url);
	    
	      URLConnection conn = realUrl.openConnection();
	     
	      conn.setRequestProperty("accept", "*/*");
	      conn.setRequestProperty("connection", "Keep-Alive");
	      conn.setRequestProperty("user-agent",
	          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	      
	      conn.setDoOutput(true);
	      conn.setDoInput(true);
	     
	      out = new PrintWriter(conn.getOutputStream());
	      
	      String param="";
	      
	      for(Iterator<String> it=paramMap.keySet().iterator();it.hasNext();) {
	    	  String key=it.next();
	    	  Object valueObj=paramMap.get(key);
	    	  String value=valueObj==null?"":valueObj.toString();
	    	  param+=key+"="+value+"&";
	      }
	      
	      out.print(param);
	      out.flush();
	     
	      in = new BufferedReader(
	          new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = in.readLine()) != null) {
	        result += line;
	      }
	      MethodInstance.close.invoke(out,in);
	    return result;
	  }  
}
