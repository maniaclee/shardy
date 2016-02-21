package psyco.shardy.config;

/**
 * Created by peng on 16/2/20.
 */
public class SlaveMappingResult extends ShardResult {
    private Object master;

    public SlaveMappingResult(String tableName, String dbName) {
        super(tableName, dbName);
    }

    public Object getMaster() {
        return master;
    }

    public void setMaster(Object master) {
        this.master = master;
    }

}
