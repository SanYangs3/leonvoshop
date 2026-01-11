package com.group.service.impl;

import com.group.entity.Business;
import com.group.entity.BusinessFullInfo;
import com.group.mapper.BusinessMapper;
import com.group.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private BusinessMapper businessMapper;

    @Override
    public List<Business> getAllBusinesses() {
        return businessMapper.getAllBusinesses();
    }

    @Override
    public BusinessFullInfo loginWithFullInfo(String bname, String password) {
        return businessMapper.loginWithFullInfo(bname, password);
    }

    @Override
    public BusinessFullInfo getBusinessFullInfoById(Integer bid) {
        return businessMapper.getBusinessFullInfoById(bid);
    }

    @Override
    public Business login(String bname, String password) {
        return businessMapper.login(bname, password);
    }

    @Override
    public Business getBusinessById(Integer bid) {
        return businessMapper.getBusinessById(bid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBusinessFullInfo(BusinessFullInfo businessFullInfo) throws Exception {
        // 验证必填字段
        if (businessFullInfo.getBid() == null) {
            throw new Exception("商家ID不能为空");
        }
        if (businessFullInfo.getBname() == null || businessFullInfo.getBname().trim().isEmpty()) {
            throw new Exception("商家名称不能为空");
        }

        // 更新商家完整信息
        int result = businessMapper.updateBusinessFullInfo(businessFullInfo);
        if (result <= 0) {
            throw new Exception("更新商家信息失败，可能商家不存在");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Integer bid, String oldPassword, String newPassword) throws Exception {
        // 1. 验证参数
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new Exception("旧密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new Exception("新密码不能为空");
        }
        if (newPassword.length() < 6) {
            throw new Exception("新密码长度不能少于6位");
        }
        if (oldPassword.equals(newPassword)) {
            throw new Exception("新密码不能与旧密码相同");
        }

        // 2. 验证旧密码是否正确
        int checkResult = businessMapper.checkPassword(bid, oldPassword);
        if (checkResult <= 0) {
            throw new Exception("旧密码错误");
        }

        // 3. 更新密码
        int updateResult = businessMapper.updatePassword(bid, oldPassword, newPassword);
        if (updateResult <= 0) {
            throw new Exception("密码修改失败，请稍后重试");
        }

        return true;
    }
}