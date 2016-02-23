package maniac.lee.shardy.test;

import org.junit.Test;
import maniac.lee.shardy.SqlParseException;
import maniac.lee.shardy.sqlparser.DruidSqlParser;
import maniac.lee.shardy.sqlparser.ISqlParser;

/**
 * Created by lipeng on 16/2/16.
 */
public class ParserTest {

    private ISqlParser iSqlParser = new DruidSqlParser();

    @Test
    public void test() throws SqlParseException {
        String sql = "select * from Table1 where a > 3 and b < 5 or 3 = ?";
        iSqlParser.init(sql);
        System.out.println("tableName: " + iSqlParser.getTableName());
        iSqlParser.setTableName("fuck");
        System.out.println("sqlresult-> " + iSqlParser.toSql());
    }
}
