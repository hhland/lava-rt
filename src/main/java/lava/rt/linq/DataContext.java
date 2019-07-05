package lava.rt.linq;


import java.util.Collection;
import java.util.List;

import lava.rt.linq.sql.PagingParam;

public interface  DataContext  {

	

	
	
	
	
	public <M extends Entity> List<M> executeQueryList(Class<M> cls, String cmd, Object... params) throws CommandExecuteExecption ;

	public Object[][] executeQueryArray(String cmd, Object... params) throws CommandExecuteExecption ;

	
	
	public String executeQueryJsonList(String cmd, Object... params) throws CommandExecuteExecption ;
	
	public String executeQueryJsonList(PagingParam pagingParam) throws CommandExecuteExecption ;

	
	public <E extends Entity> E entityGet(Class<E> cls,Object pk) throws CommandExecuteExecption;

	public <E extends Entity> int entityAddAll(Collection<E> entrys) throws CommandExecuteExecption ;

	public int entityAdd(Entity entry) throws CommandExecuteExecption ;

	public  int entityPut(Object pk,Entity entry) throws CommandExecuteExecption;

	public int entityUpdate(Entity entry) throws CommandExecuteExecption;

	

	public <E extends Entity> int entityUpdateAll(Collection<E> entrys) throws CommandExecuteExecption ;

	

	public int entityRemove(Entity entry) throws CommandExecuteExecption ;
	
	public <E extends Entity> int entityRemoveAll(Collection<E> entrys) throws CommandExecuteExecption;

	
	public void executeSetAutoCommit(boolean b) throws CommandExecuteExecption ;
	
	public void executeCommit() throws CommandExecuteExecption;
	
	public void executeRollback(Checkpoint...points) throws CommandExecuteExecption;
	
	public Checkpoint[] executeSetCheckpoint(String...points) throws CommandExecuteExecption;
	


	
}
