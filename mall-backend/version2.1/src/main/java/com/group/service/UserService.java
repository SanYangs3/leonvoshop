package com.group.service;

import com.group.entity.Ordered;
import com.group.entity.User;
import com.group.entity.UserIdentity;
import com.group.entity.vo.UserCountVO;

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

    Integer registerWithSms(String username, String password, String phone, String smsCode);

    User login(String loginName, String password, String smsCode);

    String getPasswordByUserId(Integer uid);

    void updateinfo(User user);

    User userLogin(String userName, String password);

    User adminLogin(String userName, String password);
}
