package com.group.mapper;

import com.group.entity.User;
import com.group.entity.UserIdentity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {
    /*
    返回所有用户信息
     */
    @Select("select * from user where role = 'user'")
    List<User> getAllUser();

    /*
    用户注册，只输入用户名和密码
     */
    @Insert("insert into user (username, password, email, phone, status, avatar, role, create_time, update_time) VALUES " +
            "(#{username},#{password},#{email},#{phone},#{status},#{avatar},#{role},#{createTime},#{updateTime})")
    void insertUser(User user);

    /*
    根据用户id返回单个用户所有信息
     */
    @Select("select * from user where uid = #{id}")
    User getUserById(Long id);

    /*
    根据用户id修改用户信息
     */
    @Update("update user set username = #{username},password = #{password},email = #{email},phone = #{phone}," +
            "status = #{status},avatar = #{avatar},role = #{role},update_time = #{updateTime} where uid = #{uid}")
    void updateById(User user);

    /*
    根据用户id获取用户身份
     */
    @Select("select * from identity where uid = #{uid}")
    UserIdentity getUserIndentityById(Long id);

    /*
    通过用户id注销用户
     */
    @Update("update user set status = 0 where uid = #{id}")
    void updateStatus(Integer id);

    @Select("select count(*) from user where create_time between #{startTime} and #{endTime} and role = 'user'")
    Integer getAddCountToday(LocalDateTime startTime, LocalDateTime endTime);

    /*
    编辑用户身份
     */
    @Update("update user set role = #{role} where uid = #{uid}")
    void updateUserRole(Long uid, String role);

    @Select("select count(*) from user where phone = #{phone} and password = #{password}")
    Integer getByPhoneAndPass(String phone, String password);

    @Select("select count(*) from user where email = #{email} and password = #{password}")
    Integer getByEmailAndPass(String email, String password);

    @Select("select count(*) from user where username = #{username} and password = #{password}")
    Integer getByNameAndPass(String username, String password);

    @Select("select count(*) from user where username = #{username} and role = 'user'")
    Integer searchUsername(String username);

    @Select("select count(*) from user where phone = #{phone} and role = 'user'")
    Integer searchPhone(String phone);
}
