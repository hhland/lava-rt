package lava.rt.linq;

import static org.junit.Assert.assertNotNull;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;
import javax.sql.PooledConnection;

import lava.rt.base.LangObject;

import lava.rt.base.PoolList;
import lava.rt.common.SqlCommon;
import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;

public abstract class DataContext extends LangObject {

	public static boolean DEBUG = false;

	private final Map<Class, Table> tableMap = new HashMap<>();

	private final Map<Class, View> viewMap = new HashMap<>();

	private final ThreadLocal<PoolList<Connection>> localConnection = new ThreadLocal<>();

	public DataContext(DataSource... dataSources) throws Exception {
		super();
		this.dataSources = dataSources;

		PoolList<Connection> connections = new PoolList<Connection>(dataSources.length) {

			@Override
			public Connection newSingle(int i) throws Exception {
				// TODO Auto-generated method stub
				return dataSources[i].getConnection();
			}

		};

		localConnection.set(connections);
	}
	
	

	private DataSource[] dataSources;

	public <M extends Entity> Table<M> createTable(Class<M> cls, String tableName, String pkName) {
		Table<M> table = new Table<M>(this, cls, tableName, pkName);
		tableMap.put(cls, table);
		viewMap.put(cls, table);
		return table;
	}

	public <M extends Entity> View<M> createView(Class<M> cls, String tableName) {
		View<M> view = new View<M>(this, cls, tableName);
		viewMap.put(cls, view);
		return view;
	};

	public <M extends Entity> Table<M> getTable(Class<M> mcls) {
		Table<M> ret = (Table<M>) tableMap.get(mcls);
		return ret;
	}

	public <M extends Entity> View<M> getView(Class<M> mcls) {
		View<M> ret = (View<M>) viewMap.get(mcls);
		return ret;
	};

