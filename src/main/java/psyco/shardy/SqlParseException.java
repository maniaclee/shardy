package psyco.shardy;

/**
 * Created by lipeng on 16/2/5.
 */
public class SqlParseException extends Exception {
    public SqlParseException(String message) {
        super(message);
    }

    public SqlParseException(Throwable cause) {
        super(cause);
    }
}
