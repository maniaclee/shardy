package maniac.lee.shardy.config;

import maniac.lee.shardy.shard.Transfer;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import maniac.lee.shardy.shard.ExtendedSqlSource;
import maniac.lee.shardy.shard.JdbcParameterHandler;
import maniac.lee.shardy.sqlparser.ISqlParser;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by lipeng on 16/2/19.
 */
public class ShardContext {
    public final BoundSql boundSql;
    public final MappedStatement mappedStatement;
    public final Invocation invocation;
    public Object arg;
    public List jdbcArgs;
    public ISqlParser iSqlParser;

    public final String table; //could be null
    public TableConfig tableConfig;//could be null

    public ShardContext(MappedStatement mappedStatement, Object arg, Invocation invocation) {
        this.mappedStatement = mappedStatement;
        this.arg = arg;
        this.invocation = invocation;
        ExtendedSqlSource extendedSqlSource = (ExtendedSqlSource) mappedStatement.getSqlSource();
        this.boundSql = extendedSqlSource.buildBoundSql(arg);
        jdbcArgs = JdbcParameterHandler.getParameters(mappedStatement, arg, boundSql);
        this.iSqlParser = ExtendedSqlSource.createISqlParser(boundSql.getSql());

        /** if no table found , let go , maybe some wired but legal sql or mybatis sql like "select #{id}" in SelectKey */
        this.table = iSqlParser.getTableName();
        this.tableConfig = ShardConfig.getTableConfig(table);
    }

    public boolean canShard() {
        return StringUtils.isNoneBlank(table) && tableConfig != null;
    }

    public Object invoke() throws InvocationTargetException, IllegalAccessException {
        return invocation.proceed();
    }

    public Object invoke(String table) throws InvocationTargetException, IllegalAccessException {
        iSqlParser.setTableName(table);
        /** don't worry about the parameter count,just changing the table will do */
        String sqlResult = iSqlParser.toSql();
        Transfer.setSqlShard(sqlResult);
        return invoke();
    }
}
