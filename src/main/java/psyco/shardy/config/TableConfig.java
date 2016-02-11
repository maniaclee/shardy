package psyco.shardy.config;

/**
 * Created by lipeng on 16/2/5.
 */
public class TableConfig {

    private String table;
    private String masterColumn;
    private ShardStrategy shardStrategy;

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
}
