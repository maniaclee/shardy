package psyco.shardy.interceptor;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.jdbc.PreparedStatementLogger;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import psyco.shardy.sqlparser.DruidSqlParser;
import psyco.shardy.sqlparser.ISqlParser;
import psyco.shardy.util.ProxyUtils;
import psyco.shardy.util.ReflectionUtils;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

@Intercepts({
        //        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class}),
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        //        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        //        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class ShardInterceptor implements Interceptor {

    private ISqlParser iSqlParser = new DruidSqlParser();

    public Object intercept(Invocation invocation) throws Throwable {
        /** statement is new Object with different memory address */
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        System.out.println(statementHandler.getClass());
        System.out.println("sql->" + sql);

        iSqlParser.init(sql);
        iSqlParser.setTableName("fuck");

        String masterColumn = "id";
        if (boundSql.getParameterObject() instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) boundSql.getParameterObject();
            Object masterValue = paramMap.get(masterColumn);
            System.out.println("param-> " + masterValue);
        }

        String sqlResult = iSqlParser.toSql();
        System.out.println("sqlResult->" + sqlResult);

        Statement statement = (Statement) invocation.getArgs()[0];
        ReflectionUtils.setDeclaredFieldValue(boundSql, "sql", sqlResult);

        Object h = ReflectionUtils.getFieldValue(statement, "h");
        if (h instanceof PreparedStatementLogger) {
            PreparedStatementLogger preparedStatementLogger = (PreparedStatementLogger) h;
            PreparedStatement preparedStatement = preparedStatementLogger.getPreparedStatement();
        }
        System.out.println(statement.getClass().getName());
        System.out.println("sta-> " + statementHandler);
        return invocation.proceed();
    }

    private Statement proxyStatement(Statement statement) {
        return ProxyUtils.proxyMethodInterceptor(statement, invocation -> invocation.proceed());
    }

    public Object interceptPrepare(Invocation invocation) throws Throwable {
        /** statement is new Object with different memory address */
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        System.out.println(statementHandler.getClass());
        System.out.println("sql->" + sql);

        iSqlParser.init(sql);
        iSqlParser.setTableName("fuck");

        String masterColumn = "id";
        if (boundSql.getParameterObject() instanceof MapperMethod.ParamMap) {
            MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) boundSql.getParameterObject();
            Object masterValue = paramMap.get(masterColumn);
            System.out.println("param-> " + masterValue);
        }

        String sqlResult = iSqlParser.toSql();
        System.out.println("sqlResult->" + sqlResult);

        ReflectionUtils.setDeclaredFieldValue(boundSql, "sql", sqlResult);
        System.out.println("sta-> " + statementHandler);
        return invocation.proceed();
    }

    private Object getColumnValue(BoundSql sql, String col) {
        return sql.getParameterMappings().stream().filter(parameterMapping -> Objects.equals(parameterMapping.getProperty(), col)).findFirst().orElse(null);
    }


    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }
}