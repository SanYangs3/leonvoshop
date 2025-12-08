package com.lenovo.mall_backend.mapper;

import com.lenovo.mall_backend.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    Long count(String username);

    List<User> list(int start, Integer pageSize, String username);

    @Delete("delete from users where id = #{id}")
    void deleteById(Integer id);
}
