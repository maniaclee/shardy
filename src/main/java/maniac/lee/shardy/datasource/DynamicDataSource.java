package maniac.lee.shardy.datasource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by lipeng on 16/2/16.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static ThreadLocal<String> dbHolder = new ThreadLocal<>();

    private String DEFAULT_DB;
    private static DynamicDataSource instance;

    private DynamicDataSource(String defaultDb) {
        if (DEFAULT_DB != null)
            throw new RuntimeException("DEFAULT_DB is already defined as : " + DEFAULT_DB);
        DEFAULT_DB = defaultDb;
    }

    public static DynamicDataSource instance(String defaultDb) {
        if (instance == null) {
            synchronized (DynamicDataSource.class) {
                if (instance == null) {
                    if (StringUtils.isBlank(defaultDb))
                        throw new RuntimeException(DynamicDataSource.class.getName() + " must have a default datasource name(id of datasource as spring bean)");
                    instance = new DynamicDataSource(defaultDb);
                }
            }
        }
        return instance;
    }

    public static void setDbDefault() {
        if (instance.DEFAULT_DB != null)
            setDb(instance.DEFAULT_DB);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDb();
    }

    public static void setDb(String db) {
        System.out.println("DB----->" + db);
        dbHolder.set(db);
    }

    public static void clearDb() {
        dbHolder.remove();
    }

    public static String getDb() {
        return dbHolder.get();
    }
}
