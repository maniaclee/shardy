package psyco.shardy.sqlparse;

import java.util.List;

/**
 * Created by lipeng on 16/2/4.
 */
public interface ISqlParser {

    String getTableName();

    SqlType getType();

    List<String> getWhereColums();

    boolean setTableName(String tableName);

    String toSql();

}
