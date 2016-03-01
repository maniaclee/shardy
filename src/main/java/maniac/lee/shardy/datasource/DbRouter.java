package maniac.lee.shardy.datasource;

import java.lang.annotation.*;

/**
 * Created by peng on 16/2/29.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbRouter {
    /**
     * db
     */
    String value() default "";

    String useTable() default "";
}
