package psyco.shardy.config;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;

/**
 * Created by lipeng on 16/2/19.
 */
public class ShardContext {
    public MappedStatement mappedStatement;
    public Object arg;
    public Invocation invocation;

    public ShardContext(MappedStatement mappedStatement, Object arg, Invocation invocation) {
        this.mappedStatement = mappedStatement;
        this.arg = arg;
        this.invocation = invocation;
    }
}
