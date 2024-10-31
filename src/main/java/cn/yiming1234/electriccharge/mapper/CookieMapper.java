package cn.yiming1234.electriccharge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CookieMapper {

    @Select("select cookie from cookie order by id asc limit 1")
    String getCookie();

}
