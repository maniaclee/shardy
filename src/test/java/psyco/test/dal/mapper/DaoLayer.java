package psyco.test.dal.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import psyco.test.dal.entity.User;

import java.util.List;

/**
 * Created by peng on 15/12/23.
 */
public interface DaoLayer {

    @Select("select * from User where id < #{id}")
    List<User> find(@Param("id")long id);

    @Select("select * from User where name = #{name} ")
    List<User> findByName(@Param("name") String name);

    @Update("update User set level = #{level} where id = #{id}")
    int updateLevelById(@Param("id") long id, @Param("level") Integer level);

}
