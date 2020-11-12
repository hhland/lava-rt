package lava.rt.common;


import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;


import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;





public final class SqlCommon {

	public static int executeBatch(Connection connection, String sql, Object[]... params) throws SQLException {

		int re = 0;
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {

			for (Object[] param : params) {
				for (int i = 0; i < param.length; i++) {
					preparedStatement.setObject(i + 1, param[i]);
				}
				preparedStatement.addBatch();
				re++;
			}
			int[] res = preparedStatement.executeBatch();
			for (int i : res) {
				re += i;
			}
		}

		return re;
	}

	public static int executeBatch(Connection connection, String[] sqls) throws SQLException {

		int re = 0;
		try (Statement statement = connection.createStatement();) {

			for (String sql : sqls) {
				statement.addBatch(sql);
				re++;
			}
			int[] res = statement.executeBatch();
			for (int i : res) {
				re += i;
			}
		}

		return re;
	}

	public static int executeUpdate(Connection connection, String sql, Object... params) throws SQLException {

		int re = 0;
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {

			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
			}

			re = preparedStatement.executeUpdate();
		}

		return re;
	}

	
	public static void executeQueryForeach(Connection connection,BiFunction<Integer,Object[],Integer> rowHandler, String sql, Object... params)
			throws SQLException {
		int cc = 0;
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
				
			}
			preparedStatement.setFetchSize(Integer.MIN_VALUE);
			try (ResultSet resultSet = preparedStatement.executeQuery();) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				cc = metaData.getColumnCount();
				int rowIndex=0,hre=0;
				Object[] row = new Object[cc];
				while (resultSet.next()) {
					if(hre>0) {
						hre--;
						continue;
					}else if(hre<0) {
						break;
					}
					
					for (int i = 0; i < cc; i++) {
						row[i] = resultSet.getObject(i + 1);
					}
					hre=rowHandler.apply(rowIndex,row);
					
				}
			}
		}

		
	}
	
	
	public static Object[][] executeQueryArray(Connection connection, String sql, Object... params)
			throws SQLException {
		int cc = 0;
		List<Object[]> list = new ArrayList<Object[]>();
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
			}
			try (ResultSet resultSet = preparedStatement.executeQuery();) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				cc = metaData.getColumnCount();
				while (resultSet.next()) {
					Object[] objects = new Object[cc];
					for (int i = 0; i < cc; i++) {
						objects[i] = resultSet.getObject(i + 1);
					}
					list.add(objects);
				}
			}
		}

		return list.toArray(new Object[list.size()][cc]);
	}

	public static List<Map<String, Object>> executeQueryListMap(Connection connection, String sql, Object... params)
			throws SQLException {

		List<Map<String, Object>> list = new ArrayList<>();
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
			}
			try (ResultSet resultSet = preparedStatement.executeQuery();) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				int cc = metaData.getColumnCount();
				Map<String, Object> rowMap = null;
				while (resultSet.next()) {
					rowMap = new HashMap<>();
					for (int i = 0; i < cc; i++) {
						rowMap.put(metaData.getColumnName(i + 1), resultSet.getObject(i + 1));
					}
					list.add(rowMap);
				}
			}
		}

		return list;
	}

	public static String executeQueryListJson(Connection connection, String sql, Object... params) throws SQLException {
		StringBuffer ret = new StringBuffer("[");

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
			}
			try (ResultSet resultSet = preparedStatement.executeQuery();) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				int cc = metaData.getColumnCount();
				
				while (resultSet.next()) {
                    ret.append("{");
					for (int i = 0; i < cc; i++) {
						String key=metaData.getColumnName(i + 1);
						Object val= resultSet.getObject(i + 1);
						ret.append("\"").append(key).append("\":");
						if(val==null) {
							ret.append("null");
						}else if(val instanceof Number) {
							ret.append(val);
						}else {
							ret.append("\"").append(val).append("\"");	
						}
						
						if(i<cc-1) {
							ret.append(",");
						}
					}
					ret.append("},");
				}
			}
		}
		ret.deleteCharAt(ret.length());
        ret.append("]");
		return ret.toString();
	}

	public static <E> E read(Blob blob) throws Exception {
		// TODO Auto-generated method stub
		E ret = null;
		try (ObjectInputStream stream = new ObjectInputStream(blob.getBinaryStream())) {

			// ObjectInputStream in=new ObjectInputStream(stream);
			ret = (E) stream.readObject(); // 读出对象
		}

		return ret;
	}

	public static void write(Blob blob, Object record) throws Exception {
		// TODO Auto-generated method stub

		try (ObjectOutputStream stream = new ObjectOutputStream(blob.setBinaryStream(0));) {
			stream.writeObject(record);
		}
	}

	public static String read(Clob clob) throws SQLException, IOException {

		StringBuffer sb = new StringBuffer();
		Reader is = clob.getCharacterStream();// 得到流
		try (BufferedReader br = new BufferedReader(is);) {
			String s = br.readLine();

			while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
				sb.append(s);
				s = br.readLine();
			}
		}
		return sb.toString();
	}
	
	public static void write(Clob clob, String str) throws Exception {
		// TODO Auto-generated method stub

		try (BufferedWriter stream = new BufferedWriter(clob.setCharacterStream(0))) {
			stream.write(str);
		}
	}

	
	
	
}
