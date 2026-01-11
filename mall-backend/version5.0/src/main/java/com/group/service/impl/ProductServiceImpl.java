package com.group.service.impl;

import com.group.entity.Product;
import com.group.mapper.ProductMapper;
import com.group.mapper.BusinessProductMapper;
import com.group.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private BusinessProductMapper businessProductMapper;

    @Override
    public List<Product> getAllProducts() {
        return productMapper.getAllProducts();
    }

    @Override
    public Product getProductById(Integer id) {
        return productMapper.getProductById(id);
    }

    @Override
    public Integer countAllProducts() {
        return productMapper.countAllProducts();
    }

    @Override
    public Integer countTodayProducts() {
        return productMapper.countTodayProducts();
    }

    @Override
    public Integer countYesterdayProducts() {
        return productMapper.countYesterdayProducts();
    }

    @Override
    public Integer countLowStockProducts() {
        return productMapper.countLowStockProducts();
    }

    @Override
    public List<Product> getLowStockProducts() {
        return productMapper.getLowStockProducts();
    }

    @Override
    @Transactional
    public void uploadProduct(Product product) {
        product.setRating(100);
        productMapper.insertProduct(product);
        if (product.getBid() != null) {
            businessProductMapper.insertBusinessProductRelation(product.getBid(), product.getPid());
        }
    }

    @Override
    public List<Product> getProductsByBusinessId(Integer bid) {
        return businessProductMapper.getProductsByBusinessId(bid);
    }

    @Override
    public Product getProductByBusinessIdAndProductId(Integer bid, Integer pid) {
        return businessProductMapper.getProductByBusinessIdAndProductId(bid, pid);
    }

    @Override
    @Transactional
    public void updateRatingByFeedback(Integer pid, Integer star) {
        Integer change = 0;
        switch (star) {
            case 5: change = 10; break;
            case 4: change = 5; break;
            case 3: change = 0; break;
            case 2: change = -5; break;
            case 1: change = -10; break;
            default: return;
        }

        if (change != 0) {
            productMapper.updateProductRating(pid, change);
        }
    }

    @Override
    @Transactional
    public boolean increaseRatingByPayment(Integer bid, Integer pid, Integer amount) {
        // 验证商品是否属于该商家
        Product product = businessProductMapper.getProductByBusinessIdAndProductId(bid, pid);
        if (product == null) {
            return false;
        }

        // 根据金额确定评分增加量
        Integer change = 0;
        if (amount == 200) {
            change = 5;
        } else if (amount == 500) {
            change = 15;
        } else if (amount == 1000) {
            change = 40;
        } else {
            return false;
        }

        // 更新商品评分
        productMapper.updateProductRating(pid, change);
        return true;
    }



    @Override
    public List<Product> getLowStockProductsByBusinessId(Integer bid) {
        return businessProductMapper.getLowStockProductsByBusinessId(bid);
    }

    @Override
    public Integer countLowStockProductsByBusinessId(Integer bid) {
        return businessProductMapper.countLowStockProductsByBusinessId(bid);
    }

    @Override
    @Transactional
    public boolean updateProduct(Integer bid, Product product) {
        // 1. 验证商品是否属于该商家
        Product existingProduct = businessProductMapper.getProductByBusinessIdAndProductId(bid, product.getPid());
        if (existingProduct == null) {
            return false; // 商品不存在或不属于该商家
        }

        // 2. 更新商品信息
        productMapper.updateProduct(product);
        return true;
    }
}