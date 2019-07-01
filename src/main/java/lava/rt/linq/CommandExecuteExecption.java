package lava.rt.linq;

import java.sql.SQLException;

import lava.rt.common.SqlCommon;

public class CommandExecuteExecption  extends Exception{

	
	public enum CmdType{
		sql,procedure,bat,sh;
	}
	
	final String cmd;
	
	final Object[] param;

	final CmdType cmdType;
	
	protected CommandExecuteExecption(Exception e, CmdType cmdType,String cmd, Object... param) {
		super(e);
		this.cmdType=cmdType;
		this.cmd = cmd;
		this.param = param;
	}

	
	public static CommandExecuteExecption forSql(Exception e,String cmd, Object... param) {
		CommandExecuteExecption ret=new CommandExecuteExecption(e,CmdType.sql, cmd, param);
		return ret;
	}
	
	
}
