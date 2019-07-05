package lava.rt.test.linq;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import lava.rt.linq.DataContext;
import lava.rt.linq.sql.Table;
import lava.rt.test.pojo.JC2010_ENTERPRISE_DBBase;
import lava.rt.test.pojo.JC2010_ENTERPRISE_DBImpl;
import lava.rt.test.pojo.JC2010_ENTERPRISE_DB.Criteria;
import lava.rt.test.pojo.JC2010_ENTERPRISE_DB.Test_;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;

public class TableTestCase extends TestCase {

	JtdsDataSource jds;
	
	String dir="I:/git/lava-rt/src/test/java/"
			,db="JC2010_ENTERPRISE_DB"
			;
	Criteria cr;
	
	Table<Test_> table;
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		  jds=new JtdsDataSource();
		   jds.setServerName("jc.db");
		   jds.setDatabaseName(db);
		   jds.setUser("sa");
		   jds.setPassword("nfha_505");
		   
		   
		   JC2010_ENTERPRISE_DBBase db=new JC2010_ENTERPRISE_DBImpl(jds);
		   cr=db.CRITERIA;
		  
		   table=db.TEST_;
	}
	
	@Test
	public void testLoad() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertCollectionOfE() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertE() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertWithoutPk() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateE() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateCollectionOfE() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteCollectionOfE() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteE() {
		fail("Not yet implemented");
	}

	@Test
	public void testTruncate() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPk() {
		fail("Not yet implemented");
	}

	@Test
	public void testDuplicateString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectInto() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertInto() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectStringObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSelectCriteriasIntIntStringObjectArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testCount() {
		fail("Not yet implemented");
	}

	@Test
	public void testSum() {
		fail("Not yet implemented");
	}

	@Test
	public void testMin() {
		fail("Not yet implemented");
	}

	@Test
	public void testMax() {
		fail("Not yet implemented");
	}

	@Test
	public void testDuplicateString1() {
		fail("Not yet implemented");
	}

}
