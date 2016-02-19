package psyco.shardy.config;

import psyco.shardy.ShardException;

/**
 * Created by lipeng on 16/2/16.
 */
public class BucketShardStrategy implements ShardStrategy {
    @Override
    public ShardResult indexTableByColumn(ShardStrategyContext context) {
        Object v = context.getColumnValue();
        if (!(v instanceof Number))
            throw new ShardException("column value must be number : " + v);
        Number number = (Number) v;
        long index = number.longValue() / 10000000;
        String suffix = index == 0 ? "" : ("_" + (index - 1));
        return new ShardResult(context.getTable() + suffix, null);
    }
}
