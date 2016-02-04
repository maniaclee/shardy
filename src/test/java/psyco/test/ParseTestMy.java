package psyco.test;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class ParseTestMy {

    public ParseTestMy() {
        // TODO Auto-generated constructor stub
    }


    public static void main(String[] args) {

        //        String sql = "select * from TableA a where id < ? and a = 3";
        String sql = "update TableA set col = ?   where id < ? and a = 3";

        StringBuffer select = new StringBuffer();
        StringBuffer from = new StringBuffer();
        StringBuffer where = new StringBuffer();

        // parser得到AST
        List<SQLStatement> stmtList = SqlParser.parse(sql); //

        // 将AST通过visitor输出
        SQLASTOutputVisitor visitor = SQLUtils.createFormatOutputVisitor(from, stmtList, JdbcUtils.MYSQL);
        SQLASTOutputVisitor whereVisitor = SQLUtils.createFormatOutputVisitor(where, stmtList, JdbcUtils.MYSQL);

        List<SQLSelectItem> items = null;

        SQLStatement sqlStatement  =  stmtList.get(0);
        for (SQLStatement stmt : stmtList) {
            // stmt.accept(visitor);
            if (stmt instanceof SQLSelectStatement) {
                SQLSelectStatement sstmt = (SQLSelectStatement) stmt;
                SQLSelect sqlselect = sstmt.getSelect();
                SQLSelectQueryBlock query = (SQLSelectQueryBlock) sqlselect.getQuery();

                System.out.println(ToStringBuilder.reflectionToString(query.getFrom()));
                SQLExprTableSource f = (SQLExprTableSource) query.getFrom();
                SqlParser.setTableName(stmt, "shit");
                System.out.println("tableName : " + SqlParser.getTableName(stmt));

                query.getFrom().accept(visitor);
                query.getWhere().accept(whereVisitor);
                items = query.getSelectList();
                System.out.println("select->" + sstmt);
                for (SQLSelectItem s : items) {
                    System.out.println(s.getAlias());
                }

                System.out.println("--------------------------------");

                System.out.println("from==" + from.toString());
                System.out.println("select==" + select);
                System.out.println("where==" + where);
            } else if (stmt instanceof SQLUpdateStatement) {
                SQLUpdateStatement updateStatement = (SQLUpdateStatement) stmt;
                System.out.println(SqlParser.getTableName(stmt));
            } else if (stmt instanceof SQLInsertStatement) {
                SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) stmt;
                System.out.println(((SQLInsertStatement) stmt).getTableName());
            }
        }
        SQLExpr where1 = SqlParser.getWhere(sqlStatement);
        System.out.println(where1.getClass());

    }





}