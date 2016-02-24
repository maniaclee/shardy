package maniac.lee.shardy.config;

/**
 * Created by lipeng on 16/2/5.
 */
public class ShardResult {

    private String tableName;
    private String dbName;

    public ShardResult(String tableName, String dbName) {
        this.tableName = tableName;
        this.dbName = dbName;
    }

    public static ShardResult create(String tableName, String dbName) {
        return new ShardResult(tableName, dbName);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
