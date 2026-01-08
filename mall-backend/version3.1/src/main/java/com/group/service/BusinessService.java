package com.group.service;

import com.group.entity.Business;
import com.group.entity.BusinessFullInfo;
import java.util.List;

public interface BusinessService {
    /*
    返回所有商家信息（基本信息）
     */
    List<Business> getAllBusinesses();

    /*
    新增：商家登录（返回完整信息）
     */
    BusinessFullInfo loginWithFullInfo(String bname, String password);

    /*
    新增：根据ID获取完整商家信息
     */
    BusinessFullInfo getBusinessFullInfoById(Integer bid);

    /*
    商家登录（简单信息 - 兼容性）
     */
    Business login(String bname, String password);

    /*
    根据ID获取商家信息（简单信息 - 兼容性）
     */
    Business getBusinessById(Integer bid);

    /*
    新增：更新商家完整信息
     */
    boolean updateBusinessFullInfo(BusinessFullInfo businessFullInfo) throws Exception;

    /*
    新增：修改商家密码
     */
    boolean updatePassword(Integer bid, String oldPassword, String newPassword) throws Exception;
}