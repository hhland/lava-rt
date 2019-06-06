package lava.rt.adapter;

import java.util.Calendar;
import java.util.Date;

public class CalendarAdapter extends BaseAdapter<Calendar> {

	

	
	
	public CalendarAdapter(Calendar _this) {
		super(_this);
		// TODO Auto-generated constructor stub
	}
	
     public CalendarAdapter(Date date) {
    	 super( Calendar.getInstance());
		_this.setTime(date);
	}

	protected  int getYear() {
		int ret=0;
		
		ret=_this.get(Calendar.YEAR);
		return ret;
	}
	
	protected  int getMonth() {
		int ret=0;
		
		ret=_this.get(Calendar.MONTH);
		return ret;
	}
	
	protected  int getDayOfYear() {
		int ret=0;
		
		ret=_this.get(Calendar.DAY_OF_YEAR);
		return ret;
	}
	
    public  boolean isSameYear(Date...dates){
		boolean ret=true;
		//Date date0=dates[0];
		int year0=getYear();
		for(int i=0;i<dates.length;i++) {
			Date datei=dates[i];
			if(year0!=new CalendarAdapter(datei).getYear()) {
				ret=false;
				break;
			}
		}
		return ret;
	}
	
    public  boolean isSameYearOfMonth(Date...dates){
		boolean ret=isSameYear(dates);
		if(!ret) {
		   return ret;
		}
		
		int month0=getMonth();
		for(int i=0;i<dates.length;i++) {
			Date datei=dates[i];
			if(month0!=new CalendarAdapter(datei).getMonth()) {
				ret=false;
				break;
			}
		}
		return ret;
	}
    
    
    public  boolean isSameYearOfDay(Date...dates){
		boolean ret=isSameYear(dates);
		if(!ret) {
		   return ret;
		}
		
		int yearOfDay0=getDayOfYear();
		for(int i=0;i<dates.length;i++) {
			Date datei=dates[i];
			if(yearOfDay0!=new CalendarAdapter(datei).getDayOfYear()) {
				ret=false;
				break;
			}
		}
		return ret;
	}
	
}
