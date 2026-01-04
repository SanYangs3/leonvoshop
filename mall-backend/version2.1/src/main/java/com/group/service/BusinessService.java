package com.group.service;

import com.group.entity.Business;
import java.util.List;

public interface BusinessService {
    /*
    返回所有商家信息
     */
    List<Business> getAllBusinesses();
}