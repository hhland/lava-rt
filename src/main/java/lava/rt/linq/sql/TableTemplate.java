package lava.rt.linq.sql;

import java.io.Serializable;
import java.lang.reflect.Field;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import java.util.stream.Stream;

import lava.rt.common.ReflectCommon;
import lava.rt.common.LangCommon;
import lava.rt.linq.CommandExecuteExecption;
import lava.rt.linq.Entity;


public abstract class TableTemplate<M extends Entity> extends ViewTemplate<M> {

	
	
	protected final String pkName;

	protected final Field pkField;
	
	//protected final Long pkFieldOffset;

	protected final Field[] insertFields, updateFields;
	
	//protected final Long[] insertFieldOffsets, updateFieldOffsets;

	protected final String sqlInsert,sqlInsertWithoutPk, sqlUpdate, sqlDelete,sqlLoad,sqlColumns;

	public TableTemplate(DataSourceContext dataContext, Class<M> entryClass, String tableName, String pkName) {
		super(dataContext, entryClass, tableName);
		this.pkName = pkName;

		pkField = entityFieldMap.get(pkName);
		pkField.setAccessible(true);
		
		
		List<Field> linsertFields = new ArrayList<Field>();
		for (Field f : entityFieldMap.values()) {
			String fname = f.getName();
			if (ReflectCommon.isThis0(f) || fname.equalsIgnoreCase(pkName)) {
				continue;
			}
			f.setAccessible(true);
			linsertFields.add(f);
		}
		insertFields = linsertFields.toArray(new Field[linsertFields.size()]);
		
		//insertFieldOffsets=new Long[insertFields.length];
		
		

		List<Field> lupdateFields = new ArrayList<Field>();

		for (Field f : entityFieldMap.values()) {
			String fname = f.getName();
			if (ReflectCommon.isThis0(f) || fname.equalsIgnoreCase(pkName)) {
				continue;
			}
			f.setAccessible(true);
			lupdateFields.add(f);
		}
		updateFields = lupdateFields.toArray(new Field[lupdateFields.size()]);
		
		//updateFieldOffsets=new Long[updateFields.length];
		
		

		String sqlPattern = "insert into {0} ({1}) values ({2})", cols =  "", vals = "";

		for (Field field : insertFields) {

			String fname = field.getName();

			cols += fname + ",";
			vals += " ?,";

		}
		cols = LangCommon.trim(cols, ",");
		vals = LangCommon.trim(vals, ",");
		sqlColumns=cols;
		sqlInsert = MessageFormat.format(sqlPattern, tableName, pkName+","+cols, "?,"+vals);
		sqlInsertWithoutPk = MessageFormat.format(sqlPattern, tableName, cols, vals);
		
		 sqlPattern = "update {0} set {1} where {2}=? ";
		 String key = "";

		

			for (Field field : updateFields) {
				String fname = field.getName();

				key += MessageFormat.format(" {0} =? ,", fname);

			}
			key = LangCommon.trim(key, ",");
			// key=LangCommon.repeat(" `{0}` =? ", ",", updateFields.length);
			sqlUpdate = MessageFormat.format(sqlPattern, this.tableName, key, this.pkName);

			
			sqlPattern = "delete from {0} where {1}=? ";

			
			sqlDelete = MessageFormat.format(sqlPattern, tableName, pkName);

			sqlLoad = MessageFormat.format("select * from {0} where {1}= ? ", this.tableName, this.pkName);
	}

	public M load(Serializable pk) throws CommandExecuteExecption {

		String sql = sqlLoad;
		
		List<M> entrys = dataContext.<M>listEntities(entryClass,sql , pk);
		M ret = null;
		if (entrys.size() == 1) {
			ret = entrys.get(0);
		} 
		return ret;
	}

	public <E extends M> int insertBatch(Collection<E> entrys) throws CommandExecuteExecption {
		if (entrys.size() == 0)
			return 0;
		String  sql = sqlInsert;
		
		int re = 0, insertsize = insertFields.length;

		Object[][] params = new Object[entrys.size()][insertsize + 1];
		try {
			int i=0;
			
			for(M obj : entrys) {
				
				params[i][0]=obj.thisPk();
				for (int j = 0; j < insertsize; j++) {
					Field field = insertFields[j];
					params[i][j + 1] = field.get(obj);
					
				}
				i++;
			}
		} catch (Exception e) {
		//	throw new ExecuteExecption(e);
		}

		re = dataContext.executeBatch(sql, params);
		

		return re;

	}
	
	
	
