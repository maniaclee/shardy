package maniac.lee.shardy.config;

import maniac.lee.shardy.datasource.DynamicDataSource;

/**
 * Created by lipeng on 16/2/5.
 */
public class ShardStrategyContext {

    private Object columnValue;
    private String table;
    private String db;

    public ShardStrategyContext(Object columnValue, String table) {
        this.columnValue = columnValue;
        this.table = table;
        this.db = DynamicDataSource.getDb();
    }


    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Object getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(Object columnValue) {
        this.columnValue = columnValue;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }
}
