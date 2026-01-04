package com.group.mapper;

import com.group.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper {
    /*
    根据用户id查询用户购物车
     */
    @Select("select * from cart where uid = #{uid}")
    List<Cart> getCartByUid(String uid);
}
