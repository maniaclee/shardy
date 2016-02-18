package psyco.shardy.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import psyco.shardy.SqlParseException;
import psyco.shardy.config.ShardConfig;
import psyco.shardy.config.ShardContext;
import psyco.shardy.config.ShardResult;
import psyco.shardy.config.TableConfig;
import psyco.shardy.datasource.DynamicDataSource;
import psyco.shardy.sqlparser.ColumnValue;
import psyco.shardy.sqlparser.DruidSqlParser;
import psyco.shardy.sqlparser.ISqlParser;
import psyco.shardy.sqlparser.SqlType;
import psyco.shardy.util.ReflectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * Created by lipeng on 16/2/18.
 */
public class ExtendedSqlSource implements SqlSource {
    SqlSource sqlSource;

    public static ExtendedSqlSource instance(MappedStatement mappedStatement) {
        ExtendedSqlSource extendedSqlSource = new ExtendedSqlSource();
        extendedSqlSource.sqlSource = mappedStatement.getSqlSource();
        return extendedSqlSource;
    }


    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        changeSql(boundSql);
        return boundSql;
    }

    private void changeSql(BoundSql boundSql) {
        String sql = boundSql.getSql();
        ISqlParser iSqlParser = new DruidSqlParser();
        try {
            iSqlParser.init(sql);
        } catch (Exception e) {
            e.printStackTrace();
            /** let go of the unknown sql */
            return;
        }

        String table = iSqlParser.getTableName();
        /** if no table found , let go , maybe some wired but legal sql or mybatis sql like "select #{id}" in SelectKey */
        if (StringUtils.isBlank(table))
            return;
        TableConfig tableConfig = ShardConfig.getTableConfig(table);
        if (tableConfig != null) {
            Object masterValue = findMasterValue(iSqlParser, boundSql, tableConfig);
            if (masterValue == null)
                throw new SqlParseException("no master value is found:" + sql);
            ShardResult re = tableConfig.getShardStrategy().indexTableByColumn(new ShardContext(masterValue, table));
            String destTable = re.getTableName();
            if (StringUtils.isNotBlank(destTable)) {
                iSqlParser.setTableName(re.getTableName());
                String sqlResult = iSqlParser.toSql();
                System.out.println("sqlResult->" + sqlResult);
                try {
                    ReflectionUtils.setDeclaredFieldValue(boundSql, "sql", sqlResult);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }

            String db = re.getDbName();
            if (StringUtils.isNoneBlank(db)) {
                DynamicDataSource.setDb(db);
            } else {
                DynamicDataSource.setDbDefault();
            }
        }
    }

    private Object findMasterValue(ISqlParser iSqlParser, BoundSql boundSql, TableConfig tableConfig) {
        if (iSqlParser.getType() == SqlType.INSERT) {
            /** Insert */
            List<ColumnValue> columnValues = iSqlParser.getcolumns();
            for (int i = 0; i < columnValues.size(); i++) {
                if (columnValues.get(i).column.equals(tableConfig.getMasterColumn())) {
                    try {
                        return ReflectionUtils.getFieldValue(boundSql.getParameterObject(), boundSql.getParameterMappings().get(i).getProperty());
                    } catch (Exception e) {
                        throw new SqlParseException("failed to parse property:" + boundSql.getParameterMappings().get(i).getProperty());
                    }
                }
            }
        } else {
            /** Select/Update/Delete -> columns from "where" clause */
            if (boundSql.getParameterObject() instanceof MapperMethod.ParamMap) {
                return getColumnValue(tableConfig.getMasterColumn(), iSqlParser, boundSql);
            }
        }
        return null;
    }

    private Object getColumnValue(String columnName, ISqlParser iSqlParser, BoundSql boundSql) {
        MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) boundSql.getParameterObject();
        List<ColumnValue> cols = iSqlParser.getcolumns();
        for (int i = 0; i < cols.size(); i++) {
            if (Objects.equals(cols.get(i).column, columnName)) {
                //                if (cols.get(i).value.equals("?")) //TODO
                return paramMap.get(boundSql.getParameterMappings().get(i).getProperty());
            }
        }
        return null;
    }
}
