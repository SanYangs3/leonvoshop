package com.group.service.impl;

import com.group.entity.Business;
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
}