# shardy

MySql shard for Mybatis.

### Usage

##### Maven import

``` xml
 	<dependencies>
        <dependency>
            <groupId>psyco</groupId>
    		<artifactId>shardy</artifactId>
    		<version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>psyco4j-maven-repo</id>
            <url>https://github.com/psyco4j/psyco4j-mvn-repo/tree/master/repository</url>
        </repository>
    </repositories>
```



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

##### Config table to shard

If you want a table to shard by a column,provide a TableConfig bean like this:

``` java
 	@Bean
    public TableConfig User() {
        TableConfig config = new TableConfig();
        config.setTable("User");	//set table to shard
        config.setMasterColumn("id");//set table column to shard
        config.setShardStrategy(new ShardStrategy() {
            @Override
            public ShardResult indexTableByColumn(ShardStrategyContext context) {
              	//how to route table by column value
                long columnValue = (long) context.getColumnValue();
                return new ShardResult(context.getTable() + "_" + columnValue/10000,null);
            }
        });
        return config;
    }
```

That's done. You don't need to change your sql at all.

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