package com.group.service;

import com.group.entity.Product;
import java.util.List;

public interface ProductService {
    /*
    返回所有商品信息
     */
    List<Product> getAllProducts();

    /*
    根据商品id返回单个商品所有信息
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
}