package psyco.shardy;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.aop.framework.ProxyFactoryBean;
import psyco.shardy.util.ReflectionUtils;

/**
 * Created by peng on 16/2/2.
 */
public class ShardSqlSessionFactoryBean extends SqlSessionFactoryBean {
    @Override
    public SqlSessionFactory getObject() throws Exception {
        SqlSessionFactory sqlSessionFactory = super.getObject();
        if (sqlSessionFactory instanceof DefaultSqlSessionFactory)
            return proxy(sqlSessionFactory, methodInvocation -> {
                if (methodInvocation.getMethod().getName().equals("openSession")) {
                    SqlSession session = (SqlSession) methodInvocation.proceed();
                    if (session instanceof DefaultSqlSession) {
                        DefaultSqlSession defaultSqlSession = (DefaultSqlSession) session;
                        Executor executor = (Executor) ReflectionUtils.getFieldValue(defaultSqlSession, "executor");
                        if (executor instanceof CachingExecutor) {
                            CachingExecutor cachingExecutor = (CachingExecutor) executor;
                            Executor ex = (Executor) ReflectionUtils.getFieldValue(cachingExecutor, "delegate");
                            System.out.println("ex-> " + ex.getClass().getName());
                        }
                    }
                    return session;
                }
                return methodInvocation.proceed();
            });
        return sqlSessionFactory;
    }

    private static <T> T proxy(T t, MethodInterceptor methodInterceptor) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTargetClass(t.getClass());
        proxyFactoryBean.setTarget(t);
        proxyFactoryBean.addAdvice(methodInterceptor);
        return (T) proxyFactoryBean.getObject();
    }
}
