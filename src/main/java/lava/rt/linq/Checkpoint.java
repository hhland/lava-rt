package lava.rt.linq;

import java.io.CharArrayWriter;
import java.sql.SQLException;
import java.sql.Savepoint;

public abstract class Checkpoint {

	
	public abstract int getPointId() throws Exception;



	public abstract String getPointName() throws Exception;



	public static Checkpoint forSql(Savepoint savepoint) {
		Checkpoint ret=new Checkpoint() {
			Savepoint _savepoint=savepoint;
			
			public int getPointId()  throws Exception {
				// TODO Auto-generated method stub
				return _savepoint.getSavepointId();
			}

		
			public String getPointName() throws Exception{
				// TODO Auto-generated method stub
				return _savepoint.getSavepointName();
			}
			
		};
		
		return ret;
	}
	

}
