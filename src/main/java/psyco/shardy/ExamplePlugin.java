package psyco.shardy;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import psyco.shardy.util.ReflectionUtils;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class}),
        //        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class ExamplePlugin implements Interceptor {

    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("invoke->" + invocation.getMethod().getName());
        Object[] args = invocation.getArgs();
        if (invocation.getTarget() instanceof StatementHandler) {
            /** statement is new Object with different memory address */
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = statementHandler.getBoundSql();
            System.out.println("bound sql->" + ToStringBuilder.reflectionToString(boundSql));
            String sql = boundSql.getSql();

            System.out.println("sql->" + sql);

            ReflectionUtils.setDeclaredFieldValue(boundSql, "sql", sql);
            System.out.println("sta" + statementHandler);

        }
        if (invocation.getTarget() instanceof Executor) {
            MappedStatement mappedStatement = (MappedStatement) args[0];
            //            System.out.println("mapst->" + mappedStatement);
        }
        return invocation.proceed();
    }


    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }
}