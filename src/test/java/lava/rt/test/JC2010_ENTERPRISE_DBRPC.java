package lava.rt.test;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.Collection;

import lava.rt.linq.Entity;
import lava.rt.linq.OutputParam;
import lava.rt.test.JC2010_ENTERPRISE_DB.Criteria;

public interface JC2010_ENTERPRISE_DBRPC extends Remote {

	public static final JC2010_ENTERPRISE_DB INSTANCE=new JC2010_ENTERPRISE_DB();
	
	public static JC2010_ENTERPRISE_DBRPC lookup(Registry registry) throws AccessException, RemoteException, NotBoundException {
		JC2010_ENTERPRISE_DBRPC ret=(JC2010_ENTERPRISE_DBRPC)registry.lookup(JC2010_ENTERPRISE_DBRPC.class.getName());
	    return ret;
	}
	
	public static void bind(Registry registry,JC2010_ENTERPRISE_DBRPC rpc) throws AccessException, RemoteException, AlreadyBoundException  {
		registry.rebind(JC2010_ENTERPRISE_DBRPC.class.getName(), rpc);
	}
	
	
	public static <E extends Serializable> E newEntity(Class<E> cls) throws Exception{
		E ret=(E)cls.getConstructors()[0].newInstance();
		return ret;
	}
	
	public  int insert(Collection<? extends Entity> entrys) throws SQLException,RemoteException ;
	public  int insert(Entity entry) throws SQLException,RemoteException; 
	
	public  int update(Collection<? extends Entity> entrys) throws SQLException,RemoteException ;
	public  int update(Entity entry) throws SQLException,RemoteException;
	
	public  int delete(Collection<? extends Entity> entrys) throws SQLException,RemoteException;
	public  int delete(Entity entry) throws SQLException,RemoteException;
	
	public final static Criteria CRITERIA=JC2010_ENTERPRISE_DB.CRITERIA;
	
	public class CompanyRel  extends JC2010_ENTERPRISE_DB.CompanyRel implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3790707478503717073L;

		
		public CompanyRel() {
			INSTANCE.super();
			
		}	
	}
	
	
	
	public Object[][] getColumns(Integer id,OutputParam<Float> name,OutputParam<String> age) throws SQLException,RemoteException;
	
	
}
