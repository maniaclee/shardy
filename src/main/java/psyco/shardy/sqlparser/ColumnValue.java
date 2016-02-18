package psyco.shardy.sqlparser;

/**
 * Created by lipeng on 16/2/16.
 */
public class ColumnValue {

    public String column;
    public String value;

    public ColumnValue(String column, String value) {
        this.column = column;
        this.value = value;
    }

    @Override
    public String toString() {
        return "ColumnValue{" +
                "column='" + column + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
