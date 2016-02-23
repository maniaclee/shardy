package maniac.lee.shardy.config.strategy;

import maniac.lee.shardy.config.ShardStrategyContext;

/**
 * Created by lipeng on 16/2/22.
 */
public interface IShardStrategy {
    Object map(ShardStrategyContext context);
}
