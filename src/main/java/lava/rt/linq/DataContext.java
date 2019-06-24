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

	public int executeUpdate(String sql, Object... param) throws SQLException ;

	public int executeInsertReturnPk(String sql, Object... param) throws SQLException ;

	
	
	public int executeBatch(String sql, Object[]... params) throws SQLException ;

	public int insert(Collection<? extends Entity> entrys) throws SQLException ;

	

	

	public int update(Entity entry) throws SQLException;

	

	public int update(Collection<? extends Entity> entrys) throws SQLException ;

	

	public int delete(Entity entry) throws SQLException ;
	
	public int delete(Collection<? extends Entity> entrys) throws SQLException;

	
	static Date now() {
		// TODO Auto-generated method stub
		return Calendar.getInstance().getTime();
	}
	
	
public  class  Cacheable<E extends Entity>{
		
		final E entity;
		
		final Date timeoutAt;
		
	    boolean enable=true;

		public Cacheable(E entity,long timeoutMillsec) {
			super();
			this.entity = entity;
			timeoutAt=new Date(System.currentTimeMillis()+timeoutMillsec);
		}
		
		public final E getEntity() {
			return entity;
		}
		
		public final boolean isTimeout() {
			boolean ret=System.currentTimeMillis()>timeoutAt.getTime();
			return ret;
		}

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}
		
		
		
	}

}
