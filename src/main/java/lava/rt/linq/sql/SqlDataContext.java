package lava.rt.linq.sql;



import lava.rt.execption.CommandExecuteExecption;
import lava.rt.linq.DataContext;

public interface SqlDataContext extends DataContext {

	
	public String executeQueryJsonList(PagingParam pagingParam) throws CommandExecuteExecption ;

	
	
	public @interface ColumnMeta {

		int dataLength() default -1;

		String comments();
		
		boolean nullable() default true;

	}
	
}
