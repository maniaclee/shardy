package maniac.lee.shardy.datasource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * Created by peng on 16/2/29.
 */

public class DbShardFactory {

    public static DefaultPointcutAdvisor createDbShardInterceptor(String springAop) {
        DefaultPointcutAdvisor re = new DefaultPointcutAdvisor();
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(springAop);
        re.setPointcut(aspectJExpressionPointcut);

        re.setAdvice(new MethodInterceptor() {
            public Object invoke(MethodInvocation methodInvocation) throws Throwable {
                Object clz = methodInvocation.getThis();
                DbRouter annotation = clz.getClass().getAnnotation(DbRouter.class);
                if (annotation != null) {
                    DynamicDataSource.setDb(annotation.value());
                } else {
                    DbRouter dbRouter = methodInvocation.getMethod().getAnnotation(DbRouter.class);
                    if (dbRouter != null) {
                        DynamicDataSource.setDb(dbRouter.value());
                    }
                }
                System.out.println("DB----> " + DynamicDataSource.getDb());
                try {
                    return methodInvocation.proceed();
                } finally {
                    DynamicDataSource.clearDb();
                }
            }
        });
        return re;
    }
}
