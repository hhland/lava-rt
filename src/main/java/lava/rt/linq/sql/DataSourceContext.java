package lava.rt.linq.sql;







import java.io.Closeable;
import java.lang.reflect.Field;
import java.sql.*;

import java.util.*;
import java.util.Date;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

import javax.sql.DataSource;


import lava.rt.cache.CacheItem;

import lava.rt.common.ReflectCommon;
import lava.rt.common.SqlCommon;
import lava.rt.linq.Checkpoint;
import lava.rt.linq.CommandExecuteExecption;
import lava.rt.linq.DuplicateKeyException;
import lava.rt.linq.Entity;
import lava.rt.linq.sql.SelectCommand.PagingSelectCommand;
import lava.rt.linq.CommandExecuteExecption.CmdType;

import lava.rt.wrapper.ListWrapper;
import lava.rt.wrapper.LoggerWrapper;



public abstract class DataSourceContext  implements SqlDataContext,Closeable {

	

	private final Map<Class, TableTemplate> tableMap = new HashMap<>();

	private final Map<Class,ViewTemplate> viewMap = new HashMap<>();
	
	protected  final Map<String,String> columnMap = new HashMap<>();

	private final ThreadLocal<ListWrapper<Connection>> readConnection = new ThreadLocal<>()
			,writeConnection = new ThreadLocal<>()
			;

	
	
	
	protected abstract  Criterias getCriterias();
	

	protected abstract  DataSource[] getReadDataSources() ;


	protected abstract  DataSource[] getWriteDataSources() ;

	

	protected <M extends Entity> TableTemplate<M> createTable(Class<M> cls, String tableName, String pkName,Supplier<M> entityNewer) {
		TableTemplate<M> table=null;
		try {
		table= new TableTemplate<M>(this, cls, tableName, pkName) {
			@Override
			public M newEntity() throws Exception {
				// TODO Auto-generated method stub
				return entityNewer.get();
			}
			
		};
		tableMap.put(cls, table);
		viewMap.put(cls, table);
		}catch(Exception ex) {
			logError(cls,tableName);
		}
		return table;
	}

	protected <M extends Entity> ViewTemplate<M> createView(Class<M> cls, String tableName,Supplier< M> entityNewer) {
		ViewTemplate<M> view = new ViewTemplate<M>(this, cls, tableName) {

			@Override
			public M newEntity() throws Exception {
				// TODO Auto-generated method stub
				return entityNewer.get();
			}
			
		};
		viewMap.put(cls, view);
		return view;
	};

	public <M extends Entity> TableTemplate<M> getTable(Class<M> mcls) {
		TableTemplate<M> ret = tableMap.get(mcls);
		return ret;
	}

	public <M extends Entity> ViewTemplate<M> getView(Class<M> mcls) {
		ViewTemplate<M> ret = viewMap.get(mcls);
		return ret;
	};
	
	protected <E extends Entity> CacheItem<E> getCache(Class<E> cls, Object pk){
		return null;
	}
	
	protected <E extends Entity> void putCache(E entits) {}

	
	public <E extends Entity> E getEntity(Class<E> cls, Object pk) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
		CacheItem<E> cache=getCache(cls, pk);
		E ret=null;
		if(cache==null||cache.isTimeout()||!cache.isEnable()) {
			TableTemplate<E> table=getTable(cls);
			
			ret=table.load(pk);
			
			putCache(ret);
		}else {
			ret=cache.get();
		}
		
