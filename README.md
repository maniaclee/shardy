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

##### Config tableName to shard

If you want a tableName to shard by a column,provide a TableConfig bean like this:

``` java

public TableConfig User() {
        return TableConfigBuilder.instance()
                .table("User")
                .masterColumn("id")
                .setSlaveToTableMapping(context -> new ShardResult(context.getTable() + "_" + ((Integer) context.getColumnValue()) / 1000, null))
                                .build()))
                .build();
    }
```

That's done. You don't need to change your sql at all.

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