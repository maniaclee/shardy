package maniac.lee.shardy.shard;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import maniac.lee.shardy.config.ShardResult;

import java.util.List;

/**
 * Created by lipeng on 16/2/23.
 */
public class ShardResultMap {

    Multimap<String, String> map = ArrayListMultimap.create();

    private ShardResultMap() {
    }

    public static ShardResultMap create() {
        return new ShardResultMap();
    }

    public static ShardResultMap create(Iterable<ShardResult> results) {
        ShardResultMap re = create();
        for (ShardResult result : results)
            re.put(result.getDbName(), result.getTableName());
        return re;
    }

    public static ShardResultMap create(ShardResult result) {
        ShardResultMap re = create();
        re.put(result.getDbName(), result.getTableName());
        return re;
    }

    public ShardResultMap put(String db, String table) {
        if (table == null)
            return null;
        map.put(db, table);
        return this;
    }

    public List<String> getDbs() {
        return Lists.newArrayList(map.keySet());
    }

    public List<String> getTables(String db) {
        return Lists.newArrayList(map.get(db));
    }
}
