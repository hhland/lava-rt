package lava.rt.test.linq;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import lava.rt.linq.src.DataContextSrcGener;
import lava.rt.linq.src.MSSQLServerDataContextSrcGener;
import lava.rt.test.pojo.JC2010_ENTERPRISE_DBBase;
import lava.rt.test.pojo.JC2010_ENTERPRISE_DB;
import lava.rt.test.pojo.JC2010_ENTERPRISE_DBImpl;

import net.sourceforge.jtds.jdbcx.JtdsDataSource;

public class DataContextSrcGenerTestCase extends TestCase {

	
	JtdsDataSource jds;
	DataContextSrcGener gener;
	String dir="I:/git/lava-rt/src/test/java/"
			,db="JC2010_ENTERPRISE_DB"
			;
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		  jds=new JtdsDataSource();
		   jds.setServerName("jc.db");
		   jds.setDatabaseName(db);
		   jds.setUser("sa");
		   jds.setPassword("nfha_505");
		   Connection conn=jds.getConnection();
		   //conn.getSchema();
		   gener=new MSSQLServerDataContextSrcGener(conn);
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

//	@Test
//	public void testSaveLocalSrcTo() throws SQLException, IOException {
//		File srcFile=new File(dir+gener.refSrcPath(JC2010_ENTERPRISE_DBLocal.class));
//		gener.saveLocalSrcTo(srcFile, JC2010_ENTERPRISE_DBLocal.class, db);
//	}

	@Test
	public void testSaveIntfSrcTo() throws SQLException, IOException {
		File srcFile=new File(dir+gener.refSrcPath(JC2010_ENTERPRISE_DB.class));
		gener.saveIntfSrcTo(srcFile, JC2010_ENTERPRISE_DB.class, db);
	}
	
	@Test
	public void testSaveImplSrcTo() throws SQLException, IOException {
		File srcFile=new File(dir+gener.refSrcPath(JC2010_ENTERPRISE_DBBase.class));
		gener.saveImplSrcTo(JC2010_ENTERPRISE_DB.class, srcFile,JC2010_ENTERPRISE_DBBase.class, db);
	}

	@Test
	public void testToLocalSrc() {
		 
	}

	@Test
	public void testToRpcIntfSrc() {
		 
	}

	@Test
	public void testToRpcImplSrc() {
		 
	}

	@Test
	public void testLoadViews() {
		 
	}

	@Test
	public void testLoadTablesPks() {
		 
	}

}
