package psyco.shardy.config;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by lipeng on 16/2/5.
 */
public class ShardConfig {
    public static Map<String, TableConfig> tableConfigs = Maps.newHashMap();

    public static TableConfig getTableConfig(String table){
        return tableConfigs.get(table);
    }
}
