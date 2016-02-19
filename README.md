# shardy

### MySql shard for Mybatis.

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