	public <M extends Entity> List<M> executeQueryList(Class<M> cls, String sql, Object... params) throws SQLException {
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

				View<M> view = getView(cls);

				while (resultSet.next()) {
					M m = newEntry(cls);

					if (m == null) {
						throw new SQLException(cls.getName() + " can't be instance");
					}
					int c = 0;
					for (Iterator<String> it = view.entryFieldMap.keySet().iterator(); c < metaData.getColumnCount()
							&& it.hasNext();) {
						String columnName = it.next().toUpperCase();
						if (!meteDataMap.containsKey(columnName))
							continue;
						int columnIndex = meteDataMap.get(columnName);
						Field field = view.entryFieldMap.get(columnName);
						try {
							field.set(m, resultSet.getObject(columnIndex));
						} catch (Exception e) {
							continue;
						}
						c++;
					}

					list.add(m);
				}
			}
		}

		return list;
	}

	public Object[][] executeQueryArray(String sql, Object... params) throws SQLException {
		Connection connection = getConnection();
		Object[][] re = SqlCommon.executeQueryArray(connection, sql, params);

		return re;
	}

	public String executeQueryJsonArray(String sql, Object... params) throws SQLException {

		StringBuffer ret = new StringBuffer("[");
		try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql);) {
			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
			}
			try (ResultSet resultSet = preparedStatement.executeQuery();) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				int cc = metaData.getColumnCount();
				// String[] row=new String[cc];
				// Map<String, Object> rowMap = null;
				while (resultSet.next()) {
					ret.append("{");

					for (int i = 0; i < cc; i++) {
						Object colObject = resultSet.getObject(i + 1);
						if (colObject == null) {
							continue;
						}
						String colName = metaData.getColumnName(i + 1), colValue = colObject.toString();

						ret.append(colName).append(":");
						if (colObject instanceof String || colObject instanceof java.sql.Date) {
							ret.append("\"").append(colValue).append("\"");

						} else {
							ret.append(colValue);
						}

					}

					ret.append("}");
					// list.add(rowMap);
				}
			}
		}
		ret.append("]");
		return ret.toString();
	}

	public int executeUpdate(String sql, Object... param) throws SQLException {
		int re = 0;
		PoolList<Connection> connections = localConnection.get();
		if (connections == null) {
			//printErr("error:" + sql);
		} else if (connections.size() == 1) {
			re += SqlCommon.executeUpdate(connections.get(0), sql, param);
		} else if (connections.size() > 1) {
			AtomicInteger are = new AtomicInteger(0);
			AtomicReference<SQLException> sex = new AtomicReference<>();
			connections.parallelStream().forEach(conn -> {

				try {
					are.getAndAdd(SqlCommon.executeUpdate(conn, sql, param));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					String errMsg = "errorSql:" + sql;
					errMsg += "\nerrorParam:[";
					for (Object _param : param) {
						errMsg += _param + ",";
					}
					errMsg += "]";
					//printErr(errMsg);
					sex.set(e);
				}
			});
			if (sex.get() != null)
				throw sex.get();
			re = are.get();
		}

		return re;
	}

	public int executeInsertReturnPk(String sql, Object... param) throws SQLException {
		int pk = 0;
		for (Connection connection : localConnection.get()) {

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
			}
		}
		return pk;
	}

	
	
	public int executeBatch(String sql, Object[]... params) throws SQLException {
		int re = 0;
		PoolList<Connection> connections = localConnection.get();
		if (connections.size() == 1) {
			re += SqlCommon.executeBatch(connections.get(0), sql, params);
		} else if (connections.size() > 1) {
			AtomicInteger are = new AtomicInteger(0);
			AtomicReference<SQLException> sex = new AtomicReference<>();
			connections.parallelStream().forEach(conn -> {

				try {
					are.getAndAdd(SqlCommon.executeBatch(conn, sql, params));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					sex.set(e);
				}
			});
			if (sex.get() != null)
				throw sex.get();
			re = are.get();
		}

		return re;
	}

	public int insert(Collection<? extends Entity> entrys) throws SQLException {
		AtomicInteger re = new AtomicInteger(0);
		AtomicReference<SQLException> sex = new AtomicReference<>();
		final PoolList<Connection> connections = localConnection.get();
		entrys.parallelStream().forEach(entry -> {
			try {
				re.getAndAdd(insert(entry, connections));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				sex.set(e);
				
			}
		});
		if (sex.get() != null) {
			throw sex.get();
		}

		return re.get();
	}

	

	protected int insert(Entity entry, PoolList<Connection> connections) throws SQLException {
		localConnection.set(connections);
		return insert(entry);
	}

	@SuppressWarnings("unchecked")
	public int insert(Entity entry) throws SQLException {
		int re = 0;
		Class<? extends Entity> cls = entry.getClass();
		Table table = this.getTable(cls);
		boolean hasPk = table.getPk(entry) != null;
		if (hasPk) {
			re += table.insert(entry);

		} else {
			re += table.insertWithoutPk(entry);
		}
		entry._updateTime = now();
		return re;
	}

	public int update(Entity entry) throws SQLException {
		int re = 0;

		Class cls = entry.getClass();
		Table table = this.getTable(cls);
		re += table.update(entry);
		entry._updateTime = now();

		return re;
	}

	protected int update(Entity entry, PoolList<Connection> connections) throws SQLException {
		localConnection.set(connections);
		return update(entry);
	}

	public int update(Collection<? extends Entity> entrys) throws SQLException {
		AtomicInteger re = new AtomicInteger(0);
		AtomicReference<SQLException> sex = new AtomicReference<>();
		final PoolList<Connection> connections = localConnection.get();
		entrys.parallelStream().forEach(entry -> {
			try {
				re.getAndAdd(update(entry, connections));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				sex.set(e);
			}
		});
		if (sex.get() != null) {
			throw sex.get();
		}

		return re.get();
	}

	protected int delete(Entity entry, PoolList<Connection> connections) throws SQLException {
		localConnection.set(connections);
		return delete(entry);
	}

	public int delete(Entity entry) throws SQLException {
		int re = 0;

		Class cls = entry.getClass();
		Table table = this.getTable(cls);
		re += table.delete(entry);

		return re;
	}

	public int delete(Collection<? extends Entity> entrys) throws SQLException {
		AtomicInteger re = new AtomicInteger(0);
		AtomicReference<SQLException> sex = new AtomicReference<>();
		final PoolList<Connection> connections = localConnection.get();
		entrys.parallelStream().forEach(entry -> {
			try {
				re.getAndAdd(delete(entry, connections));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				sex.set(e);
			}
		});
		if (sex.get() != null) {
			throw sex.get();
		}
		return re.get();
	}

	public void setAutoCommit(boolean b) throws SQLException {

		for (Connection conn : localConnection.get()) {
			conn.setAutoCommit(b);

		}

	}

	public void commit() throws SQLException {
		for (Connection conn : localConnection.get()) {
			conn.commit();

		}
	}
	
	public void rollback(Savepoint...savepoints) throws SQLException {
		for (Connection conn : localConnection.get()) {
			if(savepoints.length==0) {
			  conn.rollback();
			}else {
				conn.rollback(savepoints[0]);
			}

		}
	}
	
	public Savepoint[] setSavepoint(String...savepoints) throws SQLException {
		PoolList<Connection> connections=localConnection.get();
		Savepoint[] ret=new Savepoint[connections.size()];
		try {
			connections.each((i,conn)->{
				try {
				if(savepoints.length==0) {
					
						ret[i]= conn.setSavepoint();
					
					}else {
						ret[i]=conn.setSavepoint(savepoints[0]);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					throw e;
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SQLException(e);
		}
		
		return ret;
	}

	@SuppressWarnings("resource")
	protected Connection getConnection() throws SQLException {
		PoolList<Connection> connections = localConnection.get();
		Connection ret = connections.getNext();
		while (!ret.isValid(0)) {
			ret = connections.getNext();
		}
		return ret;
	}

	public <E extends Entity> E newEntry(Class<E> entryClass) {
		E ret = null;
		try {

			ret = (E) entryClass.getConstructors()[0].newInstance(this);
			ret._createTime = now();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return ret;
	}

	protected static Date now() {
		// TODO Auto-generated method stub
		return Calendar.getInstance().getTime();
	}

	protected Object[][] callProcedure(String procName, Object... params) throws SQLException {

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
				for (int i = 0; i < params.length; i++) {
					if (params[i] instanceof OutputParam) {
						OutputParam outputParam = (OutputParam) params[i];
						outputParam.result = call.getObject(i + 1);
					}

				}
			}

		}
		return ret.toArray(new Object[ret.size()][cc]);

	}
	
	

}
