package maniac.lee.shardy.shard;

/**
 * Created by peng on 16/2/18.
 */
public class Transfer {

    public static ThreadLocal<String> sqlShard = new ThreadLocal<>();
    public static ThreadLocal<String> forceUseTable = new ThreadLocal<>();

    public static void setSqlShard(String sql) {
        sqlShard.set(sql);
    }

    public static String getSqlShard() {
        return sqlShard.get();
    }

    public static void setForceUseTable(String sql) {
        forceUseTable.set(sql);
    }

    public static String getForceUseTable() {
        return forceUseTable.get();
    }

    public static void clear() {
        sqlShard.remove();
        forceUseTable.remove();
    }

}
