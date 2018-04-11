package lava.rt.linq;

import java.sql.SQLException;
import java.text.MessageFormat;

import lava.rt.common.SqlCommon;

public  class Table<M> extends View<M> {

	protected String pkName;
	
	protected Table(DataContext dataContext,Class<M> classM,String pkName) {
		super(dataContext,classM);
		this.pkName=pkName;
		
	}
	
	public M load(String pk) throws SQLException{
		String pattern="select * from {0} where {1}= '{2}'";
		String sql=MessageFormat.format(pattern, this.tableName,this.pkName,pk);
		return dataContext.<M>executeQueryList(sql).get(0);
	}
	
	public  int insert(M...models) throws SQLException {
		return this.dataContext.insert(models);
	}
	
	
	public  int update(M...models) throws SQLException{
		return 0;
	}
	
	public  int delete(M...models) throws SQLException{
		return 0;
	}
}