		return ret;
	}


	public <M extends Entity> ListWrapper<M> pagingEntities(Class<M> cls, PagingSelectCommand command,Object...param) throws CommandExecuteExecption {
		List<M> rows=listEntities(cls, command.toPaginSql(),param);
		ListWrapper<M> ret=new ListWrapper(rows);
		if(ret.self.size()==command.getLimit()) {
			String countSql=command.getCountSql();
			int total=(int)executeQueryArray(countSql,param)[0][0];
			ret.setTotal(total);
		}
		return ret;
	}


	
	public <M extends Entity> List<M> listEntities(Class<M> cls, SelectCommand command,Object...param) throws CommandExecuteExecption {
		return listEntities(cls, command.toSql(),param);
	}

	

	public <M extends Entity> void cursoringEntities(BiFunction<Integer,M,Integer> cursor,Class<M> cls, SelectCommand command, Object... params) throws CommandExecuteExecption {
		
		Connection connection = getReadConnection();
		String sql=command.toSql();
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
			}
            preparedStatement.setFetchSize(0);
			try (ResultSet resultSet = preparedStatement.executeQuery();) {

				ResultSetMetaData metaData = resultSet.getMetaData();
				Map<String, Integer> meteDataMap = new HashMap<String, Integer>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String key = metaData.getColumnName(i).toUpperCase();
					meteDataMap.put(key, i);
				}

				ViewTemplate<M> view = getView(cls);
				
				int hre=0,rowIndex=0;
				M m= view.newEntity();
				while (resultSet.next()) {
					rowIndex++;
					if(hre>0) {
						hre--;
						continue;
					}else if(hre<0) {
						break;
					}
					
					
					for (Iterator<String> it = view.entityFieldMap.keySet().iterator(); it.hasNext();) {
						String columnName = it.next().toUpperCase();
						Integer columnIndex = meteDataMap.get(columnName);
						if (columnIndex==null)
							continue;
						
						Field field = view.entityFieldMap.get(columnName);
						
						Object cellValue=resultSet.getObject(columnIndex);
						if (cellValue instanceof Clob) {
							Clob clob=(Clob)cellValue;
							String v=SqlCommon.read(clob);
							field.set(m, v);
							
						}else if(cellValue instanceof Blob) {
							
						}else {
						   field.set(m, cellValue);
						}
						
						
						
					}

					hre=cursor.apply(rowIndex, m);
				}
			}
		}catch(Exception ex) {
			SQLException seq=new SQLException(ex);
			
			logExecptioin(seq);
			logError("sql:"+sql+"\nparams:");
			logError(params);
			throw CommandExecuteExecption.forSql(seq, sql, params);
		}

		
	}


	public <M extends Entity> List<M> listEntities(Class<M> cls, String sql, Object... params) throws CommandExecuteExecption {
		
		Connection connection = getReadConnection();
		
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

				ViewTemplate<M> view = getView(cls);
				
				
			
				while (resultSet.next()) {
					M m= view.newEntity();
					
					
					for (Iterator<String> it = view.entityFieldMap.keySet().iterator(); it.hasNext();) {
						String columnName = it.next().toUpperCase();
						Integer columnIndex = meteDataMap.get(columnName);
						if (columnIndex==null)
							continue;
						
						Field field = view.entityFieldMap.get(columnName);
						Object cellValue=resultSet.getObject(columnIndex);
						if (cellValue instanceof Clob) {
							Clob clob=(Clob)cellValue;
							String v=SqlCommon.read(clob);
							field.set(m, v);
							
						}else if(cellValue instanceof Blob) {
							
						}else {
						   field.set(m, cellValue);
						}
						
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

	@Override
	public Object[][] executeQueryArray(String cmd, Object... params) throws CommandExecuteExecption {
		// TODO Auto-generated method stub
        Connection connection= getReadConnection();
		
		Object[][] re =null;
		String sql=cmd;
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
	
	public Object[][] executeQueryArray(SelectCommand command, Object... params) throws CommandExecuteExecption {
		
		Connection connection= getReadConnection();
		
		Object[][] re =null;
		String sql=command.getSql();
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
		LoggerWrapper.CONSOLE.info(method,sql,param);
	}
	
	protected void logError(Object... vals) {
		//LogingCommon.getConsoleLogger(this.getClass()).info(vals);
		LoggerWrapper.CONSOLE.self.log(Level.WARNING, LoggerWrapper.join(vals));
	}


	protected void logExecptioin(Exception exception) {
		LoggerWrapper.CONSOLE.self.log(Level.WARNING,exception.getMessage());
		
	}

	
	public String executeQueryJsonList(String sql, Object... params) throws CommandExecuteExecption {
		
		StringBuffer ret = new StringBuffer("[");
		int size=0;
		String[] columns=null;
		try (PreparedStatement preparedStatement = getReadConnection().prepareStatement(sql);) {
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
	public String executeQueryJsonList(PagingSelectCommand command,Object... param)
			throws CommandExecuteExecption {
			
       StringBuffer ret=new StringBuffer(executeQueryJsonList(command.getSql(),param));
		
		long size=Integer.parseInt(
				  ret.substring(ret.lastIndexOf("size:")+5)
				  ),total=command.getStart()+size;
		if(size==command.getLimit()) {
			//String csql=pagingParam.countSql();
			String countSql=command.getCountSql();
			total=(long)executeQueryArray(countSql,param)[0][0];
		}
		ret
		.append(",total:")
		.append(total)
		;
		return ret.toString();
	}









	public int executeUpdate(String sql, Object... param) throws CommandExecuteExecption {
		int re = 0;
		
		ListWrapper<Connection> connections = getWriteConnections();
		if (connections == null) {
			//printErr("error:" + sql);
		} else if (connections.self.size() == 1) {
			try {
			re += SqlCommon.executeUpdate(connections.self.get(0), sql, param);
			}catch(SQLException seq) {
				
				logExecptioin(seq);
				logError("sql:"+sql+"\nparams:");
				logError(param);
				throw CommandExecuteExecption.forSql(seq, sql, param);
				//throw seq;
			}
		} else if (connections.self.size() > 1) {
			AtomicInteger are = new AtomicInteger(0);
			AtomicReference<CommandExecuteExecption> sex = new AtomicReference<>();
			connections.self.parallelStream().forEach(conn -> {

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
		
		Connection connection = getWriteConnection();

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

	
	
	public int executeBatch(String sql, Object[]... params) throws CommandExecuteExecption {
		int re = 0;
		
		ListWrapper<Connection> connections = getWriteConnections();
		if (connections.self.size() == 1) {
			try {
			re += SqlCommon.executeBatch(connections.self.get(0), sql, params);
			} catch(SQLException seq) {
				
				logExecptioin(seq);
				logError("sql:"+sql+"\nparams:");
				logError(params[0]);
				throw CommandExecuteExecption.forSql(seq, sql, params);
			}
		} else if (connections.self.size() > 1) {
			AtomicInteger are = new AtomicInteger(0);
			AtomicReference<CommandExecuteExecption> sex = new AtomicReference<>();
			connections.self.parallelStream().forEach(conn -> {

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
	
	
	public  int putEntity(Entity entity) throws CommandExecuteExecption,DuplicateKeyException {
		int re = 0;
		Class<? extends Entity> cls = entity.getClass();
		TableTemplate table = this.getTable(cls);
		
		re=table.insert(entity);
		return re;
	}

	

	
	@SuppressWarnings("unchecked")
	public int addEntity(Entity entity) throws CommandExecuteExecption {
		int re = 0;
		Class<? extends Entity> cls = entity.getClass();
		TableTemplate table = this.getTable(cls);
		
		re += table.insert(entity);	
		
		putCache(entity);
		return re;
	}

	public <E extends Entity> int addEntities(Collection<E> entites) throws CommandExecuteExecption {
		int re = 0;
		Class cls = entites.stream().findFirst().getClass();
		TableTemplate table = this.getTable(cls);
		
		
		re += table.insertBatch(entites);
		
		//entity._updateTime = now();
		return re;
	}

	

	
	

	public int updateEntity(Entity entity) throws CommandExecuteExecption {
		int re = 0;

		Class cls = entity.getClass();
		TableTemplate table = this.getTable(cls);
		
		re += table.update(entity);
		
		//entity._updateTime = now();

		return re;
	}


	public <E extends Entity> int updateEntities(Collection<E> entites) throws CommandExecuteExecption {
		int re = 0;
		Class cls = entites.stream().findFirst().getClass();
		TableTemplate table = this.getTable(cls);
		
		re += table.updateBatch(entites);
		

		return re;
	}

	

	public int removeEntity(Entity entity) throws CommandExecuteExecption {
		int re = 0;

		Class cls = entity.getClass();
		TableTemplate table = this.getTable(cls);
		
	    re += table.delete(entity);
		

		return re;
	}

	public <E extends Entity> int removeEntities(Collection<E> entites) throws CommandExecuteExecption {
		int re = 0;
        
		Class cls = entites.stream().findFirst().getClass();
		TableTemplate table = this.getTable(cls);
		
		
	    re += table.deleteBatch(entites);
		

		return re;
	}

	public void executeSetAutoCommit(boolean b) throws CommandExecuteExecption {
        try {
		for (Connection conn : writeConnection.get().self) {
			conn.setAutoCommit(b);

		}
        }catch(SQLException sex) {throw CommandExecuteExecption.forSql(sex,"setAutoCommit",b);}

	}

	public void executeCommit() throws CommandExecuteExecption {
		try {
		for (Connection conn : writeConnection.get().self) {
			conn.commit();

		}
		}catch(SQLException sex) {throw CommandExecuteExecption.forSql(sex,"executeCommit");}
	}
	
	public void executeRollback(Checkpoint...points) throws CommandExecuteExecption {
		try {
		for (Connection conn : writeConnection.get().self) {
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
		ListWrapper<Connection> connections=writeConnection.get();
		Savepoint[] ret0=new Savepoint[connections.self.size()];
		Checkpoint[] ret=new Checkpoint[ret0.length];
			
				try {
			      
				  for(int i=0;i<connections.self.size();i++) {
					  Connection conn=connections.self.get(i);
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

	private  ListWrapper<Connection> getReadConnections() throws CommandExecuteExecption{
       ListWrapper<Connection> connections = readConnection.get();
		
		if(connections==null) {
			 connections=new ListWrapper<>(new ArrayList<>());
			 readConnection.set(connections);
			 DataSource[] dss=getReadDataSources();
			 try {
				for(DataSource ds:dss) {
					connections.self.add(ds.getConnection());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw CommandExecuteExecption.forSql(e, "getConnections");
			}

			readConnection.set(connections);
			
		}
		return connections;
	}
	
	@SuppressWarnings("resource")
	protected final Connection getReadConnection() throws CommandExecuteExecption {
		ListWrapper<Connection> connections = getReadConnections();
		
		Connection ret = connections.getNext();
		
		return ret;
	}

	
	private  ListWrapper<Connection> getWriteConnections() throws CommandExecuteExecption{
	       ListWrapper<Connection> connections = writeConnection.get();
			
			if(connections==null) {
				 connections=new ListWrapper<>(new ArrayList<>());
				 readConnection.set(connections);
				 DataSource[] dss=getWriteDataSources();
				 try {
					 for(DataSource ds:dss) {
							connections.self.add(ds.getConnection());
						}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw CommandExecuteExecption.forSql(e, "getConnections");
				}

				writeConnection.set(connections);
				
			}
			return connections;
		}
		
		@SuppressWarnings("resource")
		protected final Connection getWriteConnection() throws CommandExecuteExecption {
			ListWrapper<Connection> connections = getWriteConnections();
			
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
		try (CallableStatement call = getWriteConnection().prepareCall(sql.toString());) {
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
		writeConnection.get().self.parallelStream().forEach(conn-> {try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
	}


    






	
   
    
	
	
	

}
