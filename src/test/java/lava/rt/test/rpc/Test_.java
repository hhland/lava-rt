package lava.rt.test.rpc;

import java.io.Serializable;

import lava.rt.test.JC2010_ENTERPRISE_DB;
import lava.rt.test.JC2010_ENTERPRISE_DBRPC;

public class Test_  extends JC2010_ENTERPRISE_DB.Test_ implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3790707478503717073L;

	
	public Test_() {
	       
		JC2010_ENTERPRISE_DBRPC.INSTANCE.super();
	}	
}
