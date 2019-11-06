package lava.rt.linq.sql;







import java.io.Closeable;
import java.lang.reflect.Field;
import java.sql.*;

import java.util.*;
import java.util.Date;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


import javax.sql.DataSource;

import lava.rt.base.PoolList;
import lava.rt.cache.CacheItem;
import lava.rt.common.SqlCommon;
import lava.rt.linq.Checkpoint;
import lava.rt.linq.Entity;
import lava.rt.linq.execption.CommandExecuteExecption;
import lava.rt.linq.execption.CommandExecuteExecption.CmdType;
import lava.rt.linq.execption.DuplicateKeyException;
import lava.rt.logging.LogFactory;

public abstract class DataSourceContext  implements SqlDataContext,Closeable {

	

	private final Map<Class, Table> tableMap = new HashMap<>();

	private final Map<Class,View> viewMap = new HashMap<>();
	
	protected  final Map<String,String> columnMap = new HashMap<>();

	private final ThreadLocal<PoolList<Connection>> localConnection = new ThreadLocal<>();

	protected  <E extends Entity> E entityNew(Class<E> entryClass) throws Exception{
          E ret=Entity.newEntity(entryClass);
          return ret;
	}
	
	
	
	
	

	protected abstract  DataSource[] getDataSources() ;

	
	

	protected <M extends Entity> Table<M> tableCreate(Class<M> cls, String tableName, String pkName) {
		Table<M> table=null;
		try {
		table= new Table<M>(this, cls, tableName, pkName);
		tableMap.put(cls, table);
		viewMap.put(cls, table);
		}catch(Exception ex) {
			logError(cls,tableName);
		}
		return table;
	}

	protected <M extends Entity> View<M> viewCreate(Class<M> cls, String tableName) {
		View<M> view = new View<M>(this, cls, tableName);
		viewMap.put(cls, view);
		return view;
	};

	public <M extends Entity> Table<M> tableGet(Class<M> mcls) {
		Table<M> ret = tableMap.get(mcls);
		return ret;
	}

	public <M extends Entity> View<M> viewGet(Class<M> mcls) {
		View<M> ret = viewMap.get(mcls);
		return ret;
	};
	
	protected <E extends Entity> CacheItem<E> cacheGet(Class<E> cls, Object pk){
		return null;
	}
	
	protected <E extends Entity> void cachePut(E ret, Object pk) {}

	
	public <E extends Entity> E entityGet(Class<E> cls, Object pk) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		CacheItem<E> cache=cacheGet(cls, pk);
		E ret=null;
		if(cache==null||cache.isTimeout()||!cache.isEnable()) {
			Table<E> table=tableGet(cls);
			
			ret=table.load(pk);
			
			cachePut(ret,pk);
		}else {
			ret=cache.get();
		}
		
