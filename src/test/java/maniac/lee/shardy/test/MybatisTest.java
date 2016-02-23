package maniac.lee.shardy.test;

import com.google.common.collect.Lists;
import maniac.lee.shardy.test.dal.entity.User;
import maniac.lee.shardy.test.dal.mapper.DaoLayer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Iterator;

/**
 * Created by peng on 16/2/2.
 */
@SpringBootApplication
@ComponentScan("maniac.lee.shardy")
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
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

    @Test
    public void testBatch() {
        //        print(daoLayer.findByIds(Lists.newArrayList(10000001l,23l,10000000l),"USER"));
        print(daoLayer.findByIds(Lists.newArrayList(25l, 23l, 10000002l), "USER"));
    }

    @Test
    public void update() {
        print(daoLayer.updateLevelById(10000002l, 3));
    }

    @Test
    public void updates() {
        print(daoLayer.updateLevelByIds(Lists.newArrayList(25l, 23l, 10000002l), 160, "USER"));
    }

    @Test
    public void name() {
        print(daoLayer.findByName("shard_test"));
        print(daoLayer.findByName("fuck"));
    }
    @Test
    public void names() {
        print(daoLayer.findByNames(Lists.newArrayList("shard_test","fuck")));
    }


    @Test
    public void insert() {
        User user = new User();
//        user.setId(10000000 + 8);
        user.setId(59);
        user.setName("test");
        System.out.println("re - > " + daoLayer.insert(user));
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
