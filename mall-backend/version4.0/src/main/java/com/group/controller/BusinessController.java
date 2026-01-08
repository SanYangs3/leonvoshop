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

    /*
    功能1：更新商家完整信息
    请求路径：PUT /api/businesses/{bid}
    说明：可以更新除密码外的所有信息
     */
    @PutMapping("/{bid}")
    public Result<Boolean> updateBusinessFullInfo(@PathVariable Integer bid,
                                                  @RequestBody BusinessUpdateRequest request) {
        try {
            // 构建BusinessFullInfo对象
            BusinessFullInfo businessFullInfo = new BusinessFullInfo();
            businessFullInfo.setBid(bid);
            businessFullInfo.setBname(request.getBname());
            businessFullInfo.setContactPerson(request.getContactPerson());
            businessFullInfo.setContactPhone(request.getContactPhone());
            businessFullInfo.setContactEmail(request.getContactEmail());
            businessFullInfo.setAddress(request.getAddress());
            businessFullInfo.setDescription(request.getDescription());

            boolean success = businessService.updateBusinessFullInfo(businessFullInfo);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("更新商家信息失败");
            }
        } catch (Exception e) {
            return Result.error("更新商家信息失败：" + e.getMessage());
        }
    }

    /*
    功能2：商家密码修改
    请求路径：PUT /api/businesses/{bid}/password
     */
    @PutMapping("/{bid}/password")
    public Result<Boolean> updatePassword(@PathVariable Integer bid,
                                          @RequestBody PasswordUpdateRequest request) {
        try {
            boolean success = businessService.updatePassword(
                    bid,
                    request.getOldPassword(),
                    request.getNewPassword()
            );
            if (success) {
                return Result.success(true);
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            return Result.error("密码修改失败：" + e.getMessage());
        }
    }

    // 商家信息更新请求类（排除密码字段）
    static class BusinessUpdateRequest {
        private String bname;
        private String contactPerson;
        private String contactPhone;
        private String contactEmail;
        private String address;
        private String description;

        // getters and setters
        public String getBname() { return bname; }
        public void setBname(String bname) { this.bname = bname; }

        public String getContactPerson() { return contactPerson; }
        public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

        public String getContactEmail() { return contactEmail; }
        public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    // 密码修改请求类
    static class PasswordUpdateRequest {
        private String oldPassword;
        private String newPassword;

        // getters and setters
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}