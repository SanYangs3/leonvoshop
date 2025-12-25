package com.group.controller;

import com.group.entity.Result;
import com.group.entity.Business;
import com.group.entity.BusinessFullInfo;
import com.group.entity.BusinessLoginRequest;
import com.group.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    /*
    返回所有商家信息（基本信息）
     */
    @GetMapping
    public Result<List<Business>> getAllBusinesses() {
        List<Business> businesses = businessService.getAllBusinesses();
        return Result.success(businesses);
    }

    /*
    功能8完善版：通过商家登录的bname和password获取商家的所有信息（完整信息）
     */
    @PostMapping("/login-full")
    public Result<BusinessFullInfo> loginWithFullInfo(@RequestBody BusinessLoginRequest request) {
        BusinessFullInfo business = businessService.loginWithFullInfo(request.getBname(), request.getPassword());
        if (business == null) {
            return Result.error("商家名称或密码错误");
        }
        // 检查商家状态
        if (business.getStatus() != null && business.getStatus() == 0) {
            return Result.error("商家账号已被禁用");
        }
        return Result.success(business);
    }

    /*
    获取商家完整信息（根据ID）
     */
    @GetMapping("/full/{bid}")
    public Result<BusinessFullInfo> getBusinessFullInfoById(@PathVariable Integer bid) {
        BusinessFullInfo business = businessService.getBusinessFullInfoById(bid);
        if (business == null) {
            return Result.error("商家不存在");
        }
        return Result.success(business);
    }

    /*
    商家登录（简单信息 - 兼容性）
     */
    @PostMapping("/login")
    public Result<Business> login(@RequestBody BusinessLoginRequest request) {
        Business business = businessService.login(request.getBname(), request.getPassword());
        if (business == null) {
            return Result.error("商家名称或密码错误");
        }
        return Result.success(business);
    }

    /*
    获取商家信息（简单信息 - 兼容性）
     */
    @GetMapping("/{bid}")
    public Result<Business> getBusinessById(@PathVariable Integer bid) {
        Business business = businessService.getBusinessById(bid);
        if (business == null) {
            return Result.error("商家不存在");
        }
        return Result.success(business);
    }
}