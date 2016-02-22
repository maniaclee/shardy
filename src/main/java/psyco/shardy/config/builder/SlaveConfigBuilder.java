package psyco.shardy.config.builder;

import psyco.shardy.config.strategy.IShardStrategy;
import psyco.shardy.config.SlaveConfig;

/**
 * Created by peng on 16/2/20.
 */
public class SlaveConfigBuilder {
    SlaveConfig slaveConfig = new SlaveConfig();

    private SlaveConfigBuilder() {
    }

    public static SlaveConfigBuilder instance() {
        return new SlaveConfigBuilder();
    }

    public SlaveConfigBuilder setSlaveColumn(String slaveColumn) {
        slaveConfig.setSlaveColumn(slaveColumn);
        return this;
    }

    public SlaveConfigBuilder setSlaveMapping(IShardStrategy slaveMapping) {
        slaveConfig.setSlaveMapping(slaveMapping);
        return this;
    }

    public SlaveConfig build() {
        return slaveConfig.check();
    }
}
