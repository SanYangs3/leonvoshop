package com.group.service.impl;

import com.group.entity.User;
import com.group.entity.UserIdentity;
import com.group.mapper.UserMapper;
import com.group.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}