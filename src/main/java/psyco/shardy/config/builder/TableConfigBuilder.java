package psyco.shardy.config.builder;

import psyco.shardy.config.ShardStrategy;
import psyco.shardy.config.SlaveConfig;
import psyco.shardy.config.TableConfig;

import java.util.List;

/**
 * Created by peng on 16/2/20.
 */
public class TableConfigBuilder {
    TableConfig tableConfig = new TableConfig();

    private TableConfigBuilder() {
    }

    public static TableConfigBuilder instance() {
        return new TableConfigBuilder();
    }

    public TableConfigBuilder table(String table) {
        tableConfig.setTable(table);
        return this;
    }

    public TableConfigBuilder masterColumn(String masterColumn) {
        tableConfig.setMasterColumn(masterColumn);
        return this;
    }

    public TableConfigBuilder shardStrategy(ShardStrategy shardStrategy) {
        tableConfig.setShardStrategy(shardStrategy);
        return this;
    }

    public TableConfigBuilder slaveConfigs(List<SlaveConfig> slaveConfigs) {
        tableConfig.setSlaveConfigs(slaveConfigs);
        return this;
    }

    public TableConfig build() {
        return tableConfig.check();
    }
}
