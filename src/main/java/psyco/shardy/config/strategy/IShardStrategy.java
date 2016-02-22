package psyco.shardy.config.strategy;

import psyco.shardy.config.ShardStrategyContext;

/**
 * Created by lipeng on 16/2/22.
 */
public interface IShardStrategy {
    Object map(ShardStrategyContext context);
}
