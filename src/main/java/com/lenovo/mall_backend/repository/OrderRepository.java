package com.lenovo.mall_backend.repository;

import com.lenovo.mall_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // 根据订单号查找
    Order findByOrderNo(String orderNo);

    // 根据用户名查找
    List<Order> findByUserName(String userName);

    // 根据状态查找
    List<Order> findByStatus(String status);
}