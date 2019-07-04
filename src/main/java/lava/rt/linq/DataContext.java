package lava.rt.linq;

import java.sql.Connection;

import java.sql.Savepoint;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lava.rt.base.PoolList;

public interface  DataContext  {

	

	
	
	
	
	public <M extends Entity> List<M> executeQueryList(Class<M> cls, String cmd, Object... params) throws CommandExecuteExecption ;

	public Object[][] executeQueryArray(String cmd, Object... params) throws CommandExecuteExecption ;

	
	
	public String executeQueryJsonList(String cmd, Object... params) throws CommandExecuteExecption ;
	
	public String executeQueryJsonList(PagingParam pagingParam) throws CommandExecuteExecption ;

	
	public <E extends Entity> E get(Class<E> cls,Object pk) throws CommandExecuteExecption;

	public <E extends Entity> int addAll(Collection<E> entrys) throws CommandExecuteExecption ;

	public int add(Entity entry) throws CommandExecuteExecption ;

	public  int put(Object pk,Entity entry) throws Exception;

	public int update(Entity entry) throws CommandExecuteExecption;

	

	public <E extends Entity> int updateAll(Collection<E> entrys) throws CommandExecuteExecption ;

	

	public int remove(Entity entry) throws CommandExecuteExecption ;
	
	public <E extends Entity> int removeAll(Collection<E> entrys) throws CommandExecuteExecption;

	
	public void setAutoCommit(boolean b) throws CommandExecuteExecption ;
	
	public void commit() throws CommandExecuteExecption;
	
	public void rollback(Savepoint...savepoints) throws CommandExecuteExecption;
	
	public Savepoint[] setSavepoint(String...savepoints) throws CommandExecuteExecption;
	


	
}
