package psyco.shardy.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import psyco.shardy.util.ReflectionUtils;

import java.sql.Connection;
import java.util.Objects;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class}),
        //        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        //        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        //        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class ShardInterceptor implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("invoke->" + invocation.getMethod().getName());
        /** statement is new Object with different memory address */
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        System.out.println("sql->" + sql);

        ReflectionUtils.setDeclaredFieldValue(boundSql, "sql", sql);
        System.out.println("sta" + statementHandler);
        return invocation.proceed();
    }

    private Object getColumnValue(BoundSql sql , String col){
        return sql.getParameterMappings().stream().filter(parameterMapping -> Objects.equals(parameterMapping.getProperty(),col)).findFirst().orElse(null);
    }


    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }
}