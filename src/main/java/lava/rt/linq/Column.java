package lava.rt.linq;

public class Column {

	private String column;
	
	public Column(String column) {
		this.column=column;
	}
	
	public  String eq() {
		return column+" = ?";
	}
	
	public  String eq(Object val) {
		if(val instanceof Number) {
			return column+" = "+val+"";
		}
		return column+" = '"+val.toString()+"'";
	}
	
	
	
	public  <T> String in(T... vals) {
		 String[] strs=new String[vals.length];
		 
	     for(int i=0;i<strs.length;i++) {
				 strs[i]=vals[i].toString();
	}
			 
		 
		 if(vals[0] instanceof String) {
		 for(int i=0;i<strs.length;i++) {
			 strs[i]="'"+strs[i]+"'";
		   }
		 }
		 return column+" in ("+String.join(",", strs)+")";
	}
	
	
	
	public  String lt() {
		return column+" < ?";
	}
	
	public  String lt(Object val) {
		if(val instanceof Number) {
			return column+" < "+val+"";
		}
		return column+" < '"+val.toString()+"'";
	}
	
	public  String gt() {
		return column+" > ?";
	}
	
	public  String gt(Object val) {
		if(val instanceof Number) {
			return column+" > "+val+"";
		}
		return column+" > '"+val.toString()+"'";
	}
	
	
	public  String between() {
		return column+" between ? and ?";
	}
	
	public  <T> String  between(T from,T to) {
		if(from instanceof Number) {
			return column+" between "+from+" and "+to;
		}
		return this+" between '"+from+"' and '"+to+"'";
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.column;
	}
	
	
}
