package maniac.lee.shardy.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by lipeng on 16/2/16.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static ThreadLocal<String> dbHolder = new ThreadLocal<>();

    private static String DEFAULT_DB;

    public DynamicDataSource() {
    }

    public DynamicDataSource(String defaultDb) {
        if (defaultDb != null)
            throw new RuntimeException("DEFAULT_DB is already defined as : " + DEFAULT_DB);
        DEFAULT_DB = defaultDb;
    }


    public static void setDbDefault() {
        if (DEFAULT_DB != null)
            setDb(DEFAULT_DB);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDb();
    }

    public static void setDb(String db) {
        dbHolder.set(db);
    }

    public static void clearDb() {
        dbHolder.remove();
    }

    public static String getDb() {
        return dbHolder.get();
    }
}
