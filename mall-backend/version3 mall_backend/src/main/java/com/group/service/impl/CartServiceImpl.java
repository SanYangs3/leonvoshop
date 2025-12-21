package com.group.service.impl;

import com.group.entity.Cart;
import com.group.mapper.CartMapper;
import com.group.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;

    /*
    根据用户id查询用户购物车
     */

    @Override
    public List<Cart> getCartByUid(String uid) {
        return cartMapper.getCartByUid(uid);
    }
}
