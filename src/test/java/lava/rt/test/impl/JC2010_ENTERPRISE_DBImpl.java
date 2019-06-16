package lava.rt.test.impl; 

import java.lang.String; 
import java.lang.Integer; 
import java.lang.Float; 
import java.lang.Double; 
import java.sql.Date; 
import java.sql.Timestamp; 
import java.math.BigDecimal; 
import java.lang.Byte; 
import java.lang.Boolean; 
import java.lang.Byte; 
import lava.rt.linq.*; 
import java.util.*; 
import java.sql.SQLException; 
import javax.sql.DataSource; 


import java.io.Serializable; 


/*
 *@Database JC2010_ENTERPRISE_DB
 *@SrcGener lava.rt.linq.src.MSSQLServerDataContextSrcGener
 *@CreateAt Sun Jun 16 14:54:14 CST 2019
*/ 
public class JC2010_ENTERPRISE_DBImpl extends lava.rt.linq.DataSourceContext implements lava.rt.test.IJC2010_ENTERPRISE_DB{ 

	@Override
	protected Class thisClass() {return this.getClass(); }

	 public final Table<Test_> TEST_=createTable(Test_.class,"TEST_","ID");
	 public final Table<CompanyRel> COMPANY_REL=createTable(CompanyRel.class,"COMPANY_REL","COP_GB_CODE");






		public Object[][] getColumns(Integer id,OutputParam<Float> name,OutputParam<String> age) throws SQLException {


			 return callProcedure("getColumns",id,name,age);
		} 










} //end