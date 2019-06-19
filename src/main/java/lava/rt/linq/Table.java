package lava.rt.linq;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import lava.rt.common.ReflectCommon;
import lava.rt.common.TextCommon;


public class Table<M extends Entity> extends View<M> {

	protected final String pkName;

	protected final Field pkField;

	protected final Field[] insertFields, updateFields;

	protected final String sqlInsert,sqlInsertWithoutPk, sqlUpdate, sqlDelete,sqlLoad;

	public Table(DataContext dataContext, Class<M> entryClass, String tableName, String pkName) {
		super(dataContext, entryClass, tableName);
		this.pkName = pkName;

		pkField = entryFieldMap.get(pkName);
		pkField.setAccessible(true);

		List<Field> linsertFields = new ArrayList<Field>();
		for (Field f : entryFieldMap.values()) {
			String fname = f.getName();
			if (ReflectCommon.isThis0(f) || fname.equalsIgnoreCase(pkName)) {
				continue;
			}
			f.setAccessible(true);
			linsertFields.add(f);
		}
		insertFields = linsertFields.toArray(new Field[linsertFields.size()]);

		List<Field> lupdateFields = new ArrayList<Field>();

		for (Field f : entryFieldMap.values()) {
			String fname = f.getName();
			if (ReflectCommon.isThis0(f) || fname.equalsIgnoreCase(pkName)) {
				continue;
			}
			f.setAccessible(true);
			lupdateFields.add(f);
		}
		updateFields = lupdateFields.toArray(new Field[lupdateFields.size()]);

		String sqlPattern = "insert into {0} ({1}) values ({2})", cols =  "", vals = "";

		for (Field field : insertFields) {

			String fname = field.getName();

			cols += fname + ",";
			vals += " ?,";

		}
		cols = TextCommon.trim(cols, ",");
		vals = TextCommon.trim(vals, ",");
		sqlInsert = MessageFormat.format(sqlPattern, tableName, pkName+","+cols, "?,"+vals);
		sqlInsertWithoutPk = MessageFormat.format(sqlPattern, tableName, cols, vals);
		
		 sqlPattern = "update {0} set {1} where {2}=? ";
		 String key = "";

		

			for (Field field : updateFields) {
				String fname = field.getName();

				key += MessageFormat.format(" {0} =? ,", fname);

			}
			key = TextCommon.trim(key, ",");
			// key=TextCommon.repeat(" `{0}` =? ", ",", updateFields.length);
			sqlUpdate = MessageFormat.format(sqlPattern, this.tableName, key, this.pkName);

			
			sqlPattern = "delete from {0} where {1}=? ";

			
			sqlDelete = MessageFormat.format(sqlPattern, tableName, pkName);

			sqlLoad = MessageFormat.format("select * from {0} where {1}= ? ", this.tableName, this.pkName);
	}

	public M load(Object pk) throws SQLException {

		String sql = sqlLoad;
		
		List<M> entrys = dataContext.<M>executeQueryList(entryClass,sql , pk);
		M ret = null;
		if (entrys.size() == 1) {
			ret = entrys.get(0);
		} else {
			throw new SQLException(
					"there has not only 1 but " + entrys.size() + " entrys in [" + tableName + "] has [" + pk + "]");
		}
		return ret;
	}

	public <E extends M> int insert(Collection<E> entrys) throws SQLException {
		if (entrys.size() == 0)
			return 0;
		String  sql = sqlInsert;
		
		int re = 0, insertsize = insertFields.length;

		Object[][] params = new Object[entrys.size()][insertsize + 1];
		try {
			int i=0;
			//for (int i = 0; i < entrys.length; i++) {
			for(M obj : entrys) {
				//M obj = entrys[i];
				params[i][0] = pkField.get(obj);
				for (int j = 0; j < insertsize; j++) {
					Field field = insertFields[j];
					params[i][j + 1] = field.get(obj);
				}
				i++;
			}
		} catch (Exception e) {
			throw new SQLException(e);
		}

		re = dataContext.executeBatch(sql, params);
		

		return re;

	}
	
	
	
