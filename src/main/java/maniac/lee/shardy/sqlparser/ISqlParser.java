package maniac.lee.shardy.sqlparser;

import maniac.lee.shardy.SqlParseException;

import java.util.List;

/**
 * Created by lipeng on 16/2/4.
 */
public interface ISqlParser {

    void init(String sql) throws SqlParseException;

    String getTableName();

    SqlType getType();

    List<String> getWhereColumns();

    boolean setTableName(String tableName);

    List<ColumnValue> getColumns();

    String toSql();

    String getSqlOriginal();
}
