package com.lenovo.mall_backend.repository;

import com.lenovo.mall_backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 根据商品名称模糊搜索（不分页）
    List<Product> findByNameContaining(String keyword);

    // 根据商品名称模糊搜索（分页）- 新增
    Page<Product> findByNameContaining(String keyword, Pageable pageable);
}