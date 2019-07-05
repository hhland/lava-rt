package lava.rt.linq.sql;

import javax.sql.DataSource;

import lava.rt.linq.CommandExecuteExecption;
import lava.rt.linq.DataContext;

public interface SqlDataContext extends DataContext {

	
	public String executeQueryJsonList(PagingParam pagingParam) throws CommandExecuteExecption ;

	
}
