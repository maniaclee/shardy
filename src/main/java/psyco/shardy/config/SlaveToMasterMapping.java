package psyco.shardy.config;

/**
 * Created by peng on 16/2/21.
 */
public interface SlaveToMasterMapping extends SlaveMapping {

    Object map(Object slaveColumn, String table);
}
