package com.lenovo.mall_backend.repository;

import com.lenovo.mall_backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    // 根据订单ID查找所有订单项
    List<OrderItem> findByOrderId(Integer orderId);

    // 根据订单ID删除所有订单项
    void deleteByOrderId(Integer orderId);
}