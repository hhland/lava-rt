/*
 *@Database JC2010_ENTERPRISE_DB
 *@SrcGener lava.rt.linq.src.MSSQLServerDataContextSrcGener
 *@CreateAt Sat Jun 22 22:33:59 CST 2019
*/ 
package lava.rt.test.pojo; 

import lava.rt.linq.*; 
import java.util.*; 
import java.sql.*; 
import javax.sql.*; 


//onClassSrcOutter----start


public abstract class JC2010_ENTERPRISE_DBImpl extends lava.rt.linq.DataSourceContext implements lava.rt.test.pojo.JC2010_ENTERPRISE_DB{ 

//onClassSrcInner----start



//onClassSrcInner----end

	 public final Table<Test_> TEST_=createTable(Test_.class,"TEST_","ID");
	 public final Table<CompanyRel> COMPANY_REL=createTable(CompanyRel.class,"COMPANY_REL","COP_GB_CODE");






		public Object[][] getColumns(Integer id,OutputParam<Float> name,OutputParam<String> age) throws SQLException {


			 return callProcedure("getColumns",id,name,age);
		} 










} //end