package com.lenovo.mall_backend.service;

import com.lenovo.mall_backend.entity.PageBean;

public interface UserService {
    PageBean page(Integer page, Integer pageSize, String username);

    void deleteById(Integer id);
}
