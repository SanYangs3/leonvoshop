package com.group.service;

import com.group.entity.Cart;

import java.util.List;

public interface CartService {

    /*
    根据用户id查询用户购物车
     */
    List<Cart> getCartByUid(String uid);


    /*
    根据用户id向购物车中加入商品
     */
    boolean addToCart(String uid, Integer pid, Integer bid, Integer quantity);

    /*
    根据购物车ID删除购物车的商品
     */
    boolean deleteByCid(String uid, Integer cid);

    /*
    根据用户ID和商品ID删除购物车的商品
     */
    boolean deleteByUidAndPid(String uid, Integer pid);

    /*
    清空购物车
     */
    boolean clearCart(String uid);

    /*
    检查商品是否已在购物车中
     */
    boolean existsInCart(String uid, Integer pid);

    /*
    更新购物车商品数量
     */
    boolean updateQuantity(String uid, Integer cid, Integer quantity);

    /*
   从购物车创建订单
    */
    Integer createOrderFromCart(Integer uid, String province,
                                String receiverAddress, String receiverName,
                                String receiverPhone, String remark) throws Exception;


}
