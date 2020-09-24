package lava.rt.linq;


import java.util.Collection;
import java.util.List;



public interface  DataContext  {

	

	
	
	
	
	public <M extends Entity> List<M> listEntities(Class<M> cls, String cmd, Object... params) throws CommandExecuteExecption ;

	public Object[][] executeQueryArray(String cmd, Object... params) throws CommandExecuteExecption ;

	
	
	public String executeQueryJsonList(String cmd, Object... params) throws CommandExecuteExecption ;
	
	
	
	public <E extends Entity> E getEntity(Class<E> cls,Object pk) throws CommandExecuteExecption;

	public <E extends Entity> int addEntities(Collection<E> entities) throws CommandExecuteExecption ;

	public int addEntity(Entity entity) throws CommandExecuteExecption ;
	
	public  int putEntity(Entity entity) throws CommandExecuteExecption,DuplicateKeyException;

	public int updateEntity(Entity entity) throws CommandExecuteExecption;

	

	public <E extends Entity> int updateEntities(Collection<E> entities) throws CommandExecuteExecption ;

	

	public int removeEntity(Entity entity) throws CommandExecuteExecption ;
	
	public <E extends Entity> int removeEntities(Collection<E> entities) throws CommandExecuteExecption;

	
	public void executeSetAutoCommit(boolean b) throws CommandExecuteExecption ;
	
	public void executeCommit() throws CommandExecuteExecption;
	
	public void executeRollback(Checkpoint...points) throws CommandExecuteExecption;
	
	public Checkpoint[] executeSetCheckpoint(String...points) throws CommandExecuteExecption;
	


	
}
