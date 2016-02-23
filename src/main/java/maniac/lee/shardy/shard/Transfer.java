package maniac.lee.shardy.shard;

/**
 * Created by peng on 16/2/18.
 */
public class Transfer {

    public static ThreadLocal<String> sqlShard = new ThreadLocal<>();

    public static void setSqlShard(String sql) {
        sqlShard.set(sql);
    }

    public static String getSqlShard() {
        return sqlShard.get();
    }
}
