package maniac.lee.shardy.config.strategy;

import maniac.lee.shardy.config.ShardStrategyContext;

/**
 * Created by peng on 16/2/21.
 */
public interface SlaveToMasterMapping extends IShardStrategy {
    Object map(ShardStrategyContext context);
}
