package maniac.lee.shardy.config;

import maniac.lee.shardy.config.strategy.IShardStrategy;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by peng on 16/2/20.
 */
public class SlaveConfig {
    String slaveColumn;
    IShardStrategy slaveMapping;

    public SlaveConfig() {
    }

    public SlaveConfig(String slaveColumn, IShardStrategy slaveMapping) {
        this.slaveColumn = slaveColumn;
        this.slaveMapping = slaveMapping;
    }

    public SlaveConfig check(){
        assert StringUtils.isNoneBlank(slaveColumn) && slaveMapping != null;
        return this;
    }
    public String getSlaveColumn() {
        return slaveColumn;
    }

    public void setSlaveColumn(String slaveColumn) {
        this.slaveColumn = slaveColumn;
    }

    public IShardStrategy getSlaveMapping() {
        return slaveMapping;
    }

    public void setSlaveMapping(IShardStrategy slaveMapping) {
        this.slaveMapping = slaveMapping;
    }
}
