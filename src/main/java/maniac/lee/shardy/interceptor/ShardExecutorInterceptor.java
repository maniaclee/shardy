package maniac.lee.shardy.interceptor;

import maniac.lee.shardy.config.ShardContext;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import maniac.lee.shardy.config.ShardConfig;
import maniac.lee.shardy.config.TableConfig;
import maniac.lee.shardy.shard.ExtendedSqlSource;
import maniac.lee.shardy.shard.ShardExecutor;
import maniac.lee.shardy.util.ReflectionUtils;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by lipeng on 16/2/5.
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class ShardExecutorInterceptor implements Interceptor {

    ShardExecutor shardExecutor = new ShardExecutor();

    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object arg = args[1];
        Object target = invocation.getTarget();
        /** init MappedStatement */
        updateMappedStatement(mappedStatement);
        ShardContext shardContext = new ShardContext(mappedStatement, arg, invocation);
        return shardExecutor.exec(shardContext);
    }

    private void updateMappedStatement(MappedStatement mappedStatement) throws NoSuchFieldException {
        if (mappedStatement.getSqlSource() instanceof ExtendedSqlSource)
            return;
        ReflectionUtils.setDeclaredFieldValue(mappedStatement, "sqlSource", ExtendedSqlSource.instance(mappedStatement));
    }

    public void init(Collection<TableConfig> tableConfigs) {
        ShardConfig.init(tableConfigs);
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }

    public ShardExecutor getShardExecutor() {
        return shardExecutor;
    }

    public void setShardExecutor(ShardExecutor shardExecutor) {
        this.shardExecutor = shardExecutor;
    }
}