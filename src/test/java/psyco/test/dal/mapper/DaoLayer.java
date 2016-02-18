package psyco.test.dal.mapper;

import org.apache.ibatis.annotations.*;
import psyco.test.dal.entity.User;

import java.util.List;

/**
 * Created by peng on 15/12/23.
 */
public interface DaoLayer {

    @Select("select * from User where id < #{idVar}")
    List<User> find(@Param("idVar") long id);

    List<User> findByIds(@Param("ids") List<Long> ids,@Param("role") String role);

    @Insert("insert into   User(id,name) values(#{id},#{name})")
    @SelectKey(statement = "select #{id}", keyProperty = "", before = false, resultType = long.class)
    long insert(User user);

    @Select("select * from User where name = #{name} ")
    List<User> findByName(@Param("name") String name);

    @Update("update User set level = #{level} where id = #{idVar}")
    int updateLevelById(@Param("idVar") long id, @Param("level") Integer level);

}
