/*
 *@Database JC2010_ENTERPRISE_DB
 *@SrcGener lava.rt.linq.src.MSSQLServerDataContextSrcGener
 *@CreateAt Sat Jun 22 22:33:59 CST 2019
*/ 
package lava.rt.test.pojo; 

import lava.rt.linq.*;
import lava.rt.linq.execption.CommandExecuteExecption;
import lava.rt.linq.sql.OutputParam;
import lava.rt.linq.sql.Table;

import java.util.*; 
import java.sql.*; 
import javax.sql.*; 


//onClassSrcOutter----start


public abstract class JC2010_ENTERPRISE_DBBase extends lava.rt.linq.sql.DataSourceContext implements lava.rt.test.pojo.JC2010_ENTERPRISE_DB{ 

//onClassSrcInner----start



//onClassSrcInner----end

	 public final Table<Test_> TEST_=tableCreate(Test_.class,"TEST_","ID");
	 public final Table<CompanyRel> COMPANY_REL=tableCreate(CompanyRel.class,"COMPANY_REL","COP_GB_CODE");






		public Object[][] getColumns(Integer id,OutputParam<Float> name,OutputParam<String> age) throws  CommandExecuteExecption {


			 return callProcedure("getColumns",id,name,age);
		} 










} //end