package com.group.controller;

import com.group.entity.Cart;
import com.group.entity.Result;
import com.group.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    /*
    根据用户id查询用户购物车
     */
    @GetMapping("/{uid}")
    public Result<List<Cart>> getCartByUid(@PathVariable String uid) {
        log.info("根据用户id查询用户购物车, uid: {}", uid);
        try {
            List<Cart> carts = cartService.getCartByUid(uid);
            return Result.success(carts);
        } catch (Exception e) {
            log.error("查询购物车失败，用户ID: {}, 异常信息: {}", uid, e.getMessage(), e);
            return Result.error("查询购物车失败");
        }
    }

    /*
    添加商品到购物车
     */
    @PostMapping("/add")
    public Result<Boolean> addToCart(@RequestBody AddToCartRequest request) {
        log.info("添加商品到购物车，用户ID: {}, 商品ID: {}, 商家ID: {}, 数量: {}",
                request.getUid(), request.getPid(), request.getBid(), request.getQuantity());
        try {
            boolean success = cartService.addToCart(
                    request.getUid(),
                    request.getPid(),
                    request.getBid(),
                    request.getQuantity()
            );
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            log.error("添加商品到购物车失败，异常信息: {}", e.getMessage(), e);
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /*
    根据购物车ID删除商品
     */
    @DeleteMapping("/delete/{cid}")
    public Result<Boolean> deleteByCid(@PathVariable Integer cid, @RequestParam String uid) {
        log.info("根据购物车ID删除商品，购物车项ID: {}, 用户ID: {}", cid, uid);
        try {
            boolean success = cartService.deleteByCid(uid, cid);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("删除失败，可能购物车项不存在或不属于该用户");
            }
        } catch (Exception e) {
            log.error("删除购物车商品失败，异常信息: {}", e.getMessage(), e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /*
    根据用户ID和商品ID删除购物车中的商品
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteByUidAndPid(@RequestParam String uid, @RequestParam Integer pid) {
        log.info("根据用户ID和商品ID删除购物车商品，用户ID: {}, 商品ID: {}", uid, pid);
        try {
            boolean success = cartService.deleteByUidAndPid(uid, pid);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("删除失败，商品不在购物车中");
            }
        } catch (Exception e) {
            log.error("删除购物车商品失败，异常信息: {}", e.getMessage(), e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /*
    清空购物车
     */
    @DeleteMapping("/clear/{uid}")
    public Result<Boolean> clearCart(@PathVariable String uid) {
        log.info("清空购物车，用户ID: {}", uid);
        try {
            boolean success = cartService.clearCart(uid);
            return Result.success(success);
        } catch (Exception e) {
            log.error("清空购物车失败，异常信息: {}", e.getMessage(), e);
            return Result.error("清空失败: " + e.getMessage());
        }
    }

    /*
    检查商品是否在购物车中
     */
    @GetMapping("/exists")
    public Result<Boolean> existsInCart(@RequestParam String uid, @RequestParam Integer pid) {
        log.info("检查商品是否在购物车中，用户ID: {}, 商品ID: {}", uid, pid);
        try {
            boolean exists = cartService.existsInCart(uid, pid);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查购物车商品失败，异常信息: {}", e.getMessage(), e);
            return Result.error("检查失败");
        }
    }

    /*
    更新购物车商品数量
     */
    @PutMapping("/update")
    public Result<Boolean> updateQuantity(@RequestBody UpdateQuantityRequest request) {
        log.info("更新购物车商品数量，用户ID: {}, 购物车项ID: {}, 新数量: {}",
                request.getUid(), request.getCid(), request.getQuantity());
        try {
            boolean success = cartService.updateQuantity(
                    request.getUid(),
                    request.getCid(),
                    request.getQuantity()
            );
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新购物车商品数量失败，异常信息: {}", e.getMessage(), e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    // 请求参数类
    static class AddToCartRequest {
        private String uid;
        private Integer pid;
        private Integer bid;
        private Integer quantity;

        // getters and setters
        public String getUid() { return uid; }
        public void setUid(String uid) { this.uid = uid; }
        public Integer getPid() { return pid; }
        public void setPid(Integer pid) { this.pid = pid; }
        public Integer getBid() { return bid; }
        public void setBid(Integer bid) { this.bid = bid; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    static class UpdateQuantityRequest {
        private String uid;
        private Integer cid;
        private Integer quantity;

        // getters and setters
        public String getUid() { return uid; }
        public void setUid(String uid) { this.uid = uid; }
        public Integer getCid() { return cid; }
        public void setCid(Integer cid) { this.cid = cid; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    /*
    从购物车创建订单
     */
    @PostMapping("/create-order/{uid}")
    public Result<Integer> createOrderFromCart(@PathVariable Integer uid,
                                               @RequestBody CreateOrderFromCartRequest request) {
        log.info("从购物车创建订单，用户ID: {}, 省份: {}, 收货人: {}",
                uid, request.getProvince(), request.getReceiverName());

        try {
            Integer orderId = cartService.createOrderFromCart(
                    uid,
                    request.getProvince(),
                    request.getReceiverAddress(),
                    request.getReceiverName(),
                    request.getReceiverPhone(),
                    request.getRemark()
            );
            return Result.success(orderId);
        } catch (Exception e) {
            log.error("从购物车创建订单失败，用户ID: {}, 错误: {}", uid, e.getMessage(), e);
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }

    // 内部请求类
    static class CreateOrderFromCartRequest {
        private String province;
        private String receiverAddress;
        private String receiverName;
        private String receiverPhone;
        private String remark;

        // getters and setters
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

        public String getReceiverAddress() { return receiverAddress; }
        public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }

        public String getReceiverName() { return receiverName; }
        public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

        public String getReceiverPhone() { return receiverPhone; }
        public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
}