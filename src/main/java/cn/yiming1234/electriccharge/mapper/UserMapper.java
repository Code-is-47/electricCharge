package cn.yiming1234.electriccharge.mapper;

import cn.yiming1234.electriccharge.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Insert("insert into user(room,charge,create_time) values(#{room}, #{charge}, #{createTime})")
    void insert(User user);

    @Select("select * from user where room = #{room}")
    User getByRoom(String room);

    @Select("select create_time from user where phone = #{phone}")
    String getCreateTime(String phone);

    @Update("update user set create_time = #{createTime} where phone = #{phone}")
    void updateCreateTime(User user);

    @Update("update user set charge = #{charge} where room = #{room}")
    void updateChargeByRoom(String room, String charge);

}