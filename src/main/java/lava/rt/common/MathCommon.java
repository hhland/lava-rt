package lava.rt.common;

public class MathCommon {

	
	
	public static <N extends Number> N avg(N... numbers){
		Double v=0.00;
		for(int i=0;i<numbers.length;i++){
	    	 v=v+numbers[i].doubleValue();
	    }
		v=v/numbers.length;
		return (N)v; 
	}
	
	
	 public static <N extends Number> N sd(N... numbers){
		 Double sum = 0.00;
		 Double avg=avg(numbers).doubleValue();
	        for(N number : numbers){
	        	
	            sum += java.lang.Math.sqrt((number.doubleValue()-avg) * (number.doubleValue() -avg));
	        }
	      Double d=sum / (numbers.length - 1);
	      return (N)d;
	 }
	
	 
	
}
