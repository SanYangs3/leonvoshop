package com.group.service.impl;

import com.group.entity.Business;
import com.group.entity.BusinessFullInfo;
import com.group.mapper.BusinessMapper;
import com.group.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}