package com.group.controller;

import com.group.entity.Result;
import com.group.entity.Product;
import com.group.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business/products")
public class BusinessProductController {

    @Autowired
    private ProductService productService;

    /*
    需求1：商家获取自己管辖范围内所有商品
     */
    @GetMapping("/my/{bid}")
    public Result<List<Product>> getMyProducts(@PathVariable Integer bid) {
        try {
            List<Product> products = productService.getProductsByBusinessId(bid);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error("获取商品列表失败：" + e.getMessage());
        }
    }

    /*
    需求2：通过商品id获取单个商品信息（商家只能查看自己的商品）
     */
    @GetMapping("/my/{bid}/{pid}")
    public Result<Product> getMyProductById(@PathVariable Integer bid, @PathVariable Integer pid) {
        try {
            Product product = productService.getProductByBusinessIdAndProductId(bid, pid);
            if (product == null) {
                return Result.error("商品不存在或不属于该商家");
            }
            return Result.success(product);
        } catch (Exception e) {
            return Result.error("获取商品信息失败：" + e.getMessage());
        }
    }

    /*
    需求3：商家上传商品（包括所有商品信息，默认评分是100）
     */
    @PostMapping("/upload/{bid}")
    public Result<String> uploadProduct(@PathVariable Integer bid, @RequestBody Product product) {
        try {
            // 设置商家ID（用于建立关联关系）
            product.setBid(bid);
            productService.uploadProduct(product);
            return Result.success("商品上传成功，商品ID：" + product.getPid());
        } catch (Exception e) {
            return Result.error("商品上传失败：" + e.getMessage());
        }
    }


    /*
    功能11：返回商家库存小于等于5的所有商品
     */
    @GetMapping("/my/{bid}/low-stock")
    public Result<List<Product>> getMyLowStockProducts(@PathVariable Integer bid) {
        try {
            List<Product> products = productService.getLowStockProductsByBusinessId(bid);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error("获取低库存商品失败：" + e.getMessage());
        }
    }

    /*
    获取商家低库存商品数量
     */
    @GetMapping("/my/{bid}/low-stock/count")
    public Result<Integer> countMyLowStockProducts(@PathVariable Integer bid) {
        try {
            Integer count = productService.countLowStockProductsByBusinessId(bid);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("获取低库存商品数量失败：" + e.getMessage());
        }
    }


    /*
    功能12：通过商品id修改商品的信息（商家只能修改自己的商品）
     */
    @PutMapping("/update/{bid}")
    public Result<String> updateProduct(@PathVariable Integer bid, @RequestBody Product product) {
        try {
            // 验证必要字段
            if (product.getPid() == null) {
                return Result.error("商品ID不能为空");
            }

            boolean success = productService.updateProduct(bid, product);
            if (success) {
                return Result.success("商品信息修改成功");
            } else {
                return Result.error("商品不存在或不属于该商家");
            }
        } catch (Exception e) {
            return Result.error("商品信息修改失败：" + e.getMessage());
        }
    }
}