package psyco.shardy.config;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;

/**
 * Created by lipeng on 16/2/19.
 */
public class ShardContext {
    MappedStatement mappedStatement;
    Object arg;
    Invocation invocation;
}
