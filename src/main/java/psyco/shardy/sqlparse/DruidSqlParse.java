package psyco.shardy.sqlparse;

import com.alibaba.druid.sql.ast.SQLStatement;

import java.util.List;

/**
 * Created by lipeng on 16/2/4.
 */
public class DruidSqlParse implements ISqlParser {
    SQLStatement sqlStatement;

    public DruidSqlParse(SQLStatement sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    @Override
    public String getTableName() {
        return SqlParser.getTableName(sqlStatement);
    }

    @Override
    public SqlType getType() {
        return null;
    }

    @Override
    public List<String> getWhereColums() {
        return null;
    }

    @Override
    public boolean setTableName(String tableName) {
        return false;
    }

    @Override
    public String toSql() {
        return null;
    }
}
