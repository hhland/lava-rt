package lava.rt.linq;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public abstract  class View<M> {

	protected DataContext dataContext;
	
	protected Class<M> classM;
	
	protected String table,sql;
	
	protected List<String> joins=new ArrayList<String>()
			,conditions=new ArrayList<String>()
			,orderBys=new ArrayList<String>()
			;
	
	protected View (DataContext dataContext,Class<M> classM) {
		this.dataContext=dataContext;
		this.classM=classM;
	}
	
	
	
	public View<M> from(String tableName) {
		return this;
	}
	
	public View<M> join(String...joins) {
		return this;
	}
	
	public View<M> where(String condition) {
		return this;
	}
	
	public View<M> and(String...conditions) {
		return this;
	}
	
	public View<M> or(String...conditions) {
		return this;
	}
	
	public View<M> orderBy(String...columns) {
		return this;
	}
	
	public View<M> groupBy(String...columns) {
		return this;
	}
	
	public void clear() {
		
	}
	
    
    
    protected void createSql(String method) {
    	String sqlPattern="{0} from {1} #{join} #{where} #{orderby}";
    	this.sql=MessageFormat.format(sqlPattern,method,table);
    }
	
    public List<M> select(String...colunms) throws SQLException{
		return null;
	}
    
	public int count(String column) throws SQLException{
		return 0;
	}
	
	public float sum(String column) throws SQLException{
		return 0;
	}
	
	public float min(String column) throws SQLException{
		return 0;
	}
	
	public float max(String column) throws SQLException{
		return 0;
	}
	
	
	
	
}
