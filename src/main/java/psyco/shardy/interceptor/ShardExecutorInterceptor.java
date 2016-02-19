package psyco.shardy.interceptor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import psyco.shardy.SqlParseException;
import psyco.shardy.config.ShardConfig;
import psyco.shardy.config.ShardStrategyContext;
import psyco.shardy.config.ShardResult;
import psyco.shardy.config.TableConfig;
import psyco.shardy.datasource.DynamicDataSource;
import psyco.shardy.shard.ExtendedSqlSource;
import psyco.shardy.shard.Transfer;
import psyco.shardy.sqlparser.ISqlParser;
import psyco.shardy.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

@Intercepts({
        //        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class}),
        //        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class ShardExecutorInterceptor implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object arg = args[1];

        /** init MappedStatement */
        updateMappedStatement(mappedStatement);

        switch (mappedStatement.getSqlCommandType()) {
            case INSERT:
                return insert(mappedStatement, arg, invocation);
            case SELECT:
                return select(mappedStatement, arg, invocation);
            case UPDATE:
            case DELETE:
                return update(mappedStatement, arg, invocation);
        }
        return invocation.proceed();
    }

    private Object select(MappedStatement mappedStatement, Object arg, Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        ExtendedSqlSource extendedSqlSource = (ExtendedSqlSource) mappedStatement.getSqlSource();
        BoundSql boundSql = extendedSqlSource.buildBoundSql(arg);
        String sql = boundSql.getSql();
        ISqlParser iSqlParser = ExtendedSqlSource.createISqlParser(sql);

        String table = iSqlParser.getTableName();
        /** if no table found , let go , maybe some wired but legal sql or mybatis sql like "select #{id}" in SelectKey */
        if (StringUtils.isBlank(table))
            return invocation.proceed();
        TableConfig tableConfig = ShardConfig.getTableConfig(table);
        if (tableConfig != null) {
            Object masterValue = ExtendedSqlSource.findMasterValue(iSqlParser, boundSql, tableConfig);
            if (masterValue == null)
                throw new SqlParseException("no master value is found:" + sql);
            if (masterValue instanceof List) {
                List masters = (List) masterValue;
                if (masters.isEmpty())
                    return invocation.proceed();
                /** only select first to route table & all the master values must be in the SAME table */
                Multimap<String, Object> shards = parseValueList(masters, tableConfig);
                List re = Lists.newLinkedList();
                for (String t : shards.keySet()) {
                    iSqlParser.setTableName(t);
                    /** don't worry about the parameter count,just changing the table will do */
                    String sqlResult = iSqlParser.toSql();
                    Transfer.setSqlShard(sqlResult);
                    List tmp = (List) invocation.proceed();
                    if (tmp != null)
                        re.addAll(tmp);
                }
                return re;
            }
            ShardResult re = tableConfig.getShardStrategy().indexTableByColumn(new ShardStrategyContext(masterValue, table));
            String destTable = re.getTableName();
            if (StringUtils.isNotBlank(destTable)) {
                iSqlParser.setTableName(re.getTableName());
                String sqlResult = iSqlParser.toSql();
                System.out.println("sqlResult->" + sqlResult);
                Transfer.setSqlShard(sqlResult);
            }

            String db = re.getDbName();
            if (StringUtils.isNoneBlank(db)) {
                DynamicDataSource.setDb(db);
            } else {
                DynamicDataSource.setDbDefault();
            }
        }
        return invocation.proceed();
    }

    private Multimap<String, Object> parseValueList(List masters, TableConfig config) {
        Multimap<String, Object> myMultimap = ArrayListMultimap.create();
        for (Object o : masters) {
            ShardResult shardResult = config.getShardStrategy().indexTableByColumn(new ShardStrategyContext(o, config.getTable()));
            if (StringUtils.isNoneBlank(shardResult.getTableName()))
                myMultimap.put(shardResult.getTableName(), o);
        }
        return myMultimap;
    }

    private Object insert(MappedStatement mappedStatement, Object arg, Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        return invocation.proceed();
    }

    private Object update(MappedStatement mappedStatement, Object arg, Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        return invocation.proceed();
    }


    private void updateMappedStatement(MappedStatement mappedStatement) throws NoSuchFieldException {
        if (mappedStatement.getSqlSource() instanceof ExtendedSqlSource)
            return;
        ReflectionUtils.setDeclaredFieldValue(mappedStatement, "sqlSource", ExtendedSqlSource.instance(mappedStatement));
    }

    private void changeSql(MappedStatement mappedStatement, String sqlResult) throws NoSuchFieldException, IllegalAccessException {
        if (mappedStatement.getSqlSource() instanceof RawSqlSource) {
            RawSqlSource sqlSource = (RawSqlSource) mappedStatement.getSqlSource();
            StaticSqlSource staticSqlSource = (StaticSqlSource) ReflectionUtils.getFieldValue(sqlSource, "sqlSource");
            ReflectionUtils.setDeclaredFieldValue(staticSqlSource, "sql", sqlResult);
        }
    }


    public void init(Collection<TableConfig> tableConfigs) {
        ShardConfig.init(tableConfigs);
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }

}