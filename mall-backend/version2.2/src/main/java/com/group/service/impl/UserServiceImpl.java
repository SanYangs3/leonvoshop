package com.group.service.impl;

import com.group.entity.User;
import com.group.entity.UserIdentity;
import com.group.entity.vo.UserCountVO;
import com.group.mapper.UserMapper;
import com.group.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /*
    返回所有用户信息
     */
    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }

    /*
    用户注册，只输入用户名和密码
     */
    @Override
    public void insertUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insertUser(user);
    }

    /*
    根据用户id返回单个用户所有信息
     */
    @Override
    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    /*
    根据用户id修改用户信息
     */
    @Override
    public void updateById(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /*
    根据用户id获取用户身份
     */
    @Override
    public UserIdentity getUserIndentityById(Long id) {
        return userMapper.getUserIndentityById(id);
    }

    /*
    通过用户id注销用户
     */
    @Override
    public void updateStatus(Integer id) {
        userMapper.updateStatus(id);
    }

    /*
    获取今日新增用户数
     */

    @Override
    public Integer getAddCountToday(LocalDateTime startTime, LocalDateTime endTime) {
        return userMapper.getAddCountToday(startTime,endTime);
    }

    /*
    编辑用户身份
     */

    @Override
    public void updateUserRole(Long uid, String role) {
        userMapper.updateUserRole(uid,role);
    }

    @Override
    public UserCountVO getCompare() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();  // 今天00:00:00
        LocalDateTime todayEnd = LocalDateTime.now();     // 当前时间

        Integer countToday = userMapper.getAddCountToday(todayStart,todayEnd);
        // 获取昨天的时间范围
        LocalDate yesterday = today.minusDays(1);
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();  // 昨天00:00:00
        LocalDateTime yesterdayEnd = yesterday.atTime(23, 59, 59);

        Integer countYesterday = userMapper.getAddCountToday(yesterdayStart,yesterdayEnd);

        UserCountVO userCountVO = new UserCountVO(countToday,countYesterday);
        return userCountVO;
    }

    @Override
    public Integer getByPhoneAndPass(String phone, String password) {
        return userMapper.getByPhoneAndPass(phone, password);
    }

    @Override
    public Integer getByEmailAndPass(String email, String password) {
        return userMapper.getByEmailAndPass(email, password);
    }

    @Override
    public Integer getByNameAndPass(String username, String password) {
        return userMapper.getByNameAndPass(username,password);
    }

    @Override
    public Integer searchUsername(String username) {
        return userMapper.searchUsername(username);
    }

    @Override
    public Integer searchPhone(String phone) {
        return userMapper.searchPhone(phone);
    }
}