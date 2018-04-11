package lava.rt.linq;

import java.sql.SQLException;

import lava.rt.common.SqlCommon;

public abstract class Table<M> extends View<M> {

	protected Table(DataContext dataContext,Class<M> classM) {
		super(dataContext,classM);
	}
	
	public int insert(M...models) throws SQLException {
		return 0;
	}
	
	
	public int update(M...models) throws SQLException{
		return 0;
	}
	
	public int delete(M...models) throws SQLException{
		return 0;
	}
}
