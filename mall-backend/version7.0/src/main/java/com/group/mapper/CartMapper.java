package com.group.mapper;

import com.group.entity.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {
    /*
    根据用户id查询用户购物车
     */
    @Select("select * from cart where uid = #{uid}")
    List<Cart> getCartByUid(String uid);

    /*
    加入商品到购物车
     */
    @Insert("INSERT INTO cart (uid, pid, bid, quantity) VALUES (#{uid}, #{pid}, #{bid}, #{quantity}) " +
            "ON DUPLICATE KEY UPDATE quantity = quantity + #{quantity}")
    int addToCart(Cart cart);

    /*
    根据购物车ID删除商品
     */
    @Delete("DELETE FROM cart WHERE cid = #{cid}")
    int deleteByCid(Integer cid);

    /*
    根据用户ID和商品ID删除购物车中的特定商品
     */
    @Delete("DELETE FROM cart WHERE uid = #{uid} AND pid = #{pid}")
    int deleteByUidAndPid(@Param("uid") String uid, @Param("pid") Integer pid);

    /*
    根据用户ID清空购物车
     */
    @Delete("DELETE FROM cart WHERE uid = #{uid}")
    int clearCart(String uid);

    /*
    检查购物车中是否已存在该商品
     */
    @Select("SELECT COUNT(*) FROM cart WHERE uid = #{uid} AND pid = #{pid}")
    int existsInCart(@Param("uid") String uid, @Param("pid") Integer pid);

    /*
    更新购物车商品数量
     */
    @Update("UPDATE cart SET quantity = #{quantity} WHERE cid = #{cid}")
    int updateQuantity(@Param("cid") Integer cid, @Param("quantity") Integer quantity);
}