package maniac.lee.shardy.config.strategy;

import maniac.lee.shardy.config.ShardResult;
import maniac.lee.shardy.config.ShardStrategyContext;

/**
 * Created by lipeng on 16/2/5.
 */
public interface ShardStrategy extends IShardStrategy {

    ShardResult map(ShardStrategyContext context);
}
