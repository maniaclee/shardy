package psyco.test;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class ParseTestMy {

    public ParseTestMy() {
        // TODO Auto-generated constructor stub
    }


    public static void main(String[] args) {

        String sql = "select * from TableA a where id < ? and a = 3";

        StringBuffer select = new StringBuffer();
        StringBuffer from = new StringBuffer();
        StringBuffer where = new StringBuffer();

        // parser得到AST
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, JdbcUtils.MYSQL);
        List<SQLStatement> stmtList = parser.parseStatementList(); //

        // 将AST通过visitor输出
        SQLASTOutputVisitor visitor = SQLUtils.createFormatOutputVisitor(from, stmtList, JdbcUtils.MYSQL);
        SQLASTOutputVisitor whereVisitor = SQLUtils.createFormatOutputVisitor(where, stmtList, JdbcUtils.MYSQL);

        List<SQLSelectItem> items = null;

        for (SQLStatement stmt : stmtList) {
            // stmt.accept(visitor);
            if (stmt instanceof SQLSelectStatement) {
                SQLSelectStatement sstmt = (SQLSelectStatement) stmt;
                SQLSelect sqlselect = sstmt.getSelect();
                SQLSelectQueryBlock query = (SQLSelectQueryBlock) sqlselect.getQuery();

                System.out.println(ToStringBuilder.reflectionToString(query.getFrom()));
                SQLExprTableSource f = (SQLExprTableSource) query.getFrom();
                setTableName(f,"shit");
                System.out.println("tableName : " + getTableName(query.getFrom()));

                query.getFrom().accept(visitor);
                query.getWhere().accept(whereVisitor);
                items = query.getSelectList();
                System.out.println("select->" + sstmt);
            }
        }
        for (SQLSelectItem s : items) {
            System.out.println(s.getAlias());
        }

        System.out.println("--------------------------------");

        System.out.println("from==" + from.toString());
        System.out.println("select==" + select);
        System.out.println("where==" + where);
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