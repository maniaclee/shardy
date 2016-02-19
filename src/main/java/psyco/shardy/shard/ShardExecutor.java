package psyco.shardy.shard;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import psyco.shardy.SqlParseException;
import psyco.shardy.config.*;
import psyco.shardy.datasource.DynamicDataSource;
import psyco.shardy.sqlparser.ISqlParser;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by lipeng on 16/2/19.
 */
public class ShardExecutor {

    public Object exec(ShardContext shardContext) throws InvocationTargetException, IllegalAccessException {
        MappedStatement mappedStatement = shardContext.mappedStatement;
        Invocation invocation = shardContext.invocation;
        ExtendedSqlSource extendedSqlSource = (ExtendedSqlSource) mappedStatement.getSqlSource();
        BoundSql boundSql = extendedSqlSource.buildBoundSql(shardContext.arg);
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
                if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
                    return convertSelect(shards, iSqlParser, invocation);
                } else if (mappedStatement.getSqlCommandType() == SqlCommandType.DELETE || mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE) {
                    return convertUpdate(shards, iSqlParser, invocation);
                }
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
            ShardResult shardResult = config.getShardStrategy().indexTableByColumn(new ShardStrategyContext(o, config.getTable()));
            if (StringUtils.isNoneBlank(shardResult.getTableName()))
                myMultimap.put(shardResult.getTableName(), o);
        }
        return myMultimap;
    }
}
