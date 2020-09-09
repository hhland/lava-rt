package lava.rt.linq.sql;

import java.sql.ResultSetMetaData;

public interface ResultHandler<R> {

	int handleRow(int rowIndex, R row);

}

