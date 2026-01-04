package com.group.service.impl;

import com.group.entity.Cart;
import com.group.entity.OrderItem;
import com.group.service.CartService;
import com.group.service.OrderedService;
import com.group.mapper.CartMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private OrderedService orderedService;  // 需要注入OrderedService
    /*
    根据用户id查询用户购物车
     */
    @Override
    public List<Cart> getCartByUid(String uid) {
        log.info("查询用户购物车，用户ID: {}", uid);
        return cartMapper.getCartByUid(uid);
    }

    /*
    根据用户id向购物车中加入商品
     */
    @Override
    @Transactional
    public boolean addToCart(String uid, Integer pid, Integer bid, Integer quantity) {
        try {
            if (quantity == null || quantity <= 0) {
                quantity = 1; // 默认数量为1
            }

            Cart cart = new Cart();
            cart.setUid(Integer.parseInt(uid)); // 注意：uid在Cart中是Integer类型
            cart.setPid(pid);
            cart.setBid(bid);
            cart.setQuantity(quantity);

            int result = cartMapper.addToCart(cart);

            if (result > 0) {
                log.info("成功添加商品到购物车，用户ID: {}, 商品ID: {}, 数量: {}", uid, pid, quantity);
                return true;
            } else {
                log.error("添加商品到购物车失败，用户ID: {}, 商品ID: {}", uid, pid);
                return false;
            }
        } catch (Exception e) {
            log.error("添加商品到购物车时发生异常，用户ID: {}, 商品ID: {}, 异常信息: {}", uid, pid, e.getMessage(), e);
            throw new RuntimeException("添加商品到购物车失败", e);
        }
    }

    /*
    根据购物车ID删除购物车的商品
     */
    @Override
    @Transactional
    public boolean deleteByCid(String uid, Integer cid) {
        try {
            // 首先验证该购物车项是否属于该用户
            List<Cart> cartItems = cartMapper.getCartByUid(uid);
            boolean belongsToUser = cartItems.stream()
                    .anyMatch(item -> item.getCid().equals(cid));

            if (!belongsToUser) {
                log.warn("购物车项不属于该用户，用户ID: {}, 购物车项ID: {}", uid, cid);
                return false;
            }

            int result = cartMapper.deleteByCid(cid);

            if (result > 0) {
                log.info("成功删除购物车商品，购物车项ID: {}", cid);
                return true;
            } else {
                log.error("删除购物车商品失败，购物车项ID: {}", cid);
                return false;
            }
        } catch (Exception e) {
            log.error("删除购物车商品时发生异常，购物车项ID: {}, 异常信息: {}", cid, e.getMessage(), e);
            throw new RuntimeException("删除购物车商品失败", e);
        }
    }

    /*
    根据用户ID和商品ID删除购物车的商品
     */
    @Override
    @Transactional
    public boolean deleteByUidAndPid(String uid, Integer pid) {
        try {
            int result = cartMapper.deleteByUidAndPid(uid, pid);

            if (result > 0) {
                log.info("成功删除购物车商品，用户ID: {}, 商品ID: {}", uid, pid);
                return true;
            } else {
                log.warn("购物车中未找到该商品，用户ID: {}, 商品ID: {}", uid, pid);
                return false;
            }
        } catch (Exception e) {
            log.error("删除购物车商品时发生异常，用户ID: {}, 商品ID: {}, 异常信息: {}", uid, pid, e.getMessage(), e);
            throw new RuntimeException("删除购物车商品失败", e);
        }
    }

    /*
    清空购物车
     */
    @Override
    @Transactional
    public boolean clearCart(String uid) {
        try {
            int result = cartMapper.clearCart(uid);
            log.info("成功清空购物车，用户ID: {}，删除条数: {}", uid, result);
            return true;
        } catch (Exception e) {
            log.error("清空购物车时发生异常，用户ID: {}, 异常信息: {}", uid, e.getMessage(), e);
            throw new RuntimeException("清空购物车失败", e);
        }
    }

    /*
    检查商品是否已在购物车中
     */
    @Override
    public boolean existsInCart(String uid, Integer pid) {
        try {
            int count = cartMapper.existsInCart(uid, pid);
            return count > 0;
        } catch (Exception e) {
            log.error("检查购物车商品时发生异常，用户ID: {}, 商品ID: {}, 异常信息: {}", uid, pid, e.getMessage(), e);
            return false;
        }
    }

    /*
    更新购物车商品数量
     */
    @Override
    @Transactional
    public boolean updateQuantity(String uid, Integer cid, Integer quantity) {
        try {
            // 验证数量
            if (quantity == null || quantity <= 0) {
                log.error("商品数量无效，数量: {}", quantity);
                return false;
            }

            // 验证购物车项是否属于该用户
            List<Cart> cartItems = cartMapper.getCartByUid(uid);
            boolean belongsToUser = cartItems.stream()
                    .anyMatch(item -> item.getCid().equals(cid));

            if (!belongsToUser) {
                log.warn("购物车项不属于该用户，用户ID: {}, 购物车项ID: {}", uid, cid);
                return false;
            }

            int result = cartMapper.updateQuantity(cid, quantity);

            if (result > 0) {
                log.info("成功更新购物车商品数量，购物车项ID: {}, 新数量: {}", cid, quantity);
                return true;
            } else {
                log.error("更新购物车商品数量失败，购物车项ID: {}", cid);
                return false;
            }
        } catch (Exception e) {
            log.error("更新购物车商品数量时发生异常，购物车项ID: {}, 异常信息: {}", cid, e.getMessage(), e);
            throw new RuntimeException("更新购物车商品数量失败", e);
        }
    }

    /*
    从购物车创建订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createOrderFromCart(Integer uid, String province,
                                       String receiverAddress, String receiverName,
                                       String receiverPhone, String remark) throws Exception {

        log.info("从购物车创建订单，用户ID: {}", uid);

        // 1. 查询用户购物车
        List<Cart> cartItems = cartMapper.getCartByUid(String.valueOf(uid));

        if (cartItems == null || cartItems.isEmpty()) {
            throw new Exception("购物车为空，无法创建订单");
        }

        log.info("购物车中找到 {} 件商品", cartItems.size());

        // 2. 将购物车商品转换为订单项
        List<OrderItem> orderItems = new ArrayList<>();

        for (Cart cart : cartItems) {
            OrderItem item = new OrderItem();
            item.setPid(cart.getPid());
            item.setQuantity(cart.getQuantity());
            item.setBid(cart.getBid());
            // 注意：这里不需要设置name和price，OrderedService会从数据库查询
            orderItems.add(item);

            log.info("购物车商品：商品ID={}, 数量={}, 商家ID={}",
                    cart.getPid(), cart.getQuantity(), cart.getBid());
        }

        // 3. 调用订单服务创建订单
        Integer orderId = orderedService.createOrder(uid, orderItems, province,
                receiverAddress, receiverName,
                receiverPhone, remark);

        log.info("订单创建成功，订单ID: {}", orderId);

        // 4. 清空购物车
        cartMapper.clearCart(String.valueOf(uid));
        log.info("已清空用户购物车，用户ID: {}", uid);

        return orderId;
    }
}