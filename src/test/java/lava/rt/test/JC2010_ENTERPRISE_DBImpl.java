package lava.rt.test; 

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
import java.util.List; 
import java.util.ArrayList; 
import java.sql.SQLException; 
import javax.sql.DataSource; 


import java.io.Serializable; 


/*
 *@Database JC2010_ENTERPRISE_DB
 *@SrcGener lava.rt.linq.src.MSSQLServerDataContextSrcGener
 *@CreateAt Fri Jun 14 13:34:51 CST 2019
*/ 
public class JC2010_ENTERPRISE_DBImpl extends lava.rt.linq.DataContext implements IJC2010_ENTERPRISE_DB{ 

	private static final long serialVersionUID=1L;

	@Override
	protected Class thisClass() {return this.getClass(); }

	 public JC2010_ENTERPRISE_DBImpl(DataSource... dataSources)throws Exception{ super(dataSources);  } 

	 public final Table<Test_> TEST_=createTable(Test_.class,"TEST_","ID");
	 public final Table<CompanyRel> COMPANY_REL=createTable(CompanyRel.class,"COMPANY_REL","COP_GB_CODE");

	@Override
	public Object[][] getColumns(Integer id, OutputParam<Float> name, OutputParam<String> age) throws SQLException {
		// TODO Auto-generated method stub
		return callProcedure("getColumns", id,name,age);
	}




	 




} //end