		return ret;
	}





	






	public <M extends Entity> List<M> entityList(Class<M> cls, String sql, Object... params) throws CommandExecuteExecption {
		sql=View.formatEl(sql, viewMap);
		Connection connection = getConnection();
		
		List<M> list = new ArrayList<M>();
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
			}

			try (ResultSet resultSet = preparedStatement.executeQuery();) {

				ResultSetMetaData metaData = resultSet.getMetaData();
				Map<String, Integer> meteDataMap = new HashMap<String, Integer>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String key = metaData.getColumnName(i).toUpperCase();
					meteDataMap.put(key, i);
				}

				View<M> view = viewGet(cls);
				
				
			
				while (resultSet.next()) {
					M m= entityNew(cls);
					
					int c = 0;
					for (Iterator<String> it = view.entryFieldMap.keySet().iterator(); it.hasNext();) {
						String columnName = it.next().toUpperCase();
						Integer columnIndex = meteDataMap.get(columnName);
						if (columnIndex==null)
							continue;
						
						Field field = view.entryFieldMap.get(columnName);
						try {
							//m.val(columnName,resultSet.getObject(columnIndex));
							field.set(m, resultSet.getObject(columnIndex));
						} catch (Exception e) {
							continue;
						}
						c++;
					}

					list.add(m);
				}
			}
		}catch(Exception ex) {
			SQLException seq=new SQLException(ex);
			
			logExecptioin(seq);
			logError("sql:"+sql+"\nparams:");
			logError(params);
			throw CommandExecuteExecption.forSql(seq, sql, params);
		}

		return list;
	}

	public Object[][] executeQueryArray(String sql, Object... params) throws CommandExecuteExecption {
		sql=formatEl(sql);
		Connection connection= getConnection();
		
		Object[][] re =null;
		try {
		re=SqlCommon.executeQueryArray(connection, sql, params);
		}
		catch(SQLException seq) {
			
			logExecptioin(seq);
			logError("sql:"+sql+"\nparams:");
			logError(params);
			throw CommandExecuteExecption.forSql(seq, sql, params);
		}
		return re;
	}
	
	protected void logSql(String method,String sql,Object param) {
		
	}
	
	protected void logError(Object... vals) {
		LogFactory.SYSTEM.getLog(this.getClass()).error(vals);
	}


	protected void logExecptioin(Exception exception) {
		LogFactory.SYSTEM.getLog(this.getClass()).error(exception);
	}

	

	public String executeQueryJsonList(String sql, Object... params) throws CommandExecuteExecption {
		sql=formatEl(sql);
		StringBuffer ret = new StringBuffer("[");
		int size=0;
		String[] columns=null;
		try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);) {
			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
			}
			try (ResultSet resultSet = preparedStatement.executeQuery();) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				int cc = metaData.getColumnCount();
				
				columns=new String[cc];
				for (int i = 0; i < cc; i++) {
					
					columns[i]=metaData.getColumnName(i + 1);
				}

				while (resultSet.next()) {
					
					
					
					ret.append("{");

					for (int i = 0; i < cc; i++) {
						Object colObject = resultSet.getObject(i + 1);
						if (colObject == null) {
							continue;
						}
						String colValue = colObject.toString();

						ret.append("\"").append(columns[i]).append("\"").append(":");
						if (colObject instanceof String 
								
								) {
							ret.append("\"").append(colValue).append("\"");

						}else if(colObject instanceof java.sql.Date
								|| colObject instanceof Date) {
							ret
							//.append("\"")
							.append(((Date)colObject).getTime())
							//.append("\"")
							;
						} else {
							ret.append(colValue);
						}
						if(i<cc-1) {
							ret.append(",");
						}

					}

					ret.append("},");
					size++;
					// list.add(rowMap);
				}
			}
		}catch(SQLException seq) {
			
			logExecptioin(seq);
			logError("sql:"+sql+"\nparams:");
			logError(params);
			throw CommandExecuteExecption.forSql(seq, sql, params);
		}
		if(size>0) {
			ret.deleteCharAt(ret.length()-1);
		}
		ret
		//.append("],total:").append(total)
		.append("],size:").append(size)
		
		;
		return ret.toString();
	}
	
	
	
	

	




	@Override
	public String executeQueryJsonList(PagingParam pagingParam)
			throws CommandExecuteExecption {
		pagingParam.sql=formatEl(pagingParam.sql);
		pagingParam.pagingSql=formatEl(pagingParam.pagingSql);		
       StringBuffer ret=new StringBuffer(executeQueryJsonList(pagingParam.pagingSql, pagingParam.param));
		
		int size=Integer.parseInt(
				  ret.substring(ret.lastIndexOf("size:")+5)
				  ),total=pagingParam.start+size;
		if(size==pagingParam.limit) {
			String csql=pagingParam.countSql();
			total=(int)executeQueryArray(csql, pagingParam.param)[0][0];
		}
		ret
		.append(",total:")
		.append(total)
		;
		return ret.toString();
	}









	public int executeUpdate(String sql0, Object... param) throws CommandExecuteExecption {
		int re = 0;
		final String sql=View.formatEl(sql0, viewMap);
		PoolList<Connection> connections = getConnections();
		if (connections == null) {
			//printErr("error:" + sql);
		} else if (connections.size() == 1) {
			try {
			re += SqlCommon.executeUpdate(connections.get(0), sql, param);
			}catch(SQLException seq) {
				
				logExecptioin(seq);
				logError("sql:"+sql+"\nparams:");
				logError(param);
				throw CommandExecuteExecption.forSql(seq, sql, param);
				//throw seq;
			}
		} else if (connections.size() > 1) {
			AtomicInteger are = new AtomicInteger(0);
			AtomicReference<CommandExecuteExecption> sex = new AtomicReference<>();
			connections.parallelStream().forEach(conn -> {

				try {
					are.getAndAdd(SqlCommon.executeUpdate(conn, sql, param));
				} catch (SQLException seq) {
					
					logExecptioin(seq);
					logError("sql:"+sql+"\nparams:");
					logError(param);
					
					sex.set(CommandExecuteExecption.forSql(seq, sql, param));
				}
			});
			if (sex.get() != null)
				throw sex.get();
			re = are.get();
		}

		return re;
	}

	public int executeInsertReturnPk(String sql, Object... param) throws CommandExecuteExecption {
		int pk = 0;
		sql=formatEl(sql);
		Connection connection = getConnection();

			try (PreparedStatement preparedStatement = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);) {

				for (int i = 0; i < param.length; i++) {
					preparedStatement.setObject(i + 1, param[i]);
				}
				preparedStatement.executeUpdate();
				try (ResultSet resultSet = preparedStatement.getGeneratedKeys();) {
					if (resultSet.next()) {
						pk = resultSet.getInt(1);
					}
				}
			}catch(SQLException seq) {
				
				logExecptioin(seq);
				logError("sql:"+sql+"\nparams:");
				logError(param);
				throw CommandExecuteExecption.forSql(seq, sql, param);
			}
		
		return pk;
	}

	
	
	public int executeBatch(String sql0, Object[]... params) throws CommandExecuteExecption {
		int re = 0;
		final String  sql=formatEl(sql0);
		PoolList<Connection> connections = getConnections();
		if (connections.size() == 1) {
			try {
			re += SqlCommon.executeBatch(connections.get(0), sql, params);
			} catch(SQLException seq) {
				
				logExecptioin(seq);
				logError("sql:"+sql+"\nparams:");
				logError(params);
				throw CommandExecuteExecption.forSql(seq, sql, params);
			}
		} else if (connections.size() > 1) {
			AtomicInteger are = new AtomicInteger(0);
			AtomicReference<CommandExecuteExecption> sex = new AtomicReference<>();
			connections.parallelStream().forEach(conn -> {

				try {
					are.getAndAdd(SqlCommon.executeBatch(conn, sql, params));
				} catch(SQLException seq) {
					
					logExecptioin(seq);
					logError("sql:"+sql+"\nparams:");
					logError(params);
					sex.set(CommandExecuteExecption.forSql(seq, sql, params));
				}
			});
			if (sex.get() != null)
				throw sex.get();
			re = are.get();
		}

		return re;
	}
	
	
	public  int entityPut(Object pk,Entity entry) throws CommandExecuteExecption,DuplicateKeyException {
		int re = 0;
		Class<? extends Entity> cls = entry.getClass();
		Table table = this.tableGet(cls);
		try {
			table.pkField.set(entry, pk);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			throw new CommandExecuteExecption(e,CmdType.reflect,"set",table.pkField,entry,pk);
		}
		re=table.insert(entry);
		return re;
	}

	

	
	@SuppressWarnings("unchecked")
	public int entityAdd(Entity entry) throws CommandExecuteExecption {
		int re = 0;
		Class<? extends Entity> cls = entry.getClass();
		Table table = this.tableGet(cls);
		re += table.insertWithoutPk(entry);
		
		return re;
	}

	public <E extends Entity> int entityAddAll(Collection<E> entrys) throws CommandExecuteExecption {
		int re = 0;
		Class cls = entrys.stream().findFirst().getClass();
		Table table = this.tableGet(cls);
		
		
		re += table.insert(entrys);
		

		
		
		//entry._updateTime = now();
		return re;
	}

	

	
	

	public int entityUpdate(Entity entry) throws CommandExecuteExecption {
		int re = 0;

		Class cls = entry.getClass();
		Table table = this.tableGet(cls);
		
		re += table.update(entry);
		
		//entry._updateTime = now();

		return re;
	}


	public <E extends Entity> int entityUpdateAll(Collection<E> entrys) throws CommandExecuteExecption {
		int re = 0;
		Class cls = entrys.stream().findFirst().getClass();
		Table table = this.tableGet(cls);
		
		re += table.update(entrys);
		

		return re;
	}

	

	public int entityRemove(Entity entry) throws CommandExecuteExecption {
		int re = 0;

		Class cls = entry.getClass();
		Table table = this.tableGet(cls);
		
	    re += table.delete(entry);
		

		return re;
	}

	public <E extends Entity> int entityRemoveAll(Collection<E> entrys) throws CommandExecuteExecption {
		int re = 0;
        
		Class cls = entrys.stream().findFirst().getClass();
		Table table = this.tableGet(cls);
		
		
	    re += table.delete(entrys);
		

		return re;
	}

	public void executeSetAutoCommit(boolean b) throws CommandExecuteExecption {
        try {
		for (Connection conn : localConnection.get()) {
			conn.setAutoCommit(b);

		}
        }catch(SQLException sex) {throw CommandExecuteExecption.forSql(sex,"setAutoCommit",b);}

	}

	public void executeCommit() throws CommandExecuteExecption {
		try {
		for (Connection conn : localConnection.get()) {
			conn.commit();

		}
		}catch(SQLException sex) {throw CommandExecuteExecption.forSql(sex,"executeCommit");}
	}
	
	public void executeRollback(Checkpoint...points) throws CommandExecuteExecption {
		try {
		for (Connection conn : localConnection.get()) {
			if(points.length==0) {
			  conn.rollback();
			}else {
				conn.rollback(new Savepoint() {
					
					@Override
					public String getSavepointName() throws SQLException {
						// TODO Auto-generated method stub
						String ret=null;
						try {
							ret=points[0].getPointName();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new SQLException(e);
						}
						return ret;
					}
					
					@Override
					public int getSavepointId() throws SQLException {
						// TODO Auto-generated method stub
						int ret=0;
						try {
							ret=points[0].getPointId();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							throw new SQLException(e);
						}
						return ret;
					}
				});
			}

		}
		}catch(SQLException sex) {throw CommandExecuteExecption.forSql(sex,"executeRollback",points);}
	}
	
	public Checkpoint[] executeSetCheckpoint(String... points) throws CommandExecuteExecption {
		PoolList<Connection> connections=localConnection.get();
		Savepoint[] ret0=new Savepoint[connections.size()];
		Checkpoint[] ret=new Checkpoint[ret0.length];
			
				try {
			      
				  for(int i=0;i<connections.size();i++) {
					  Connection conn=connections.get(i);
				   if(points.length==0) {
					
						ret0[i]= conn.setSavepoint();
					    
					}else {
						ret0[i]=conn.setSavepoint(points[0]);
					}
				   ret[i]= Checkpoint.forSql(ret0[i]);
				  }
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw CommandExecuteExecption.forSql(e, "executeSetCheckpoint", points);
				}
		
		
		
		return ret;
	}

	private  PoolList<Connection> getConnections() throws CommandExecuteExecption{
       PoolList<Connection> connections = localConnection.get();
		
		if(connections==null) {
			 DataSource[] dss=getDataSources();
			 try {
				connections = new PoolList<Connection>(dss.length) {

					@Override
					public Connection newSingle(int i) throws Exception {
						// TODO Auto-generated method stub
						return dss[i].getConnection();
					}

				};
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw CommandExecuteExecption.forSql(e, "getConnections");
			}

			localConnection.set(connections);
			
		}
		return connections;
	}
	
	@SuppressWarnings("resource")
	protected final Connection getConnection() throws CommandExecuteExecption {
		PoolList<Connection> connections = getConnections();
		
		Connection ret = connections.getNext();
		
		return ret;
	}

	
	
	

	

	protected  Object[][] callProcedure(String procName, Object... params) throws CommandExecuteExecption {

		List<Object[]> ret = new ArrayList<Object[]>();
		StringBuffer sql = new StringBuffer();
		sql.append("{call ").append(procName).append("(");

		String[] paramStrs = new String[params.length];
		for (int i = 0; i < paramStrs.length; i++) {

			paramStrs[i] = "?";

		}
		sql.append(String.join(",", paramStrs));

		sql.append(")}");
		int cc = 0;
		try (CallableStatement call = getConnection().prepareCall(sql.toString());) {
			boolean isOutputParam = false;
			for (int i = 0; i < params.length; i++) {
				if (params[i] instanceof OutputParam) {
					OutputParam outputParam = (OutputParam) params[i];
					call.registerOutParameter(i + 1, outputParam.sqlType);
					if (outputParam.value != null) {
						call.setObject(i + 1, outputParam.value);
					}
					isOutputParam = true;
				} else {
					call.setObject(i + 1, params[i]);
				}

			}

			call.execute();

			ResultSet resultSet = call.getResultSet();
			ResultSetMetaData metaData = resultSet.getMetaData();
			cc = metaData.getColumnCount();
			while (resultSet.next()) {
				Object[] objects = new Object[cc];
				for (int i = 0; i < cc; i++) {
					objects[i] = resultSet.getObject(i + 1);
				}
				ret.add(objects);
			}

			if (isOutputParam) {
				call.getMoreResults();
				Object[] outRe=new Object[params.length];
				for (int i = 0; i < params.length; i++) {
					if (params[i] instanceof OutputParam) {
						OutputParam outputParam = (OutputParam) params[i];
						outputParam.result = call.getObject(i + 1);
						outRe[i]=outputParam.result;
					}

				}
				ret.add(outRe);
			}

		}catch(SQLException sex) {
			throw CommandExecuteExecption.forSql(sex, procName, params);
		}
		return ret.toArray(new Object[ret.size()][cc]);

	}





	@Override
	public void close()  {
		// TODO Auto-generated method stub
		localConnection.get().parallelStream().forEach(conn-> {try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
	}


    protected String formatEl(String command) {
    	
    	String ret=View.formatEl(command, viewMap);
    	
    	ret=Criteria.formatEl(ret, columnMap);
    	
    	return ret;
    }






	
   
    
	
	
	

}
