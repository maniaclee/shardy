package psyco.shardy.config;

/**
 * Created by lipeng on 16/2/5.
 */
public class ShardResult {

    private String tableName;
    private String DbName;

    public ShardResult(String tableName, String dbName) {
        this.tableName = tableName;
        DbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbName() {
        return DbName;
    }

    public void setDbName(String dbName) {
        DbName = dbName;
    }
}
