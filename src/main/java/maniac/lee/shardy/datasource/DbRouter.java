package maniac.lee.shardy.datasource;

import java.lang.annotation.*;

/**
 * Created by peng on 16/2/29.
 */
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbRouter {
    String value();
}
