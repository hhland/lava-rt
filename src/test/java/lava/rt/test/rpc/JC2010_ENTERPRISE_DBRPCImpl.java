package lava.rt.test.rpc;

import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.Collection;

import lava.rt.linq.Entity;
import lava.rt.linq.OutputParam;
import lava.rt.test.JC2010_ENTERPRISE_DB;
import lava.rt.test.JC2010_ENTERPRISE_DBRPC;

public class JC2010_ENTERPRISE_DBRPCImpl extends UnicastRemoteObject implements JC2010_ENTERPRISE_DBRPC{

	private final JC2010_ENTERPRISE_DB dc;
	
	public JC2010_ENTERPRISE_DBRPCImpl(JC2010_ENTERPRISE_DB dc) throws RemoteException {
		//super();
		// TODO Auto-generated constructor stub
		this.dc=dc;
	}
	

	

	@Override
	public Object[][] getColumns(Integer id, OutputParam<Float> name, OutputParam<String> age) throws SQLException {
		// TODO Auto-generated method stub
		return dc.getColumns(id, name, age);
	}




	@Override
	public int insert(Collection<? extends Entity> entrys) throws SQLException, RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public int insert(Entity entry) throws SQLException, RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public int update(Collection<? extends Entity> entrys) throws SQLException, RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public int update(Entity entry) throws SQLException, RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public int delete(Collection<? extends Entity> entrys) throws SQLException, RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public int delete(Entity entry) throws SQLException, RemoteException {
		// TODO Auto-generated method stub
		return dc.delete(entry);
	}

	
	
	
	
	

}
