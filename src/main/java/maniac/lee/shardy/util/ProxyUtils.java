package maniac.lee.shardy.util;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactoryBean;

/**
 * Created by lipeng on 16/2/16.
 */
public class ProxyUtils {

    public static <T> T proxyAdvice(T t, Advice methodInterceptor) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTargetClass(t.getClass());
        proxyFactoryBean.setTarget(t);
        proxyFactoryBean.addAdvice(methodInterceptor);
        return (T) proxyFactoryBean.getObject();
    }

    public static <T> T proxyMethodInterceptor(T t, MethodInterceptor methodInterceptor) {
        return proxyAdvice(t, methodInterceptor);
    }
}
