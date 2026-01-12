package com.group.service;

import com.group.entity.DTO.DailyUserGrowthDTO;
import com.group.entity.DTO.UserGrowthTrendDTO;
import com.group.entity.Ordered;
import com.group.entity.User;
import com.group.entity.UserIdentity;
import com.group.entity.vo.UserCountVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    /*
    返回所有用户信息
     */
    List<User> getAllUser();

    /*
    用户注册，只输入用户名和密码
     */
    void insertUser(User user);

    /*
    根据用户id返回单个用户所有信息
     */
    User getUserById(Long id);

    /*
    根据用户id修改用户信息
     */
    void updateById(User user);

    /*
    根据用户id获取用户身份
     */
    UserIdentity getUserIndentityById(Long id);


    /*
    通过用户id注销用户
     */
    void updateStatus(Integer id);

    /*
    获取今日新增用户数
     */
    Integer getAddCountToday(LocalDateTime startTime, LocalDateTime endTime);

    /*
    编辑用户身份
     */
    void updateUserRole(Long uid, String role);


    UserCountVO getCompare();

    Integer getByPhoneAndPass(String phone, String password);

    Integer getByEmailAndPass(String email, String password);

    Integer getByNameAndPass(String username, String password);

    Integer searchUsername(String username);

    Integer searchPhone(String phone);

    Integer searchEmail(String email);

    void updatePasswordByEmail(String email, String password);

    void updatePasswordByPhone(String phone, String password);



    /**
     * 获取用户增长趋势（本周和本月每日数据）
     * @return UserGrowthTrendDTO
     */
    UserGrowthTrendDTO getUserGrowthTrend();

    /**
     * 获取指定日期范围内的每日用户增长
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日增长列表
     */
    List<DailyUserGrowthDTO> getDailyGrowthBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 获取本周每日用户增长
     * @return 本周每日增长列表
     */
    List<DailyUserGrowthDTO> getWeeklyGrowth();

    /**
     * 获取本月每日用户增长
     * @return 本月每日增长列表
     */
    List<DailyUserGrowthDTO> getMonthlyGrowth();
}
