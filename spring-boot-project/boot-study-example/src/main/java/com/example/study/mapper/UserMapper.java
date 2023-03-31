package com.example.study.mapper;

import com.example.study.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author user
 */
@Mapper
public interface UserMapper {

  @Select("select * from users")
  List<User> list();

  @Update("update users set name = #{name} where id = #{id}")
  void updateUser(@Param("name") String name, @Param("id") int id);

}
