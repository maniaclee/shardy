package psyco.shardy.config;

/**
 * Created by lipeng on 16/2/5.
 */
public class ShardContext {

    private Object columnValue;
    private String table;

    public ShardContext(Object columnValue, String table) {
        this.columnValue = columnValue;
        this.table = table;
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
}
