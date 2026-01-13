package com.group.mapper;

import com.group.entity.DTO.DailyUserGrowthDTO;
import com.group.entity.User;
import com.group.entity.UserIdentity;
import org.apache.ibatis.annotations.*;

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

    @Select("select count(*) from user where email = #{email} and role = 'user'")
    Integer searchEmail(String email);

    @Update("update user set password = #{password}, update_time = #{updateTime} where email = #{email}")
    int updatePasswordByEmail(@Param("email") String email,
                              @Param("password") String password,
                              @Param("updateTime") LocalDateTime updateTime);

    @Update("update user set password = #{password}, update_time = #{updateTime} where phone = #{phone}")
    int updatePasswordByPhone(@Param("phone") String phone,
                              @Param("password") String password,
                              @Param("updateTime") LocalDateTime updateTime);

    @Update("update user set email = #{email}, update_time = #{updateTime} where uid = #{uid}")
    int updateEmailByUid(@Param("uid") Long uid,
                         @Param("email") String email,
                         @Param("updateTime") LocalDateTime updateTime);

    @Update("update user set phone = #{phone}, update_time = #{updateTime} where uid = #{uid}")
    int updatePhoneByUid(@Param("uid") Long uid,
                         @Param("phone") String phone,
                         @Param("updateTime") LocalDateTime updateTime);

    @Select("SELECT " +
            "DATE(create_time) as date, " +
            "COUNT(*) as userCount " +
            "FROM user " +
            "WHERE DATE(create_time) BETWEEN #{startDate} AND #{endDate} " +
            "AND status = 1 " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY DATE(create_time)")
    List<DailyUserGrowthDTO> selectDailyUserGrowth(@Param("startDate") String startDate,
                                                   @Param("endDate") String endDate);

    /**
     * 根据用户ID增加积分（确认收货时调用，积分与订单金额1:1）
     */
    @Update("UPDATE identity SET points = points + #{points} WHERE uid = #{uid}")
    int addPointsByUid(@Param("uid") Integer uid, @Param("points") Integer points);
}
