package psyco.shardy.sqlparser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import psyco.shardy.SqlParseException;

import java.util.List;

/**
 * Created by lipeng on 16/2/4.
 */
public class DruidSqlParser implements ISqlParser {
    SQLStatement sqlStatement;

    @Override
    public void init(String sql) throws SqlParseException {
        List<SQLStatement> statements = DruidUtils.parse(sql);
        if (statements == null || statements.isEmpty())
            throw new SqlParseException("empty sql is not allowed");
        this.sqlStatement = statements.get(0);
    }

    @Override
    public String getTableName() {
        return DruidUtils.getTableName(sqlStatement);
    }

    @Override
    public SqlType getType() {
        if (sqlStatement instanceof SQLSelectStatement)
            return SqlType.SELECT;
        if (sqlStatement instanceof SQLUpdateStatement) return SqlType.UPDATE;

        if (sqlStatement instanceof SQLInsertStatement)

            return SqlType.INSERT;
        if (sqlStatement instanceof SQLDeleteStatement)
            return SqlType.DELETE;
        return null;
    }

    @Override
    public List<String> getWhereColumns() {
        return DruidUtils.getColNamesFromWhere(DruidUtils.getWhere(sqlStatement));
    }

    @Override
    public boolean setTableName(String tableName) {
        return DruidUtils.setTableName(sqlStatement, tableName);
    }

    @Override
    public List<ColumnValue> getcolumns() {
        return DruidUtils.getColumns(sqlStatement);
    }

    @Override
    public String toSql() {
        if (sqlStatement instanceof MySqlUpdateStatement) {
            StringBuffer stringBuffer = new StringBuffer();
            sqlStatement.output(stringBuffer);
            return stringBuffer.toString();
        }
        return sqlStatement.toString();
    }

}
