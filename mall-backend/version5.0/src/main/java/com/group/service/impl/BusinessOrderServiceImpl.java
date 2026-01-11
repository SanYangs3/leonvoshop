package com.group.service.impl;

import com.group.entity.Ordered;
import com.group.mapper.BusinessOrderMapper;
import com.group.service.BusinessOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusinessOrderServiceImpl implements BusinessOrderService {

    @Autowired
    private BusinessOrderMapper businessOrderMapper;

    @Override
    public List<Ordered> getOrdersByBusinessId(Integer bid) {
        return businessOrderMapper.getOrdersByBusinessId(bid);
    }

    @Override
    @Transactional
    public boolean shipOrder(Integer bid, Integer oid) {
        // 1. 验证订单是否属于该商家
        boolean belongs = businessOrderMapper.isOrderBelongsToBusiness(oid, bid);
        if (!belongs) {
            return false; // 订单不属于该商家
        }

        // 2. 验证订单状态是否为0（待发货）
        Integer status = businessOrderMapper.getOrderStatus(oid);
        if (status == null || status != 0) {
            return false; // 订单不存在或不是待发货状态
        }

        // 3. 更新状态为1（已发货）
        int updated = businessOrderMapper.updateOrderToShipped(oid);
        return updated > 0; // 返回是否更新成功
    }

    @Override
    public List<Ordered> getPendingOrdersByBusinessId(Integer bid) {
        return businessOrderMapper.getPendingOrdersByBusinessId(bid);
    }
}