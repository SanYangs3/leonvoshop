package com.lenovo.mall_backend.repository;

import com.lenovo.mall_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // 根据用户ID查找订单
    List<Order> findByUserId(Long userId);

    // 根据订单状态查找订单
    List<Order> findByStatus(String status);

    // 根据用户ID和状态查找订单
    List<Order> findByUserIdAndStatus(Long userId, String status);

    // 根据订单编号查找订单
    Optional<Order> findByOrderNumber(String orderNumber);

    // 根据订单编号模糊搜索
    List<Order> findByOrderNumberContaining(String orderNumber);

    // 根据收货人姓名查找订单
    List<Order> findByReceiverNameContaining(String receiverName);

    // 根据收货人电话查找订单
    List<Order> findByReceiverPhone(String receiverPhone);

    // 根据订单时间范围查找订单
    @Query("SELECT o FROM Order o WHERE o.orderTime BETWEEN :startTime AND :endTime")
    List<Order> findByOrderTimeBetween(@Param("startTime") String startTime, @Param("endTime") String endTime);

    // 根据用户ID和订单时间范围查找订单
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.orderTime BETWEEN :startTime AND :endTime")
    List<Order> findByUserIdAndOrderTimeBetween(@Param("userId") Long userId,
                                                @Param("startTime") String startTime,
                                                @Param("endTime") String endTime);

    // 根据订单金额范围查找订单
    @Query("SELECT o FROM Order o WHERE CAST(o.amount AS double) BETWEEN :minAmount AND :maxAmount")
    List<Order> findByAmountBetween(@Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount);

    // 根据用户ID查找最近的订单
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.orderTime DESC")
    List<Order> findRecentOrdersByUserId(@Param("userId") Long userId);

    // 统计用户订单总数
    long countByUserId(Long userId);

    // 统计不同状态的订单数量
    long countByStatus(String status);

    // 统计总销售额
    @Query("SELECT SUM(CAST(o.amount AS double)) FROM Order o WHERE o.status IN ('1', '2', '3')")
    Double sumTotalSales();

    // 统计今日订单数
    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.orderTime) = CURRENT_DATE")
    long countTodayOrders();

    // 统计今日销售额
    @Query("SELECT SUM(CAST(o.amount AS double)) FROM Order o WHERE DATE(o.orderTime) = CURRENT_DATE AND o.status IN ('1', '2', '3')")
    Double sumTodaySales();

    // 查找包含特定商品的订单
    @Query("SELECT o FROM Order o WHERE o.gwc LIKE %:productName%")
    List<Order> findByProductName(@Param("productName") String productName);

    // 根据多个状态查找订单
    List<Order> findByStatusIn(List<String> statuses);

    // 根据用户ID和多个状态查找订单
    List<Order> findByUserIdAndStatusIn(Long userId, List<String> statuses);
}