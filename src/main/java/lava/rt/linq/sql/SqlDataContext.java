package lava.rt.linq.sql;



import lava.rt.linq.DataContext;
import lava.rt.linq.execption.CommandExecuteExecption;

public interface SqlDataContext extends DataContext {

	
	public String executeQueryJsonList(PagingParam pagingParam) throws CommandExecuteExecption ;

	
}
