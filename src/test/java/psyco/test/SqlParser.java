package psyco.test;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;

import java.util.List;

/**
 * Created by lipeng on 16/2/4.
 */
public class SqlParser {

    public static List<SQLStatement> parse(String sql) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
        return parser.parseStatementList();
    }



    public static String getTableName(SQLTableSource sqlTableSource) {
        if (sqlTableSource instanceof SQLExprTableSource)
            return ((SQLExprTableSource) sqlTableSource).getExpr().toString();
        return null;
    }

    public static boolean setTableName(SQLTableSource sqlTableSource, String tableName) {
        if (sqlTableSource instanceof SQLExprTableSource) {
            SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) sqlTableSource;
            SQLIdentifierExpr expr = (SQLIdentifierExpr) sqlExprTableSource.getExpr();
            expr.setName(tableName);
            return true;
        }
        return false;
    }
}
