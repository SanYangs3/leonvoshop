package com.group.controller;

import com.group.entity.DTO.EmailCodeLoginRequest;
import com.group.entity.DTO.SmsCodeLoginRequest;
import com.group.entity.Result;
import com.group.service.UserService;
import com.group.service.email.EmailVerificationService;
import com.group.service.impl.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthCodeController {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserService userService;

    /**
     * 邮箱验证码登录（需先调用 /api/email/send-code 传 purpose=login 发送验证码）
     */
    @PostMapping("/login/email-code")
    public Result<Object> emailCodeLogin(@RequestBody EmailCodeLoginRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            return Result.error("验证码不能为空");
        }
        String email = request.getEmail().trim();
        boolean verified = emailVerificationService.verify(email, "login", request.getCode());
        if (!verified) {
            return Result.error("验证码错误或已过期");
        }
        if (userService.searchEmail(email) == 0) {
            return Result.error("邮箱未注册");
        }
        return Result.success("登录验证通过");
    }

    /**
     * 短信验证码登录（需先调用 /api/sms/send-code 传 purpose=login 发送验证码）
     */
    @PostMapping("/login/sms-code")
    public Result<Object> smsCodeLogin(@RequestBody SmsCodeLoginRequest request) {
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return Result.error("手机号不能为空");
        }
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            return Result.error("验证码不能为空");
        }
        String phone = request.getPhone().trim();
        boolean verified = smsService.verifyCode(phone, request.getCode());
        if (!verified) {
            return Result.error("验证码错误或已过期");
        }
        if (userService.searchPhone(phone) == 0) {
            return Result.error("手机号未注册");
        }
        return Result.success("登录验证通过");
    }
}

