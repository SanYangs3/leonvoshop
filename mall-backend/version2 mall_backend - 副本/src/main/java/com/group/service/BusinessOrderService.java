//商家订单服务接口
package com.group.service;

import com.group.entity.Ordered;
import java.util.List;

public interface BusinessOrderService {

    /*
    获取商家的所有订单（不显示status=3的订单）
     */
    List<Ordered> getOrdersByBusinessId(Integer bid);

    /*
    商家发货（改变status=1）
     */
    boolean shipOrder(Integer bid, Integer oid);

    /*
    获取商家待发货订单（status=0）
     */
    List<Ordered> getPendingOrdersByBusinessId(Integer bid);
}