package psyco.test;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import psyco.test.dal.mapper.DaoLayer;

import javax.annotation.Resource;
import java.util.Iterator;

/**
 * Created by peng on 16/2/2.
 */
@SpringBootApplication
@ComponentScan("psyco")
@RunWith(SpringJUnit4ClassRunner.class)
//@EnableAutoConfiguration
@EnableAspectJAutoProxy
@ContextConfiguration(classes = {MybatisTest.class})
public class MybatisTest {

    @Resource
    private DaoLayer daoLayer;

    @Test
    public void test() {
        print(daoLayer.find(10000001));
        print(daoLayer.find(32));
    }

    private void print(Object p) {
        if (p instanceof Iterable) {
            Iterator i = ((Iterable) p).iterator();
            if (i.hasNext())
                while (i.hasNext())
                    System.out.println(ToStringBuilder.reflectionToString(i.next()));
            else {
                System.out.println(p);
            }
        } else {
            System.out.println(ToStringBuilder.reflectionToString(p));
        }
    }
}
