package psyco.test;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import psyco.shardy.interceptor.ShardInterceptor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by peng on 16/2/2.
 */
@Configuration
@MapperScan("psyco.test.dal.mapper")
@EnableTransactionManagement
@PropertySource("application.properties")
public class DalConfig {

    @Bean(autowire = Autowire.BY_NAME)
    public SqlSessionFactoryBean sqlSessionFactory() {
        SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
        ssfb.setTypeAliasesPackage("psyco.test.dal.entity");
        ssfb.setPlugins(new Interceptor[]{new ShardInterceptor()});
        return ssfb;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @Autowired
    public DruidDataSource dataSource(@Value("${user.jdbc.url}") String url,
                                      @Value("${user.jdbc.user}") String username,
                                      @Value("${user.jdbc.password}") String password) throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(60);
        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxWait(60000);//60s
        druidDataSource.setMinIdle(1);
        druidDataSource.setTimeBetweenEvictionRunsMillis(3000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setValidationQuery("select 1");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        //        druidDataSource.setFilters("psyco/user/center/config");
        Properties properties = new Properties();
        properties.put("config.decrypt", "true");
        druidDataSource.setConnectProperties(properties);

        StatFilter statFilter = new StatFilter();
        statFilter.setSlowSqlMillis(10000);//10s。。慢
        statFilter.setMergeSql(true);
        statFilter.setLogSlowSql(true);

        List<Filter> filterList = new ArrayList<Filter>();
        filterList.add(statFilter);
        druidDataSource.setProxyFilters(filterList);

        return druidDataSource;
    }

    @Bean(autowire = Autowire.BY_NAME)
    public TransactionTemplate transactionTemplate() {
        return new TransactionTemplate();
    }

    //    @Bean
    public DefaultPointcutAdvisor programAspect() {
        DefaultPointcutAdvisor re = new DefaultPointcutAdvisor();
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        //        aspectJExpressionPointcut.setExpression("execution(* org.apache.ibatis.executor.BaseExecutor.*(..))");
        aspectJExpressionPointcut.setExpression("execution(* psyco.test.dal.mapper..*.*(..))");
        re.setPointcut(aspectJExpressionPointcut);

        re.setAdvice(new MethodInterceptor() {
            public Object invoke(MethodInvocation methodInvocation) throws Throwable {
                System.out.println("fuck!!!!" + methodInvocation.getMethod().getName());
                return methodInvocation.proceed();
            }
        });
        return re;
    }
}
