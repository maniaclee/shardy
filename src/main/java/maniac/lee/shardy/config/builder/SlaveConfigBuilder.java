package maniac.lee.shardy.config.builder;

import maniac.lee.shardy.config.SlaveConfig;
import maniac.lee.shardy.config.strategy.ShardStrategy;
import maniac.lee.shardy.config.strategy.SlaveToMasterMapping;

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

    public SlaveConfigBuilder setSlaveToTableMapping(ShardStrategy slaveMapping) {
        slaveConfig.setSlaveMapping(slaveMapping);
        return this;
    }

    public SlaveConfigBuilder setSlaveToMasterMapping(SlaveToMasterMapping slaveMapping) {
        slaveConfig.setSlaveMapping(slaveMapping);
        return this;
    }

    public SlaveConfig build() {
        return slaveConfig.check();
    }
}
