package psyco.shardy.config;

import org.apache.commons.lang3.StringUtils;
import psyco.shardy.config.strategy.ShardStrategy;

import java.util.List;

/**
 * Created by lipeng on 16/2/5.
 */
public class TableConfig {

    private String table;
    private String masterColumn;
    private ShardStrategy shardStrategy;
    private List<SlaveConfig> slaveConfigs;

    public TableConfig check() {
        assert StringUtils.isNoneBlank(table) && StringUtils.isNoneBlank(masterColumn) && shardStrategy != null;
        return this;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getMasterColumn() {
        return masterColumn;
    }

    public void setMasterColumn(String masterColumn) {
        this.masterColumn = masterColumn;
    }

    public ShardStrategy getShardStrategy() {
        return shardStrategy;
    }

    public void setShardStrategy(ShardStrategy shardStrategy) {
        this.shardStrategy = shardStrategy;
    }

    public List<SlaveConfig> getSlaveConfigs() {
        return slaveConfigs;
    }

    public void setSlaveConfigs(List<SlaveConfig> slaveConfigs) {
        this.slaveConfigs = slaveConfigs;
    }

}
