package lava.rt.test.linq;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import lava.rt.linq.CommandExecuteExecption;
import lava.rt.linq.Criterias;
import lava.rt.linq.DataContext;
import lava.rt.linq.Entity;
import lava.rt.linq.src.DataContextSrcGener;
import lava.rt.linq.src.MSSQLServerDataContextSrcGener;
import lava.rt.test.pojo.JC2010_ENTERPRISE_DB.Criteria;
import lava.rt.test.pojo.JC2010_ENTERPRISE_DB.Test_;
import lava.rt.test.pojo.JC2010_ENTERPRISE_DBImpl;

import net.sourceforge.jtds.jdbcx.JtdsDataSource;

public class DataContextTestCase extends TestCase {

	
	JtdsDataSource jds;
	DataContext dc;
	String dir="I:/git/lava-rt/src/test/java/"
			,db="JC2010_ENTERPRISE_DB"
			;
	Criteria cr;
	
	
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		  jds=new JtdsDataSource();
		   jds.setServerName("jc.db");
		   jds.setDatabaseName(db);
		   jds.setUser("sa");
		   jds.setPassword("nfha_505");
		   
		   
		   JC2010_ENTERPRISE_DBImpl db=new JC2010_ENTERPRISE_DBImpl(jds);
		   cr=db.CRITERIA;
		   dc=db;
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		
	}

	@Test
	public void testLoad() throws SQLException, CommandExecuteExecption {
		
		 Test_ record=dc.get(Test_.class, 1);
		 assertNotNull(record);
	}

	@Test
	public void testExecuteQueryList() throws SQLException, CommandExecuteExecption {
		
		 List<Test_> records=dc.executeQueryList(Test_.class, "select * from "+Criterias.tableName(Test_.class) );
		 assertTrue(records.size()>0);
	}

	@Test
	public void testExecuteQueryArray() {
		 
	}

	@Test
	public void testExecuteQueryJsonArrayStringObjectArray() {
		 
	}

	@Test
	public void testExecuteQueryJsonListStringObjectArray() {
		 
	}

	@Test
	public void testExecuteQueryJsonArrayPagingParam() {
		 
	}

	@Test
	public void testExecuteQueryJsonListPagingParam() {
		 
	}

	@Test
	public void testExecuteUpdate() {
		 
	}

	@Test
	public void testExecuteInsertReturnPk() {
		 
	}

	@Test
	public void testExecuteBatch() {
		 
	}

	@Test
	public void testInsertCollectionOfQextendsEntity() {
		 
	}

	@Test
	public void testInsertEntityPoolListOfConnection() {
		 
	}

	@Test
	public void testInsertEntity() {
		 
	}

	@Test
	public void testUpdateEntity() {
		 
	}

	@Test
	public void testUpdateEntityPoolListOfConnection() {
		 
	}

	@Test
	public void testUpdateCollectionOfQextendsEntity() {
		 
	}

	@Test
	public void testDeleteEntityPoolListOfConnection() {
		 
	}

	@Test
	public void testDeleteEntity() {
		 
	}

	@Test
	public void testDeleteCollectionOfQextendsEntity() {
		 
	}

}
