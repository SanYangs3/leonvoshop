package com.group.mapper;

import com.group.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
    @Select("SELECT pid, name, price, stock, cpu, gpu, rating " +
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
}