package com.group.controller;

import com.group.entity.Result;
import com.group.entity.Product;
import com.group.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /*
    返回所有商品信息
     */
    @GetMapping
    public Result<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return Result.success(products);
    }

    /*
    根据商品id返回单个商品所有信息
     */
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    /*
    获取所有商品数
     */
    @GetMapping("/count")
    public Result<Integer> getProductCount() {
        Integer count = productService.countAllProducts();
        return Result.success(count);
    }

    /*
    获取今天商品数
     */
    @GetMapping("/today-count")
    public Result<Integer> getTodayProductCount() {
        Integer count = productService.countTodayProducts();
        return Result.success(count);
    }

    /*
    获取昨天商品数
     */
    @GetMapping("/yesterday-count")
    public Result<Integer> getYesterdayProductCount() {
        Integer count = productService.countYesterdayProducts();
        return Result.success(count);
    }

    /*
    获取低库存商品数（库存 <= 10）
     */
    @GetMapping("/low-stock/count")
    public Result<Integer> countLowStockProducts() {
        Integer count = productService.countLowStockProducts();
        return Result.success(count);
    }

    /*
    获取低库存商品列表
     */
    @GetMapping("/low-stock")
    public Result<List<Product>> getLowStockProducts() {
        List<Product> products = productService.getLowStockProducts();
        return Result.success(products);
    }
}