	public <E extends M> int insert(E entry) throws SQLException {
		
		
		
		int re = 0, insertsize = insertFields.length;

		Object[] param = new Object[insertsize + 1];
		try {
			
				param[0] = pkField.get(entry);
				for (int j = 0; j < insertsize; j++) {
					Field field = insertFields[j];
					param[j + 1] = field.get(entry);
				}
		} catch (Exception e) {
			throw new SQLException(e);
		}
		re = dataContext.executeUpdate(sqlInsert, param);
		

		return re;

	}

	public <E extends M> int insertWithoutPk(E... entrys) throws SQLException {
        AtomicInteger re=new AtomicInteger(0);
        AtomicReference<SQLException> sex=new AtomicReference<>();
		String sql=sqlInsertWithoutPk;
		
		int insertsize = insertFields.length;

		Object[] param = new Object[insertsize];

		Stream.of(entrys).parallel().forEach(entry->{
			try {
				for (int j = 0; j < insertsize; j++) {
					Field field = insertFields[j];
					param[j] = field.get(entry);
				}
				int pk = dataContext.executeInsertReturnPk(sql, param);
				pkField.set(entry, pk);
			    re.getAndIncrement();
			} catch (Exception se) {
				SQLException nse = new SQLException(se);
				
				sex.set(nse);
			} 
		});
		
		if(sex.get()!=null) {
			throw sex.get();
		}
		
		
		return re.get();

	}

	
	public <E extends M> int update(E entry) throws SQLException {
		
		int updatesize = updateFields.length;
		Object[] param = new Object[updatesize + 1];
		
        try {
		for (int j = 0; j < updatesize; j++) {
			Field field = updateFields[j];
			param[j] = field.get(entry);
	    }
        }catch(Exception ex) {
        	throw new SQLException(ex);
        }
		param[updatesize] = getPk(entry);

			
		int re = dataContext.executeUpdate(sqlUpdate, param);
		
		return re;

	}
	
	
	public <E extends M> int update(Collection<E> entrys) throws SQLException {
		if (entrys.size() == 0)
			return 0;
		
		
		int updatesize = updateFields.length;
		Object[][] params = new Object[entrys.size()][updatesize + 1];
		try {
			int i=0;
			//for (int i = 0; i < entrys.size(); i++) {
			for(M obj:entrys) {
				//M obj = entrys[i];

				for (int j = 0; j < updatesize; j++) {
					Field field = updateFields[j];
					params[i][j] = field.get(obj);
				}
				params[i][updatesize] = getPk(obj);
                i++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SQLException(e);
		}
		int re =  dataContext.executeBatch(sqlUpdate, params);
		
		return re;

	}
	
	
	public <E extends M> int delete(Collection<E> entrys) throws SQLException {
		int re = 0;
		if (entrys.size() == 0)
			return re;
		
		
		
		int dlength = entrys.size();
		Object[][] params = new Object[dlength][1];
        int i=0;
		for(M entry :entrys) {
			params[i][0] = getPk(entry);
			i++;
		}

		re = dataContext.executeBatch(sqlDelete, params);
		
		return re;
	}

	public <E extends M> int delete(E entry) throws SQLException {
		
		
		
		Object[] param = new Object[] {getPk(entry)};

		int re = 0;
		
		re = dataContext.executeUpdate(sqlDelete, param);
		
		return re;
	}

	public int truncate() throws SQLException {
		String sql = "truncate table " + tableName;
		int re = dataContext.executeUpdate(sql);
		return re;
	}

	public <E extends M,R> R getPk(E entry) {
		R ret = null;
		try {
			ret = (R)pkField.get(entry);
		} catch (IllegalArgumentException | IllegalAccessException e) {
		}
		return ret;
	}
	
	public Table<M> duplicate(String tableName){
		Table<M> ret=new Table<>(this.dataContext, this.entryClass, tableName, pkName);
		return ret;
	}

}
