package com.group.service;

import com.group.entity.Ordered;
import com.group.entity.User;
import com.group.entity.UserIdentity;

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
}
