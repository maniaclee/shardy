package maniac.lee.shardy.shard;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import maniac.lee.shardy.ShardException;
import maniac.lee.shardy.SqlParseException;
import maniac.lee.shardy.config.*;
import maniac.lee.shardy.config.strategy.ShardStrategy;
import maniac.lee.shardy.config.strategy.SlaveToMasterMapping;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by lipeng on 16/2/19.
 */
public class ShardExecutor {

    public Object exec(ShardContext shardContext) throws InvocationTargetException, IllegalAccessException {
        /** if no table found , let go , maybe some wired but legal sql or mybatis sql like "select #{id}" in SelectKey */
        if (!shardContext.canShard())
            return shardContext.invoke();

        TableConfig tableConfig = shardContext.tableConfig;
        /** -------- master dimension ----------- */
        Object masterValue = ExtendedSqlSource.findColumnValue(tableConfig.getMasterColumn(), shardContext);
        if (masterValue != null)
            return routeMaster(masterValue, shardContext);

        /** -------- slave dimension ----------- */
        if (tableConfig.getSlaveConfigs() != null) {
            for (SlaveConfig slaveConfig : tableConfig.getSlaveConfigs()) {
                Object slaveValue = ExtendedSqlSource.findColumnValue(slaveConfig.getSlaveColumn(), shardContext);
                return routeSlave(slaveValue, shardContext, slaveConfig);
            }
        }
        throw new ShardException("no table to route for sql:" + shardContext.iSqlParser.getSqlOriginal());
    }

    private Object routeMaster(Object masterValue, ShardContext shardContext) throws InvocationTargetException, IllegalAccessException {
        return routeColumnByStrategy(masterValue, shardContext, shardContext.tableConfig.getShardStrategy());
    }


    private Object routeSlave(Object slaveValue, ShardContext shardContext, SlaveConfig slaveConfig) throws InvocationTargetException, IllegalAccessException {
        if (slaveValue == null)
            throw new SqlParseException("no slave value is found:" + shardContext.boundSql.getSql());

        if (slaveConfig.getSlaveMapping() instanceof ShardStrategy)
            return routeColumnByStrategy(slaveValue, shardContext, ((ShardStrategy) slaveConfig.getSlaveMapping()));

        String table = shardContext.table;
        if (slaveConfig.getSlaveMapping() instanceof SlaveToMasterMapping) {
            SlaveToMasterMapping slaveToMasterMapping = (SlaveToMasterMapping) slaveConfig.getSlaveMapping();
            Object master;
            if (slaveValue instanceof List) {
                master = ((List) slaveValue).stream().map(e -> slaveToMasterMapping.map(new ShardStrategyContext(e, table))).filter(a -> a != null).collect(Collectors.toList());
            } else {
                master = slaveToMasterMapping.map(new ShardStrategyContext(slaveValue, table));
            }
            return routeMaster(master, shardContext);
        }
        throw new ShardException("unsupported SlaveConfig Type : " + slaveConfig.getSlaveMapping().getClass().getName());
    }

    private Object routeColumnByStrategy(Object masterValue, ShardContext shardContext, ShardStrategy shardStrategy) throws InvocationTargetException, IllegalAccessException {
        if (masterValue == null)
            throw new SqlParseException("no master value is found:" + shardContext.boundSql.getSql());
        String table = shardContext.tableConfig.getTable();
        if (masterValue instanceof List) {
            /** only select first to route table & all the master values must be in the SAME table */
            List<ShardResult> re = Lists.newLinkedList();
            for (Object o : (List) masterValue)
                re.add(shardStrategy.map(new ShardStrategyContext(o, table)));
            return execSqlList(shardContext, re.toArray(new ShardResult[0]));
        }
        return execSqlList(shardContext, shardStrategy.map(new ShardStrategyContext(masterValue, table)));
    }

    private Object execSqlList(ShardContext shardContext, ShardResult... shards) throws InvocationTargetException, IllegalAccessException {
        if (shards == null || shards.length == 0)
            return shardContext.invoke();
        ShardResultMap shardResultMap = ShardResultMap.create(Lists.newArrayList(shards));
        MappedStatement mappedStatement = shardContext.mappedStatement;
        switch (mappedStatement.getSqlCommandType()) {
            case SELECT:
                return execSelect(shardResultMap, shardContext);
            case DELETE:
            case INSERT:
            case UPDATE:
                return execUpdate(shardResultMap, shardContext);
        }
        throw new ShardException("not supported for sql type :" + mappedStatement.getSqlCommandType());
    }

    public static Object execSelect(ShardResultMap shardResultMap, ShardContext context) throws InvocationTargetException, IllegalAccessException {
        List re = Lists.newLinkedList();
        execSql(shardResultMap, context, o -> re.addAll((List) o));
        return re;
    }

    public static Object execUpdate(ShardResultMap shardResultMap, ShardContext context) throws InvocationTargetException, IllegalAccessException {
        int[] re = {0};
        execSql(shardResultMap, context, o -> re[0] += toInt(o));
        return re[0];
    }

    public static void execSql(ShardResultMap shardResultMap, ShardContext context, Consumer<Object> f) throws InvocationTargetException, IllegalAccessException {
        for (String db : shardResultMap.getDbs()) {
            shardDb(db);
            for (String table : shardResultMap.getTables(db)) {
                context.iSqlParser.setTableName(table);
                /** don't worry about the parameter count,just changing the table will do */
                String sqlResult = context.iSqlParser.toSql();
                Transfer.setSqlShard(sqlResult);
                Object result = context.invoke();
                if (result != null)
                    f.accept(result);
            }
        }
    }

    private static void shardDb(String db) {
//        if (StringUtils.isNoneBlank(db)) {
//            DynamicDataSource.setDb(db);
//        } else {
//            DynamicDataSource.setDbDefault();
//        }
    }

    private static int toInt(Object o) {
        if (o == null)
            return 0;
        if (o instanceof Number)
            return ((Number) o).intValue();
        return 0;
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
