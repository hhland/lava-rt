package lava.rt.execption;



public class CommandExecuteExecption  extends Exception{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;





	public enum CmdType{
		unknow,sql,procedure,bat,sh, reflect;
	}
	
	final String cmd;
	
	final Object[] param;

	final CmdType cmdType;
	
	
	
	public CommandExecuteExecption(Exception e, CmdType cmdType,String cmd, Object... param) {
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
