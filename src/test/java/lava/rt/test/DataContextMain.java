
package lava.rt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;


import lava.rt.linq.Criteria;
import lava.rt.linq.OutputParam;
import lava.rt.linq.Table;
import lava.rt.linq.src.DataContextSrcGener;
import lava.rt.linq.src.MSSQLServerDataContextSrcGener;
import lava.rt.test.JC2010_ENTERPRISE_DB.CompanyRel;

import lava.rt.test.JC2010_ENTERPRISE_DB.Test_;
import lava.rt.test.rpc.JC2010_ENTERPRISE_DBRPCImpl;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;

public class DataContextMain {

	
	  public static void main(String[] args) throws Exception {
		  
		   JtdsDataSource jds=new JtdsDataSource();
		   jds.setServerName("jc.db");
		   jds.setDatabaseName("JC2010_ENTERPRISE_DB");
		   jds.setUser("sa");
		   jds.setPassword("nfha_505");
		   DataSource ds=jds;
		   Connection conn=ds.getConnection();
		   //conn.getSchema();
		   DataContextSrcGener gener=new MSSQLServerDataContextSrcGener(conn);
		   
		  // String src= gener.toSrc(JC2010_ENTERPRISE_DB.class, "JC2010_ENTERPRISE_DB");
		   File srcFile=new File("G:\\git\\lava-rt\\src\\test\\java\\lava\\rt\\test\\JC2010_ENTERPRISE_DB.java");
		   //gener.toFile(srcFile, JC2010_ENTERPRISE_DB.class, "JC2010_ENTERPRISE_DB");
		   //gener.toSrc(JC2010_ENTERPRISE_DB.class, "JC2010_ENTERPRISE_DB", "TEST_");
		   //System.out.println(src);
		   JC2010_ENTERPRISE_DB db=new JC2010_ENTERPRISE_DB(ds);
		   JC2010_ENTERPRISE_DB.Criteria cr= db.CRITERIA;
		   OutputParam<Float> op0=new OutputParam(90f);
		   OutputParam<String> op1=new OutputParam<>(String.class);
		   Object[][] objs=db.getColumns(4,op0, op1);
		 
		   System.out.println(objs[0][2]);
		   
		   
		   int port=8070;
		   Registry registry=LocateRegistry.createRegistry(port);
		   JC2010_ENTERPRISE_DBRPCImpl dbrpcImpl=new JC2010_ENTERPRISE_DBRPCImpl(db);
		   JC2010_ENTERPRISE_DBRPC.bind(registry, dbrpcImpl);
		   
		   Registry registry2=LocateRegistry.getRegistry(port);
		   
		   JC2010_ENTERPRISE_DBRPC dbrpc=JC2010_ENTERPRISE_DBRPC.lookup(registry2);
		  
		   assertEquals(90, op0.result,0);
		  // assertEquals("10", op1.result);
		   
		   List<CompanyRel> companyRels=db.COMPANY_REL.select("");
		   System.out.println(companyRels.size());
		   for(CompanyRel companyRel:companyRels) {
			   System.out.println(companyRel);
		   }
		   
		   db.TEST_.truncate();
		   List<Test_> tests=new ArrayList<>();
		   for(int i=0;i<4000;i++) {
			   Test_ test=db.newEntry(Test_.class);
			   test.setVarchar_(""+i);
			   
			   tests.add(test);
			   //db.insert(test);
		   }
		  // db.TEST_.insertWithoutPk(tests.toArray(new Test[tests.size()]));
		   db.insert(tests);
		   Random random=new Random();
		   for(Test_ test:tests) {
			   test.setFloat_(random.nextDouble());
		   }
		   //db.TEST_.update(tests.toArray(new Test[tests.size()]));
		   //db.update(tests);
		   
		   //db.delete();
		   
		   
		   lava.rt.test.rpc.Test_ test_=new lava.rt.test.rpc.Test_();
		   test_.setId(4);
		   dbrpc.delete(test_);
		   
		   //db.insert(tests);
		   
		   //List<JcBizJcJobActualAdditionalTax> list= 
		//		   db.getTable(JcBizJcJobActualAdditionalTax.class).select("");
		   
		  // assertTrue(list.size()>0);
	  }
}
