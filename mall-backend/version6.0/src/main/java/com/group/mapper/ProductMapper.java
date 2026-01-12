package com.group.mapper;

import com.group.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    // 2. 获取所有商品数（统计总数）
    @Select("SELECT COUNT(*) FROM product")
    Integer countAllProducts();

    // 新增：获取今天商品数
    @Select("SELECT COUNT(*) FROM product WHERE DATE(create_time) = CURDATE()")
    Integer countTodayProducts();

    // 新增：获取昨天商品数
    @Select("SELECT COUNT(*) FROM product WHERE DATE(create_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)")
    Integer countYesterdayProducts();

    // 12. 获取所有商品信息
    //@Select("SELECT pid, name, price, stock, cpu, gpu, rating FROM product ORDER BY pid")
    //List<Product> getAllProducts();
    // 12.25修正
    @Select("SELECT pid, name, price, description, CPU as cpu, GPU as gpu, storage, size, type, picture, stock, rating " +
            "FROM product ORDER BY pid")
    List<Product> getAllProducts();

    // 根据ID查询商品
    @Select("SELECT * FROM product WHERE pid = #{pid}")
    Product getProductById(@Param("pid") Integer pid);

    // 10. 获取低库存商品数（库存 <= 10）
    @Select("SELECT COUNT(*) FROM product WHERE stock <= 10")
    Integer countLowStockProducts();

    // 获取低库存商品列表（库存 <= 10）
    @Select("SELECT pid, name, price, stock, picture FROM product WHERE stock <= 10 ORDER BY stock ASC")
    List<Product> getLowStockProducts();

    // 获取今日低库存商品数
    @Select("SELECT COUNT(*) FROM product WHERE stock <= 10")
    Integer countTodayLowStockProducts();

    // 新增：插入商品（商家上传商品）
    @Insert("INSERT INTO product (name, price, description, cpu, gpu, storage, size, type, picture, stock, rating, create_time) " +
            "VALUES (#{name}, #{price}, #{description}, #{cpu}, #{gpu}, #{storage}, #{size}, #{type}, #{picture}, #{stock}, #{rating}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "pid")
    void insertProduct(Product product);


    // 更新商品评分
    @Update("UPDATE product SET rating = rating + #{change} WHERE pid = #{pid}")
    void updateProductRating(@Param("pid") Integer pid, @Param("change") Integer change);

    // 根据ID获取当前评分（用于验证）
    @Select("SELECT rating FROM product WHERE pid = #{pid}")
    Integer getProductRating(@Param("pid") Integer pid);


    // 更新商品信息（商家修改商品）
    @Update("UPDATE product SET " +
            "name = #{name}, " +
            "price = #{price}, " +
            "description = #{description}, " +
            "cpu = #{cpu}, " +
            "gpu = #{gpu}, " +
            "storage = #{storage}, " +
            "size = #{size}, " +
            "type = #{type}, " +
            "picture = #{picture}, " +
            "stock = #{stock}, " +
            "rating = #{rating} " +
            "WHERE pid = #{pid}")
    void updateProduct(Product product);

    @Update("UPDATE product SET stock = stock - #{quantity} WHERE pid = #{pid} AND stock >= #{quantity}")
    int decreaseStock(@Param("pid") Integer pid, @Param("quantity") Integer quantity);

    @Update("UPDATE product SET stock = stock + #{quantity} WHERE pid = #{pid}")
    int increaseStock(@Param("pid") Integer pid, @Param("quantity") Integer quantity);

    // 获取商品信息
    @Select("SELECT * FROM product WHERE pid = #{pid}")
    Product selectById(Integer pid);

    // 更新商品评分（平均分）
    @Update("UPDATE product SET rating = #{rating} WHERE pid = #{pid}")
    int updateRating(@Param("pid") Integer pid, @Param("rating") Integer rating);

    // 计算商品平均评分
    @Select("SELECT COALESCE(AVG(star), 0) FROM feedback WHERE pid = #{pid}")
    double calculateAverageRating(@Param("pid") Integer pid);

}