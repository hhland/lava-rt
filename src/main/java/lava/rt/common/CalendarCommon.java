package lava.rt.common;

import java.util.Calendar;
import java.util.Date;

public class CalendarCommon {

	
	public static Calendar getInstance(Date date) {
		Calendar ret=Calendar.getInstance();
		ret.setTime(date);
		return ret;
	}
	
	public static int getYear(Date date) {
		int ret=0;
		Calendar cal=getInstance(date);
		ret=cal.get(Calendar.YEAR);
		return ret;
	}
	
	public static int getMonth(Date date) {
		int ret=0;
		Calendar cal=getInstance(date);
		ret=cal.get(Calendar.MONTH);
		return ret;
	}
	
	public static int getDayOfYear(Date date) {
		int ret=0;
		Calendar cal=getInstance(date);
		ret=cal.get(Calendar.DAY_OF_YEAR);
		return ret;
	}
	
    public static boolean isSameYear(Date...dates){
		boolean ret=true;
		Date date0=dates[0];
		int year0=getYear(date0);
		for(int i=1;i<dates.length;i++) {
			Date datei=dates[i];
			if(year0!=getYear(datei)) {
				ret=false;
				break;
			}
		}
		return ret;
	}
	
    public static boolean isSameYearOfMonth(Date...dates){
		boolean ret=isSameYear(dates);
		if(!ret) {
		   return ret;
		}
		Date date0=dates[0];
		int month0=getMonth(date0);
		for(int i=1;i<dates.length;i++) {
			Date datei=dates[i];
			if(month0!=getMonth(datei)) {
				ret=false;
				break;
			}
		}
		return ret;
	}
    
    
    public static boolean isSameYearOfDay(Date...dates){
		boolean ret=isSameYear(dates);
		if(!ret) {
		   return ret;
		}
		Date date0=dates[0];
		int yearOfDay0=getDayOfYear(date0);
		for(int i=1;i<dates.length;i++) {
			Date datei=dates[i];
			if(yearOfDay0!=getDayOfYear(datei)) {
				ret=false;
				break;
			}
		}
		return ret;
	}
}
