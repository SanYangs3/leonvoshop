package com.group.controller;

import com.group.entity.Result;
import com.group.entity.Business;
import com.group.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    /*
    返回所有商家信息
     */
    @GetMapping
    public Result<List<Business>> getAllBusinesses() {
        List<Business> businesses = businessService.getAllBusinesses();
        return Result.success(businesses);
    }
}