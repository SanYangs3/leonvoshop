package com.group.service;

import com.group.entity.Cart;

import java.util.List;

public interface CartService {

    /*
    根据用户id查询用户购物车
     */
    List<Cart> getCartByUid(String uid);

}
