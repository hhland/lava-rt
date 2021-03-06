package lava.rt.wrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class URLWrapper extends BaseWrapper<URL>{

	public URLWrapper(URL url) {
		super(url);
		// TODO Auto-generated constructor stub
		
	}

	
	
	protected static String toQueryString( Map<String, Object> paramMap) {
		
		 String ret="";
		
		for(Entry<String, Object> _param:paramMap.entrySet()) {
	    	  String key=_param.getKey();
	    	  Object valueObj=_param.getValue();
	    	  String value=valueObj==null?"":valueObj.toString();
	    	  ret+=key+"="+value+"&";
	      }
		return ret;
	}
	
	public String get() throws Exception{
	    String result = "";
	    BufferedReader in = null;
	    
	      
	      URL realUrl = self;
	      
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
	      
	      return result;
	  }
	 
	 
	  public String post(Map<String, Object> paramMap)throws Exception {
		
	    PrintWriter out = null;
	    BufferedReader in = null;
	    String result = "";
	    
	      URL realUrl = self;
	    
	      URLConnection conn = realUrl.openConnection();
	     
	      conn.setRequestProperty("accept", "*/*");
	      conn.setRequestProperty("connection", "Keep-Alive");
	      conn.setRequestProperty("user-agent",
	          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	      
	      conn.setDoOutput(true);
	      conn.setDoInput(true);
	     
	      out = new PrintWriter(conn.getOutputStream());
	      
	      String param=toQueryString(paramMap);
	      out.print(param);
	      out.flush();
	     
	      in = new BufferedReader(
	          new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = in.readLine()) != null) {
	        result += line;
	      }
	    
	    return result;
	  }  
	  
	  
	  public Map<String,String> getParamMap(){
		  Map<String,String> ret=new HashMap<>();
		  for(String query :self.getQuery().split("&")) {
			  String[] kv=query.split("=");
			  ret.put(kv[0], kv[1]);
		  }
		  return ret;
	  }
	
}
