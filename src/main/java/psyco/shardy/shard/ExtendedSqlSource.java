package psyco.shardy.shard;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import psyco.shardy.ShardException;
import psyco.shardy.SqlParseException;
import psyco.shardy.config.ShardContext;
import psyco.shardy.config.TableConfig;
import psyco.shardy.sqlparser.ColumnValue;
import psyco.shardy.sqlparser.DruidSqlParser;
import psyco.shardy.sqlparser.ISqlParser;
import psyco.shardy.util.ReflectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        /** thread local to transfer the value, for multi invoke */
        try {
            ReflectionUtils.setDeclaredFieldValue(boundSql, "sql", Transfer.getSqlShard());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new ShardException("error setting the sql result for sql:" + boundSql.getSql());
        }
        return boundSql;
    }

    public BoundSql buildBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    public BoundSql getBoundSqlRaw(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    public static ISqlParser createISqlParser(String sql) {
        ISqlParser iSqlParser = new DruidSqlParser();
        try {
            iSqlParser.init(sql);
            return iSqlParser;
        } catch (Exception e) {
            throw new SqlParseException("error parsing sql:" + sql);
        }
    }

    public static Object findColumnValue(String column, ShardContext context) {
        List<ColumnValue> columnValues = context.iSqlParser.getColumns();
        for (int i = 0; i < columnValues.size(); i++) {
            ColumnValue columnValue = columnValues.get(i);
            if (columnValue.column.equals(column)) {
                int start = columnValues.subList(0, i).stream().collect(Collectors.summingInt(value -> value.placeHolderCount));
                if (columnValue.placeHolderCount == 1)
                    return context.jdbcArgs.get(start);
                return context.jdbcArgs.subList(start, start + columnValue.placeHolderCount);
            }
        }
        return null;
    }

    @Deprecated
    private static Object findColumnValue(ISqlParser iSqlParser, BoundSql boundSql, TableConfig tableConfig) {
        /** Select/Update/Delete -> columns from "where" clause */
        if (boundSql.getParameterObject() instanceof MapperMethod.ParamMap) {
            return getColumnValue(tableConfig.getMasterColumn(), iSqlParser, boundSql);
        } else {
            /** parameter is object */
            List<ColumnValue> columnValues = iSqlParser.getColumns();
            for (int i = 0; i < columnValues.size(); i++) {
                if (columnValues.get(i).column.equals(tableConfig.getMasterColumn())) {
                    try {
                        return ReflectionUtils.getFieldValue(boundSql.getParameterObject(), boundSql.getParameterMappings().get(i).getProperty());
                    } catch (Exception e) {
                        throw new SqlParseException("failed to parse property:" + boundSql.getParameterMappings().get(i).getProperty());
                    }
                }
            }
        }
        return null;
    }

    public static Object getColumnValue(String columnName, ISqlParser iSqlParser, BoundSql boundSql) {
        MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) boundSql.getParameterObject();
        List<ColumnValue> cols = iSqlParser.getColumns();
        for (int i = 0; i < cols.size(); i++) {
            if (Objects.equals(cols.get(i).column, columnName)) {
                //                if (cols.get(i).value.equals("?")) //TODO
                List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
                String property = parameterMappings.get(i).getProperty();
                if (!property.startsWith("_"))
                    return paramMap.get("param" + (i + 1)); //index start from 1
                return parameterMappings.stream()
                        .filter(p -> p.getProperty().startsWith("__frch_" + columnName))
                        .map(parameterMapping -> boundSql.getAdditionalParameter(parameterMapping.getProperty()))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }

}
