package com.lenovo.mall_backend.service.impl;

import com.lenovo.mall_backend.entity.PageBean;
import com.lenovo.mall_backend.entity.User;
import com.lenovo.mall_backend.mapper.UserMapper;
import com.lenovo.mall_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public PageBean page(Integer page, Integer pageSize, String username) {
        Long count = userMapper.count(username);
        List<User> empList = userMapper.list((page-1)*pageSize, pageSize, username);
        PageBean pageBean = new PageBean(count,empList);
        return pageBean;
    }

    @Override
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }
}
