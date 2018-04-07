package lava.instance;

import java.text.ParseException;

import java.util.Date;



public enum SimpleDateFormatInstance {

	
	yyyyMMdd_cn("yyyy年MM月dd日"),yyyyMMddHHmmss_en("yyyy-MM-dd HH:mm:ss"),yyyyMMdd_en("yyyy-MM-dd")
	,yyyyMMddTHHmmss_en("yyyy-MM-dd'T'HH:mm:ss")
	;
	
	SimpleDateFormatInstance(String pattern){
		this.simpleDateFormat=new java.text.SimpleDateFormat(pattern);
	}
	
	private java.text.SimpleDateFormat simpleDateFormat;

	
	

	private Date parse(String str) throws ParseException{
		return this.simpleDateFormat.parse(str);
	}
	
	
	

	public String format(Date dt) {
		return this.simpleDateFormat.format(dt);
	}
	
	
	
	public java.text.SimpleDateFormat getSimpleDateFormat() {
		return simpleDateFormat;
	}

	

	public static Date tryParse(String str){
		Date dt=null;
		for(SimpleDateFormatInstance dateTimeFormater:SimpleDateFormatInstance.values()){
			try{
				dt=dateTimeFormater.parse(str);
			}catch(Exception ex){continue;}
			break;
		}
		return dt;
	}
}
