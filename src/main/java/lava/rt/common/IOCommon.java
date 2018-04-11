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



public class IOCommon {

	
	public static String get(String url, String param) {
	    String result = "";
	    BufferedReader in = null;
	    try {
	      String urlNameString = url + "?" + param;
	      URL realUrl = new URL(urlNameString);
	      // �򿪺�URL֮�������
	      URLConnection connection = realUrl.openConnection();
	      // ����ͨ�õ���������
	      connection.setRequestProperty("accept", "*/*");
	      connection.setRequestProperty("connection", "Keep-Alive");
	      connection.setRequestProperty("user-agent",
	          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	      // ����ʵ�ʵ�����
	      connection.connect();
	      // ��ȡ������Ӧͷ�ֶ�
	      Map<String, List<String>> map = connection.getHeaderFields();
	      // �������е���Ӧͷ�ֶ�
	      for (String key : map.keySet()) {
	        System.out.println(key + "--->" + map.get(key));
	      }
	      // ���� BufferedReader����������ȡURL����Ӧ
	      in = new BufferedReader(new InputStreamReader(
	          connection.getInputStream()));
	      String line;
	      while ((line = in.readLine()) != null) {
	        result += line; 
	      }
	    } catch (Exception e) {
	      System.out.println("����GET��������쳣��" + e);
	      e.printStackTrace();
	    }
	    // ʹ��finally�����ر�������
	    finally {
	      try {
	        if (in != null) {
	          in.close();
	        }
	      } catch (Exception e2) {
	        e2.printStackTrace();
	      }
	    }
	    return result;
	  }
	 
	  /**
	   * ��ָ�� URL ����POST����������
	   * 
	   * @param url
	   *      ��������� URL
	   * @param param
	   *      ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
	   * @return ������Զ����Դ����Ӧ���
	   */
	  public static String post(String url, Map<String, Object> paramMap) {
	    PrintWriter out = null;
	    BufferedReader in = null;
	    String result = "";
	    try {
	      URL realUrl = new URL(url);
	      // �򿪺�URL֮�������
	      URLConnection conn = realUrl.openConnection();
	      // ����ͨ�õ���������
	      conn.setRequestProperty("accept", "*/*");
	      conn.setRequestProperty("connection", "Keep-Alive");
	      conn.setRequestProperty("user-agent",
	          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
	      // ����POST�������������������
	      conn.setDoOutput(true);
	      conn.setDoInput(true);
	     
	      out = new PrintWriter(conn.getOutputStream());
	      // �����������
	      String param="";
	      
	      for(Iterator<String> it=paramMap.keySet().iterator();it.hasNext();) {
	    	  String key=it.next();
	    	  Object valueObj=paramMap.get(key);
	    	  String value=valueObj==null?"":valueObj.toString();
	    	  param+=key+"="+value+"&";
	      }
	      
	      out.print(param);
	      // flush������Ļ���
	      out.flush();
	      // ����BufferedReader����������ȡURL����Ӧ
	      in = new BufferedReader(
	          new InputStreamReader(conn.getInputStream()));
	      String line;
	      while ((line = in.readLine()) != null) {
	        result += line;
	      }
	    } catch (Exception e) {
	      System.out.println("���� POST ��������쳣��"+e);
	      e.printStackTrace();
	    }
	    //ʹ��finally�����ر��������������
	    finally{
	      try{
	        if(out!=null){
	          out.close();
	        }
	        if(in!=null){
	          in.close();
	        }
	      }
	      catch(IOException ex){
	        ex.printStackTrace();
	      }
	    }
	    return result;
	  }  
}
