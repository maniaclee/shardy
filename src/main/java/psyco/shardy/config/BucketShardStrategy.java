package psyco.shardy.config;

/**
 * Created by lipeng on 16/2/16.
 */
public class BucketShardStrategy implements ShardStrategy {
    @Override
    public ShardResult indexTableByColumn(ShardContext context) {
        return new ShardResult(context.getTable()+"_0" , null);
    }
}
