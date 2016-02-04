package psyco.test;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcUtils;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by lipeng on 16/2/4.
 */
public class SqlParser {

    public static List<SQLStatement> parse(String sql) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
        return parser.parseStatementList();
    }

    public static String getTableName(SQLStatement stmt) {
        SQLExpr re = getTableNameExpr(stmt);
        return re == null ? null : re.toString();
    }

    public static boolean setTableName(SQLStatement stmt, String tableName) {
        SQLExpr re = getTableNameExpr(stmt);
        if (re != null) {
            setName(re, tableName);
            return true;
        }
        return false;
    }

    private static SQLExpr getTableNameExpr(SQLStatement stmt) {
        if (stmt instanceof SQLSelectStatement) {
            SQLSelectStatement s = (SQLSelectStatement) stmt;
            SQLSelectQueryBlock query = (SQLSelectQueryBlock) s.getSelect().getQuery();
            SQLExprTableSource re = (SQLExprTableSource) query.getFrom();
            return re.getExpr();
        } else if (stmt instanceof SQLUpdateStatement) {
            return ((SQLUpdateStatement) stmt).getTableName();
        } else if (stmt instanceof SQLInsertStatement) {
            return ((SQLInsertStatement) stmt).getTableName();
        } else if (stmt instanceof SQLDeleteStatement) {
            return ((SQLDeleteStatement) stmt).getTableName();
        }
        return null;
    }

    private static void setName(SQLExpr SQLExpr, String name) {
        ((SQLIdentifierExpr) SQLExpr).setName(name);
    }

    /***
     * get where , except delete
     *
     * @param stmt
     * @return
     */
    public static SQLExpr getWhere(SQLStatement stmt) {
        if (stmt instanceof SQLSelectStatement) {
            SQLSelectStatement s = (SQLSelectStatement) stmt;
            SQLSelectQueryBlock query = (SQLSelectQueryBlock) s.getSelect().getQuery();
            return query.getWhere();
        } else if (stmt instanceof SQLUpdateStatement) {
            return ((SQLUpdateStatement) stmt).getWhere();
        } else if (stmt instanceof SQLDeleteStatement) {
            return ((SQLDeleteStatement) stmt).getWhere();
        }
        return null;
    }

    public static List<String> getColsFromWhere(SQLExpr where) {
        List<String> re = Lists.newLinkedList();
        flatSqlExpr(where, sqlExpr -> {
            if (sqlExpr instanceof SQLIdentifierExpr)
                re.add(sqlExpr.toString());
        });
        return re;
    }

    private static List<SQLExpr> flatSqlExprToList(SQLExpr sqlExpr) {
        List<SQLExpr> result = Lists.newLinkedList();
        flatSqlExpr(sqlExpr, sqlExpr1 -> result.add(sqlExpr1));
        return result;
    }

    private static void flatSqlExpr(SQLExpr sqlExpr, Consumer<SQLExpr> re) {
        if (sqlExpr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            flatSqlExpr(binaryOpExpr.getLeft(), re);
            flatSqlExpr(binaryOpExpr.getRight(), re);
        } else
            re.accept(sqlExpr);
    }


}
