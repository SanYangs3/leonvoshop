package com.group.controller;

import com.group.entity.Cart;
import com.group.entity.Result;
import com.group.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    /*
    根据用户id查询用户购物车
     */
    @GetMapping("/{uid}")
    public Result<List<Cart>> getCartByUid(@PathVariable String uid){
        log.info("根据用户id查询用户购物车,uid:{}",uid);
        List<Cart> carts = cartService.getCartByUid(uid);
        return Result.success(carts);
    }
}
