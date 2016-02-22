package psyco.shardy.shard;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import psyco.shardy.ShardException;
import psyco.shardy.SqlParseException;
import psyco.shardy.config.*;
import psyco.shardy.datasource.DynamicDataSource;
import psyco.shardy.sqlparser.ISqlParser;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lipeng on 16/2/19.
 */
public class ShardExecutor {

    public Object exec(ShardContext shardContext) throws InvocationTargetException, IllegalAccessException {
        /** if no table found , let go , maybe some wired but legal sql or mybatis sql like "select #{id}" in SelectKey */
        if (!shardContext.canShard())
            return shardContext.invocation.proceed();

        TableConfig tableConfig = shardContext.tableConfig;
        /** -------- master dimension ----------- */
        Object masterValue = ExtendedSqlSource.findColumnValue(tableConfig.getMasterColumn(), shardContext);
        if (masterValue != null)
            return routeMasterColumn(masterValue, shardContext);

        /** -------- slave dimension ----------- */
        if (tableConfig.getSlaveConfigs() != null) {
            for (SlaveConfig slaveConfig : tableConfig.getSlaveConfigs()) {
                Object slaveValue = ExtendedSqlSource.findColumnValue(slaveConfig.getSlaveColumn(), shardContext);
                return routeSlaveValues(slaveValue, shardContext, slaveConfig);
            }
        }
        throw new ShardException("no table to route for sql:" + shardContext.iSqlParser.getSqlOriginal());
    }

    private Object routeMasterColumn(Object masterValue, ShardContext shardContext) throws InvocationTargetException, IllegalAccessException {
        if (masterValue == null)
            throw new SqlParseException("no master value is found:" + shardContext.boundSql.getSql());
        TableConfig tableConfig = shardContext.tableConfig;
        if (masterValue instanceof List) {
            List masters = (List) masterValue;
            if (masters.isEmpty())
                return shardContext.invocation.proceed();
            /** only select first to route table & all the master values must be in the SAME table */
            Multimap<String, Object> shards = parseValueList(masters, tableConfig);
            return execSqlList(shards, shardContext);
        }
        ShardResult re = tableConfig.getShardStrategy().map(new ShardStrategyContext(masterValue, tableConfig.getTable()));

        if (!shard(shardContext.iSqlParser, re))
            throw new ShardException("no table found to shard for sql + " + shardContext.iSqlParser.getSqlOriginal());
        return shardContext.invocation.proceed();
    }

    private Object routeSlaveValues(Object slaveValue, ShardContext shardContext, SlaveConfig slaveConfig) throws InvocationTargetException, IllegalAccessException {
        if (slaveValue == null)
            throw new SqlParseException("no slave value is found:" + shardContext.boundSql.getSql());
        String table = shardContext.table;
        if (slaveConfig.getSlaveMapping() instanceof SlaveToMasterMapping) {
            SlaveToMasterMapping slaveToMasterMapping = (SlaveToMasterMapping) slaveConfig.getSlaveMapping();
            Object master = null;
            if (slaveValue instanceof List) {
                master = ((List) slaveValue).stream().map(e -> slaveToMasterMapping.map(e, table)).filter(a -> a != null).collect(Collectors.toList());
            } else {
                master = slaveToMasterMapping.map(slaveValue, table);
            }
            return routeMasterColumn(master, shardContext);
        } else if (slaveConfig.getSlaveMapping() instanceof SlaveToTableMapping) {
            SlaveToTableMapping slaveToTableMapping = (SlaveToTableMapping) slaveConfig.getSlaveMapping();
            if (slaveValue instanceof List) {
                Multimap<String, Object> parseValueList4slave = parseValueList4slave((List) slaveValue, slaveConfig, shardContext.tableConfig);
                return execSqlList(parseValueList4slave, shardContext);
            }
            return shardResult(shardContext, slaveToTableMapping.map(new ShardStrategyContext(slaveValue, table)));
        }
        throw new ShardException("unknow SlaveConfig Type");
    }

    private Object execSqlList(Multimap<String, Object> shards, ShardContext shardContext) throws InvocationTargetException, IllegalAccessException {
        if (shards == null || shards.isEmpty())
            return null;
        MappedStatement mappedStatement = shardContext.mappedStatement;
        if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
            return convertSelect(shards, shardContext.iSqlParser, shardContext.invocation);
        } else if (mappedStatement.getSqlCommandType() == SqlCommandType.DELETE || mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE) {
            return convertUpdate(shards, shardContext.iSqlParser, shardContext.invocation);
        }
        throw new ShardException("List operation is not supported for sql type :" + mappedStatement.getSqlCommandType());
    }


    private Object shardResult(ShardContext shardContext, ShardResult shardResult) throws InvocationTargetException, IllegalAccessException {
        if (!shard(shardContext.iSqlParser, shardResult))
            throw new ShardException("no table found to shard of slave for sql + " + shardContext.iSqlParser.getSqlOriginal());
        return shardContext.invocation.proceed();
    }

    private boolean shard(ISqlParser iSqlParser, ShardResult shardResult) {
        return shard(iSqlParser, shardResult.getTableName(), shardResult.getDbName());
    }

    private boolean shard(ISqlParser iSqlParser, String destTable, String db) {
        boolean re = shardTable(iSqlParser, destTable);
        if (re)
            shardDb(db);
        return re;
    }


    private boolean shardTable(ISqlParser iSqlParser, String destTable) {
        if (StringUtils.isNotBlank(destTable)) {
            iSqlParser.setTableName(destTable);
            String sqlResult = iSqlParser.toSql();
            System.out.println("sqlResult->" + sqlResult);
            Transfer.setSqlShard(sqlResult);
            return true;
        } else
            return false;
    }

    private void shardDb(String db) {
        if (StringUtils.isNoneBlank(db)) {
            DynamicDataSource.setDb(db);
        } else {
            DynamicDataSource.setDbDefault();
        }
    }

    public static int convertUpdate(Multimap<String, Object> shards, ISqlParser iSqlParser, Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        int re = 0;
        for (String t : shards.keySet()) {
            iSqlParser.setTableName(t);
            /** don't worry about the parameter count,just changing the table will do */
            String sqlResult = iSqlParser.toSql();
            Transfer.setSqlShard(sqlResult);
            int tmp = (int) invocation.proceed();
            re += tmp;
        }
        return re;
    }

    public static List convertSelect(Multimap<String, Object> shards, ISqlParser iSqlParser, Invocation invocation) throws InvocationTargetException, IllegalAccessException {
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


    public static Multimap<String, Object> parseValueList(List masters, TableConfig config) {
        Multimap<String, Object> myMultimap = ArrayListMultimap.create();
        for (Object o : masters) {
            ShardResult shardResult = config.getShardStrategy().map(new ShardStrategyContext(o, config.getTable()));
            if (StringUtils.isNoneBlank(shardResult.getTableName()))
                myMultimap.put(shardResult.getTableName(), o);
        }
        return myMultimap;
    }

    public static Multimap<String, Object> parseValueList4slave(List slaveValue, SlaveConfig slaveConfig, TableConfig tableConfig) {
        ShardStrategy shardStrategy = (ShardStrategy) slaveConfig.getSlaveMapping();
        Multimap<String, Object> re = ArrayListMultimap.create();
        for (Object a : slaveValue) {
            ShardResult tableResult = shardStrategy.map(new ShardStrategyContext(a, tableConfig.getTable()));
            if (tableResult != null)
                re.put(tableResult.getTableName(), a);
        }
        return re;
    }
}
