package maniac.lee.shardy.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lipeng on 16/2/16.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static ThreadLocal<String> dbHolder = new ThreadLocal<>();

    private static DynamicDataSource instance;

    public static DynamicDataSource instance(DataSource defaultDb, Map<String, DataSource> map) {
        if (instance == null) {
            synchronized (DynamicDataSource.class) {
                if (instance == null) {
                    if (defaultDb == null)
                        throw new RuntimeException(DynamicDataSource.class.getName() + " must have a default datasource name(id of datasource as spring bean)");
                    instance = new DynamicDataSource();
                    instance.setDefaultTargetDataSource(defaultDb);
                    instance.setTargetDataSources(convert(map));
                }
            }
        }
        return instance;
    }

    private static Map<Object, Object> convert(Map<String, DataSource> map) {
        Map re = new HashMap<>();
        for (String s : map.keySet()) re.put(s, map.get(s));
        return re;
    }

    public static void setDbDefault() {
        clearDb();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return dbHolder.get();
    }

    public static void setDb(String db) {
        dbHolder.set(db);
    }

    public static String getDb() {
        return dbHolder.get();
    }


    public static void clearDb() {
        dbHolder.remove();
    }

}
