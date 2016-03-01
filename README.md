# shardy

### MySql shard for Mybatis.

Goal:	Shard quietly base on tableName , never change the sql.

### Usage



##### Config in Spring

``` java
	@Bean
    public ShardInterceptorFactoryBean shardInterceptor() {//create the Shardy bean
        return new ShardInterceptorFactoryBean();
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(Interceptor shardInterceptor) {
        SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
      	....
          //register the Shardy plugin to Mybatis
        ssfb.setPlugins(new Interceptor[]{shardInterceptor});
        return ssfb;
    }
```

##### Get started

If you want a tableName to shard by a column,provide a TableConfig bean like this:

``` java
	@Bean
    public TableConfig User() {
        return TableConfigBuilder.instance()
                .table("User")
                .masterColumn("id")
                .shardStrategy( context -> ShardResult.ofTable("User_" + context.getColumnInt()%100))
                .build();
    }
```

That's done. You don't need to change your sql at all.

#####Slave dimension

If you want a tableName to shard by a slave column,provide a TableConfig bean like this:

```java
	@Bean
    public TableConfig User() {
        return TableConfigBuilder.instance()
                .table("User")
                .masterColumn("id")
                .shardStrategy(context -> ShardResult.ofTable("User_" + context.getColumnInt()%100))
                .slaveConfigs(Lists.newArrayList(
                        SlaveConfigBuilder.instance()
                                .setSlaveColumn("name")
                                .setSlaveToTableMapping(context -> ShardResult.ofTable(context.getTable() + "_" +  context.getColumnInt() / 1000))
                                .build()))
                .build();
    }
```

That's done. Now shardy will route table by master or slave dimension.

#####Db route

Config a Db datasource router DynamicDataSource, and tell it the Dao to be intercepted by Spring aop expression:

```java
	@Bean
    public AbstractRoutingDataSource dynamicDataSource(DataSource user, DataSource user_shard) {
      	//user is the default datasource to use
        return DynamicDataSource.instance(user, ImmutableMap.of("user_shard", user_shard));
    }

    @Bean
    public DefaultPointcutAdvisor dbShard() {
        return DbShardFactory.createDbShardInterceptor("execution(* maniac.lee.shardy.test.dal.mapper..*.*(..))");
    }
```

and then you can annotate you dao layer with @DbRouter  to route by different datasources :

```java
public interface DaoLayer {

    @Select("select * from User where id < #{idVar}")
    @DbRouter("user_shard") //the name that you config in DynamicDataSource
  	List<User> find(@Param("idVar") long id);
}
```

 @DbRouter can be used in interface and method, of course method annotation will have the higher priority. If you don't use the @DbRouter, the default datasource "user" will be used.

Ps: Don't forget to let SqlSessionFactoryBean to use the DynamicDataSource:

```java
 	SqlSessionFactoryBean ssfb = new SqlSessionFactoryBean();
        ssfb.setPlugins(new Interceptor[]{shardInterceptor});
        ssfb.setDataSource(dynamicDataSource); // use DynamicDataSource
		...
```



##### Maven import

``` xml
 	<dependencies>
        <dependency>
            <groupId>maniaclee</groupId>
    		<artifactId>shardy</artifactId>
    		<version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>maniaclee-mvn-repo</id>
            <url>https://github.com/maniaclee/maniaclee-mvn-repo/tree/master/repository</url>
        </repository>
    </repositories>
```

### Requirements

1. Mybatis 3.0
2. Jdk 1.8
3. druid(alibaba)
4. commons-lang3 & guava

### Supported Sql

1-	by master column, operator "="

``` sql
select * from T where id = ?
```

2-	by master column, operator "in"

``` sql
update T set status = ? where id in (?,?,?)
```

3-	insert with id

``` sql
insert into T(id,status ....) values(?,? ....)
```

join/union , order by and slave search is not supported for now , to be continued ...

### Contact

If you have any issues , feel free to send email to dianxiangan@aliyun.com, thx.