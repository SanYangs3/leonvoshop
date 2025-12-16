package com.group.service.impl;

import com.group.entity.Product;
import com.group.mapper.ProductMapper;
import com.group.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

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
}