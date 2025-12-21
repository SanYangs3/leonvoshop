package com.group.service;

import com.group.entity.Product;
import java.util.List;

public interface ProductService {
    /*
    返回所有商品信息（系统角度）
     */
    List<Product> getAllProducts();

    /*
    根据商品id返回单个商品所有信息（系统角度）
     */
    Product getProductById(Integer id);

    /*
    获取所有商品数
     */
    Integer countAllProducts();

    /*
    获取今天商品数
     */
    Integer countTodayProducts();

    /*
    获取昨天商品数
     */
    Integer countYesterdayProducts();

    /*
   获取低库存商品数（库存 <= 10）
    */
    Integer countLowStockProducts();

    /*
    获取低库存商品列表
     */
    List<Product> getLowStockProducts();

    /*
    新增：商家上传商品
     */
    void uploadProduct(Product product);

    /*
    新增：根据商家ID获取该商家的所有商品
     */
    List<Product> getProductsByBusinessId(Integer bid);

    /*
    新增：根据商家ID和商品ID获取该商家的单个商品
     */
    Product getProductByBusinessIdAndProductId(Integer bid, Integer pid);

    /*
    新增：根据评论更新商品评分
     */
    void updateRatingByFeedback(Integer pid, Integer star);

    /*
    新增：商家花钱增加商品评分
     */
    boolean increaseRatingByPayment(Integer bid, Integer pid, Integer amount);
}