package lava.rt.sqlparser;

public class WhereSqlParser extends BaseSingleSqlParser {

	public WhereSqlParser(String originalSql) {
		super(originalSql);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeSegments() {
		// TODO Auto-generated method stub
		segments.add(new SqlSegment("(where|on|having)(.+)( group by | order by | ENDOFSQL)","(and|or)"));
	}

}
