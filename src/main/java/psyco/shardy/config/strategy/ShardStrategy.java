package psyco.shardy.config.strategy;

import psyco.shardy.config.ShardResult;
import psyco.shardy.config.ShardStrategyContext;
import psyco.shardy.config.strategy.IShardStrategy;

/**
 * Created by lipeng on 16/2/5.
 */
public interface ShardStrategy extends IShardStrategy {

    ShardResult map(ShardStrategyContext context);
}
