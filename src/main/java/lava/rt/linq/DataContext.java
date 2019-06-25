package lava.rt.linq;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface  DataContext  {

	

	public <E extends Entity> E load(Class<E> cls,Object pk) throws SQLException;
	
	
	
	public <M extends Entity> List<M> executeQueryList(Class<M> cls, String sql, Object... params) throws SQLException ;

	public Object[][] executeQueryArray(String sql, Object... params) throws SQLException ;

	
	
	public String executeQueryJsonList(String sql, Object... params) throws SQLException ;
	
	public String executeQueryJsonList(PagingParam pagingParam) throws SQLException ;

	

	public <E extends Entity> int insert(Collection<E> entrys) throws SQLException ;

	public int insert(Entity entry) throws SQLException ;

	public  int insertWithoutPk(Entity entry) throws SQLException;

	public int update(Entity entry) throws SQLException;

	

	public <E extends Entity> int update(Collection<E> entrys) throws SQLException ;

	

	public int delete(Entity entry) throws SQLException ;
	
	public <E extends Entity> int delete(Collection<E> entrys) throws SQLException;

	
	
	


}
