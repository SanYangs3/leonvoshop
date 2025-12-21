// 创建新文件：BusinessProductMapper.java
package com.group.mapper;

import com.group.entity.Product;
import com.group.entity.BusinessProduct;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BusinessProductMapper {

    // 根据商家ID查询该商家的所有商品
    @Select("SELECT p.* FROM product p " +
            "INNER JOIN business_product bp ON p.pid = bp.pid " +
            "WHERE bp.bid = #{bid} " +
            "ORDER BY p.pid")
    List<Product> getProductsByBusinessId(@Param("bid") Integer bid);

    // 根据商家ID和商品ID查询单个商品（验证商品是否属于该商家）
    @Select("SELECT p.* FROM product p " +
            "INNER JOIN business_product bp ON p.pid = bp.pid " +
            "WHERE bp.bid = #{bid} AND p.pid = #{pid}")
    Product getProductByBusinessIdAndProductId(@Param("bid") Integer bid, @Param("pid") Integer pid);

    // 新增商家-商品关联记录（商家上传商品时调用）
    @Insert("INSERT INTO business_product (bid, pid) VALUES (#{bid}, #{pid})")
    void insertBusinessProductRelation(@Param("bid") Integer bid, @Param("pid") Integer pid);

    // 统计商家拥有的商品数量
    @Select("SELECT COUNT(*) FROM business_product WHERE bid = #{bid}")
    Integer countProductsByBusinessId(@Param("bid") Integer bid);
}