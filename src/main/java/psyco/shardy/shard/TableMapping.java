package psyco.shardy.shard;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import psyco.shardy.mybatis.ExtendedSqlSource;
import psyco.shardy.sqlparser.DruidSqlParser;
import psyco.shardy.sqlparser.ISqlParser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lipeng on 16/2/18.
 */
public class TableMapping {
    public static Map<MappedStatement, String> map = new ConcurrentHashMap<>();

    public static String getTableName(MappedStatement mappedStatement, Object param) {
        String re = map.get(mappedStatement);
        if (re == null) {
            ISqlParser iSqlParser = new DruidSqlParser();
            try {
                SqlSource sqlSource = mappedStatement.getSqlSource();
                if (sqlSource instanceof ExtendedSqlSource) {
                    iSqlParser.init(((ExtendedSqlSource) sqlSource).getBoundSqlRaw(param).getSql());
                    re = iSqlParser.getTableName();
                    map.put(mappedStatement, re);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return re;
    }
}
