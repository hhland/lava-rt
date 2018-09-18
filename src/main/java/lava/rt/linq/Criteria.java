package lava.rt.linq;

import lava.rt.common.TextCommon;

public abstract class Criteria {

	
	public  String groupBy(Column... columns) {
		return " group by "+TextCommon.join(",", columns);
	}
	
	public  String orderBy(Object... columns) {
		return " order by "+TextCommon.join(",", columns);
	}
	
	public String distinct(Column...columns) {
		return " distinct "+TextCommon.join(",", columns);
	}
	
}
