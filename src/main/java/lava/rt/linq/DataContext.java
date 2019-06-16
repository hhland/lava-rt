package lava.rt.linq;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface  DataContext  {

	

	
	
	
	
	public <M extends Entity> List<M> executeQueryList(Class<M> cls, String sql, Object... params) throws SQLException ;

	public Object[][] executeQueryArray(String sql, Object... params) throws SQLException ;

	public String executeQueryJsonArray(String sql, Object... params) throws SQLException ;

	public int executeUpdate(String sql, Object... param) throws SQLException ;

	public int executeInsertReturnPk(String sql, Object... param) throws SQLException ;

	
	
	public int executeBatch(String sql, Object[]... params) throws SQLException ;

	public int insert(Collection<? extends Entity> entrys) throws SQLException ;

	

	

	public int update(Entity entry) throws SQLException;

	

	public int update(Collection<? extends Entity> entrys) throws SQLException ;

	

	public int delete(Entity entry) throws SQLException ;
	
	public int delete(Collection<? extends Entity> entrys) throws SQLException;

	
	
	

}
