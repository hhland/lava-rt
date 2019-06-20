
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


import lava.rt.linq.Criterias;
import lava.rt.linq.OutputParam;
import lava.rt.linq.Table;
import lava.rt.linq.src.DataContextSrcGener;
import lava.rt.linq.src.MSSQLServerDataContextSrcGener;
import lava.rt.test.IJC2010_ENTERPRISE_DB.Test_;
import lava.rt.test.impl.JC2010_ENTERPRISE_DBImpl;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;

public class DataContextMain {

	
	  public static void main(String[] args) throws Exception {
		  
		   JtdsDataSource jds=new JtdsDataSource();
		   jds.setServerName("jc.db");
		   jds.setDatabaseName("JC2010_ENTERPRISE_DB");
		   jds.setUser("sa");
		   jds.setPassword("nfha_505");
		   
		   JtdsDataSource jds2=new JtdsDataSource();
		   jds2.setServerName("jc.db");
		   jds2.setDatabaseName("JC_SH");
		   jds2.setUser("sa");
		   jds2.setPassword("nfha_505");
		   
		   DataSource ds=jds;
		   
		   Connection conn=jds.getConnection();
		   //conn.getSchema();
		   DataContextSrcGener gener=new MSSQLServerDataContextSrcGener(conn);
		   
		  // String src= gener.toSrc(JC2010_ENTERPRISE_DB.class, "JC2010_ENTERPRISE_DB");
		   String dir="I:/git/lava-rt/src/test/java/lava/rt/test/";
		   File srcFile=new File(dir+"/JC2010_ENTERPRISE_DB.java")
				   ,intfFile=new File(dir+"IJC2010_ENTERPRISE_DB.java")
						   ,implFile=new File(dir+"JC2010_ENTERPRISE_DBimpl.java")
				   ;
		  // gener.saveLocalSrcTo(srcFile, JC2010_ENTERPRISE_DB.class, "JC2010_ENTERPRISE_DB");
		   gener.saveRpcIntfSrcTo(intfFile, IJC2010_ENTERPRISE_DB.class, "JC");
		   gener.saveRpcImplSrcTo(implFile, IJC2010_ENTERPRISE_DB.class,JC2010_ENTERPRISE_DBImpl.class, "JC");
		   //gener.toSrc(JC2010_ENTERPRISE_DB.class, "JC2010_ENTERPRISE_DB", "TEST_");
		   //System.out.println(src);
		  JC2010_ENTERPRISE_DB db=new JC2010_ENTERPRISE_DB();
		  // db.setDataSource(jds,jds2);
		   
		   JC2010_ENTERPRISE_DBImpl db0=new JC2010_ENTERPRISE_DBImpl();
		  // db0.setDataSource(jds,jds2);
		   
		   IJC2010_ENTERPRISE_DB idb=db0;
		   
		   
//		   JC2010_ENTERPRISE_DB.Criteria cr= db.CRITERIA;
//		   OutputParam<Float> op0=new OutputParam(90f);
//		   OutputParam<String> op1=new OutputParam<>(String.class);
//		   //Object[][] objs=db.getColumns(4,op0, op1);
//		 
//		   //System.out.println(objs[0][2]);
//		   
//		   
//		  
//		   
//		 
//		  
//		   //assertEquals(90, op0.result,0);
//		  // assertEquals("10", op1.result);
//		   
////		   List<CompanyRel> companyRels=db.COMPANY_REL.select("");
////		   System.out.println(companyRels.size());
////		   for(CompanyRel companyRel:companyRels) {
////			   System.out.println(companyRel);
////		   }
//		   
		   db.TEST_.truncate();
		   
		   List<Test_> tests=new ArrayList<>();
		   for(int i=0;i<100;i++) {
			   Test_ test= new Test_(); //db.newEntry(Test_.class);
			   test.setVarchar_(""+i);
			   test.setId(i);
			   tests.add(test);
		   }
//			   //db.insert(test);
//		   }
//		   db.setAutoCommit(false);
//		   System.out.println("insert:"+System.currentTimeMillis());
//		   //db.TEST_.insert(tests);
//		   db.insert(tests);
			   idb.insert(tests);
//		   
//		   Random random=new Random();
//		   for(Test_ test:tests) {
//			   test.setFloat_(random.nextDouble());
//		   }
//		   System.out.println("update:"+System.currentTimeMillis());
//		   //db.TEST_.update(tests);
//		   db.update(tests);
//		   System.out.println("delete:"+System.currentTimeMillis());
//		   List<Test_> suList=tests.subList(0, 5);
//		   //db.TEST_.delete(suList);
//		   db.delete(suList);
//		   
//		   db.commit();
//		   
//		   
//		   
//		   //db.insert(tests);
//		   
//		   //List<JcBizJcJobActualAdditionalTax> list= 
//		//		   db.getTable(JcBizJcJobActualAdditionalTax.class).select("");
//		   
//		  // assertTrue(list.size()>0);
	  }
}