	public <E extends M> int insert(E entry) throws CommandExecuteExecption {
		
		
		
		int re = 0, insertsize = insertFields.length;

		
		if(entry.thisPk()==null) {
			Object[] param = new Object[insertsize];
			try {
			for (int j = 0; j < insertsize; j++) {
				Field field = insertFields[j];
				param[j] = field.get(entry);
			}
			
			}catch(Exception ex) {}
			int pk = dataContext.executeInsertReturnPk(sqlInsertWithoutPk, param);
			try {
				pkField.set(entry, pk);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw CommandExecuteExecption.forSql(e, sqlInsert, param);
			} 
		}else {
			
			Object[] param = new Object[insertsize + 1];
			try {
				    
					param[0] = entry.thisPk();
			        //param[0] = getPk(entry);
					for (int j = 0; j < insertsize; j++) {
						Field field = insertFields[j];
						param[j + 1] = field.get(entry);
						//param[j+1]=unsafeAdapter.getObject(entry, insertFieldOffsets[j]);
					}
			} catch (Exception e) {
			//	throw new ExecuteExecption(e);
			}
			re = dataContext.executeUpdate(sqlInsert, param);
		}
		
		
		

		return re;

	}
	
	
	

	
	public <E extends M> int update(E entry) throws CommandExecuteExecption {
		
		int updatesize = updateFields.length;
		Object[] param = new Object[updatesize + 1];
		
        try {
		for (int j = 0; j < updatesize; j++) {
			Field field = updateFields[j];
			param[j] = field.get(entry);
			//param[j]=unsafeAdapter.getObject(entry, updateFieldOffsets[j]);
	    }
        }catch(Exception ex) {
        	//throw new ExecuteExecption(ex);
        }
		param[updatesize] = entry.thisPk();

			
		int re = dataContext.executeUpdate(sqlUpdate, param);
		
		return re;

	}
	
	
	public <E extends M> int updateBatch(Collection<E> entrys) throws CommandExecuteExecption {
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
					//params[i][j]=unsafeAdapter.getObject(obj, updateFieldOffsets[j]);
				}
				params[i][updatesize] = obj.thisPk();
                i++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//throw  CommandExecuteExecption.forSql(e, cmd, param)(e);
		}
		int re =  dataContext.executeBatch(sqlUpdate, params);
		
		return re;

	}
	
	
	public <E extends M> int deleteBatch(Collection<E> entrys) throws CommandExecuteExecption {
		int re = 0;
		if (entrys.size() == 0)
			return re;
		
		
		
		int dlength = entrys.size();
		Object[][] params = new Object[dlength][1];
        int i=0;
		for(M entry :entrys) {
			params[i][0] = entry.thisPk();
			i++;
		}

		re = dataContext.executeBatch(sqlDelete, params);
		
		return re;
	}

	public <E extends M> int delete(E entry) throws CommandExecuteExecption {
		
		
		
		Object[] param = new Object[] {entry.thisPk()};

		int re =  dataContext.executeUpdate(sqlDelete, param);
		
		return re;
	}
	
	
	
	
	 public int delete(String where,Serializable...params) throws CommandExecuteExecption{
		 StringBuffer sql = new StringBuffer("delete from ");
		 sql
		 .append(tableName);
		 if(where!=null) {
			 sql.append(" where ").append(where);
		 }
		 int ret = dataContext.executeUpdate(sql.toString(),params);
    	 return ret;
	}
	 
	
	 public int update(String set,String where,Serializable...params) throws CommandExecuteExecption{
		 
		 StringBuffer sql = new StringBuffer("update ");
		 sql
		 .append(tableName)
		 .append(" set ")
		 .append(set)
		 ;
		 
		 if(where!=null) {
			 sql.append(" where ").append(where);
		 }
		 int ret = dataContext.executeUpdate(sql.toString(),params);
    	 return ret;
	}
	 

	public int truncate() throws CommandExecuteExecption {
		String sql = "truncate table " + tableName;
		int re = dataContext.executeUpdate(sql);
		return re;
	}

	
	
    public  int remove(Serializable pk) throws CommandExecuteExecption {
		int re = 0;
		re = dataContext.executeUpdate(sqlDelete, pk);
		return re;
	}
	
    public <P extends Serializable> int removeBatch(Collection<P> pks) throws CommandExecuteExecption {
		int re = 0;
		if (pks.size() == 0)
			return re;
		
		int dlength = pks.size();
		Object[][] params = new Object[dlength][1];
        int i=0;
		for(P pk :pks) {
			params[i][0] = pk;
			i++;
		}

		re = dataContext.executeBatch(sqlDelete, params);
		
		return re;
	}
	
	
	
	
	
	
}
