package lava.rt.linq;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Types;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public abstract class DataContext {

	protected Connection connection;
	
	public DataContext(Connection connection) {
		this.connection=connection;
	}
	
	public abstract <T extends Table> T  getTable(Class<T> cls);
	
	public abstract <T extends View> T  getView(Class<T> cls);
	
	
	
	public enum ColumnStruct{
		STRING(String.class,Types.VARCHAR,Types.CHAR,Types.NVARCHAR,Types.LONGNVARCHAR,Types.NCHAR)
		,INT(Integer.class,Types.INTEGER,Types.SMALLINT,Types.BIGINT,Types.TINYINT)
		,FLOAT(Float.class,Types.FLOAT)
		,DATE(Date.class,Types.DATE,Types.TIME)
		,DECIMAL(BigDecimal.class,Types.DECIMAL)
		;
		
		private Class fieldCls;
		private Set<Integer> sqlTypes;
		
		ColumnStruct(Class fieldCls,int...types) {
			this.fieldCls=fieldCls;
			this.sqlTypes=new HashSet<Integer>();
			for(int type:types) {
				sqlTypes.add(type);
			}
			
		}
	}
	
	
}
