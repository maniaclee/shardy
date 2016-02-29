package maniac.lee.shardy.config.strategy;


import maniac.lee.shardy.ShardException;
import maniac.lee.shardy.config.ShardResult;
import maniac.lee.shardy.config.ShardStrategyContext;

import java.util.Arrays;

/**
 * Created by lipeng on 15/11/25.
 */
public class BucketArrayShardStrategy implements ShardStrategy {

    private final long offset;
    private final long[] bucketLengthIndexArray;
    private final boolean startFromOriginalTable;

    public BucketArrayShardStrategy(long[] bucketLengthArray) {
        this(0, bucketLengthArray, false);
    }

    public BucketArrayShardStrategy(long offset, long[] bucketLengthArray) {
        this(offset, bucketLengthArray, false);
    }

    public BucketArrayShardStrategy(long offset, long[] bucketLengthArray, boolean startFromOriginalTable) {
        this.offset = offset;
        this.bucketLengthIndexArray = new long[bucketLengthArray.length];
        for (int i = 0; i < bucketLengthArray.length; i++) {
            if (i == 0)
                bucketLengthIndexArray[i] = bucketLengthArray[i];
            else
                bucketLengthIndexArray[i] = bucketLengthArray[i] + bucketLengthIndexArray[i - 1];
        }
        this.startFromOriginalTable = startFromOriginalTable;
    }

    @Override
    public ShardResult map(ShardStrategyContext context) {
        String re = routeTable(context.getTable(), context.getColumnValue());
        return re == null ? null : new ShardResult(re);
    }

    public String routeTable(String table, Object col) throws ShardException {
        long bucket = findTableIndex(table, col);
        if (bucket == -1) {
            /** use the last table as the default table when */
            bucket = bucketLengthIndexArray.length - 1;
        }
        if (startFromOriginalTable)
            return bucket == 0 ? table : (table + "_" + (bucket - 1));
        return table + "_" + bucket;
    }

    public int findTableIndex(String table, Object column) throws ShardException {
        /** don't shard , use the original table */
        if (bucketLengthIndexArray.length <= 1 || column == null)
            return 0;
        if (column == null || !(column instanceof Number))
            throw new ShardException(String.format("%s only support continuous number column : table[%s] column[%s]", getClass().getSimpleName(), table, column));
        long col = ((Number) column).longValue() - offset;
        if (col <= 0) {
            /** if column < offset ,use the first table*/
            return 0;
        }

        for (int i = 0; i < bucketLengthIndexArray.length; i++)
            if (col < bucketLengthIndexArray[i])
                return i;
        return -1;
    }

    public long[] getBucketLengthIndexArray() {
        return Arrays.copyOf(bucketLengthIndexArray, bucketLengthIndexArray.length);
    }

}
