package psyco.shardy.config;

/**
 * Created by lipeng on 16/2/5.
 */
public interface ShardStrategy {

    ShardResult map(ShardStrategyContext context);
}
