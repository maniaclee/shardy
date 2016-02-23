package maniac.lee.shardy;

/**
 * Created by lipeng on 16/2/5.
 */
public class ShardException extends RuntimeException {
    public ShardException(String message) {
        super(message);
    }

    public ShardException(Throwable cause) {
        super(cause);
    }
}
