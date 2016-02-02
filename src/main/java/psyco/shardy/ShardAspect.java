package psyco.shardy;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Created by peng on 16/2/2.
 */
@Aspect
public class ShardAspect {

    @Around("sdf")
    public void test(){

    }
}
