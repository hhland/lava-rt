package lava.rt.linq.sql.src;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lava.rt.linq.sql.src.DataContextSrcGener.ColumnSrc;
import lava.rt.linq.sql.src.DataContextSrcGener.ProcedureParamSrc;
import lava.rt.linq.sql.src.DataContextSrcGener.ProcedureSrc;
import lava.rt.linq.sql.src.DataContextSrcGener.TableSrc;



public class SrcEvent {

	public void onTableSrcAppend(StringBuffer sbr,TableSrc tableSrc) {
		// TODO Auto-generated method stub
		
	}

	
	public void onViewSrcAppend(StringBuffer sbr,TableSrc tableSrc) {
		// TODO Auto-generated method stub
		
	}


	public void onProcedureImplSrcAppend(StringBuffer src, ProcedureSrc tableSrc) {
		// TODO Auto-generated method stub
		
	}

	public void onProcedureIntfSrcAppend(StringBuffer src, ProcedureSrc tableSrc) {
		// TODO Auto-generated method stub
		
	}

	public void onViewsLoaded(StringBuffer src, Set<String> views) {
		// TODO Auto-generated method stub
		
	}


	public void onTablesLoaded(StringBuffer src, Map<String, String> tablesPks) {
		// TODO Auto-generated method stub
		
	}


	public void onProceduresLoaded(StringBuffer src, Map<String, List<ProcedureParamSrc>> procs) {
		// TODO Auto-generated method stub
		
	}


	public void onColumnSrcAppend(StringBuffer src, ColumnSrc columnSrc) {
		// TODO Auto-generated method stub
		
	}
	
}
