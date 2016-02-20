package psyco.shardy.config;

/**
 * Created by peng on 16/2/20.
 */
public class SlaveMappingResult {
    private String table;
    private Object master;

    public SlaveMappingResult() {
    }

    public SlaveMappingResult(String table) {
        this.table = table;
    }

    public SlaveMappingResult(Object master) {
        this.master = master;
    }

    public SlaveMappingResult(String table, Object master) {
        this.table = table;
        this.master = master;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Object getMaster() {
        return master;
    }

    public void setMaster(Object master) {
        this.master = master;
    }
}
