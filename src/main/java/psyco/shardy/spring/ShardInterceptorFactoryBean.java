package psyco.shardy.spring;

import com.google.common.collect.Lists;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import psyco.shardy.config.ShardConfig;
import psyco.shardy.config.TableConfig;
import psyco.shardy.interceptor.ShardExecutorInterceptor;
import psyco.shardy.util.ReflectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by lipeng on 16/2/16.
 */
public class ShardInterceptorFactoryBean implements FactoryBean<Interceptor>, ApplicationListener<ContextRefreshedEvent> {
    @Override
    public Interceptor getObject() throws Exception {
        return new ShardExecutorInterceptor();
    }

    @Override
    public Class<Interceptor> getObjectType() {
        return Interceptor.class;
    }


    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();

        try {
            installPlugin(context);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        Map<String, TableConfig> beans = context.getBeansOfType(TableConfig.class);
        if (beans != null)
            ShardConfig.init(beans.values());
    }

    private void installPlugin(ApplicationContext context) throws NoSuchFieldException, IllegalAccessException {
        SqlSessionFactoryBean ssfb = context.getBean(SqlSessionFactoryBean.class);
        Interceptor[] plugins = (Interceptor[]) ReflectionUtils.getFieldValue(ssfb, "plugins");
        List<Interceptor> interceptors = plugins == null ? Lists.newLinkedList() : Lists.newArrayList(plugins);
        interceptors.add(context.getBean(Interceptor.class));//TODO
        ssfb.setPlugins(interceptors.toArray(new Interceptor[0]));
    }
}
