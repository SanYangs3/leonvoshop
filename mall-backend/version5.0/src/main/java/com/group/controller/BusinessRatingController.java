// 商家评分控制器
package com.group.controller;

import com.group.entity.PaymentRequest;
import com.group.entity.Result;
import com.group.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business/rating")
public class BusinessRatingController {

    @Autowired
    private ProductService productService;

    /*
    功能7：商家花钱自己增加商品评分
    规则：200块钱+5，500块钱+15，1000块钱+40
     */
    @PostMapping("/increase")
    public Result<String> increaseRating(@RequestBody PaymentRequest request) {
        try {
            boolean success = productService.increaseRatingByPayment(
                    request.getBid(),
                    request.getPid(),
                    request.getAmount()
            );

            if (success) {
                return Result.success("评分增加成功，已扣除" + request.getAmount() + "元");
            } else {
                return Result.error("评分增加失败：商品不存在、不属于您或金额不正确");
            }
        } catch (Exception e) {
            return Result.error("评分增加失败：" + e.getMessage());
        }
    }
}