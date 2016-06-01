package maniac.lee.shardy.core;

import maniac.lee.shardy.core.wrap.AbstractPrepareStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by peng on 16/6/1.
 */
public class ShardyPrepareStatement extends AbstractPrepareStatement {
    @Override
    public ResultSet executeQuery() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate() throws SQLException {
        return 0;
    }

    @Override
    public boolean execute() throws SQLException {
        return false;
    }
}
