package psyco.shardy.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by peng on 16/2/20.
 */
public class SlaveConfig {
    String slaveColumn;
    SlaveMapping slaveMapping;

    public SlaveConfig() {
    }

    public SlaveConfig(String slaveColumn, SlaveMapping slaveMapping) {
        this.slaveColumn = slaveColumn;
        this.slaveMapping = slaveMapping;
    }

    public SlaveConfig check(){
        assert StringUtils.isNoneBlank(slaveColumn) && slaveMapping != null;
        return this;
    }
    public String getSlaveColumn() {
        return slaveColumn;
    }

    public void setSlaveColumn(String slaveColumn) {
        this.slaveColumn = slaveColumn;
    }

    public SlaveMapping getSlaveMapping() {
        return slaveMapping;
    }

    public void setSlaveMapping(SlaveMapping slaveMapping) {
        this.slaveMapping = slaveMapping;
    }

}
