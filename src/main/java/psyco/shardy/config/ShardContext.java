package psyco.shardy.config;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import psyco.shardy.shard.ExtendedSqlSource;
import psyco.shardy.shard.JdbcParameterHandler;
import psyco.shardy.sqlparser.ISqlParser;

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

    public ShardContext(MappedStatement mappedStatement, Object arg, Invocation invocation) {
        this.mappedStatement = mappedStatement;
        this.arg = arg;
        this.invocation = invocation;
        ExtendedSqlSource extendedSqlSource = (ExtendedSqlSource) mappedStatement.getSqlSource();
        this.boundSql = extendedSqlSource.buildBoundSql(arg);
        jdbcArgs = JdbcParameterHandler.getParameters(mappedStatement, arg, boundSql);
        this.iSqlParser = ExtendedSqlSource.createISqlParser(boundSql.getSql());
    }
}
