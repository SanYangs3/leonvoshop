package com.group.controller;

import com.group.entity.DTO.SmsPasswordResetRequest;
import com.group.entity.Result;
import com.group.service.UserService;
import com.group.service.sms.SmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private UserService userService;

    @PostMapping("/sms/send-code")
    public Result<Object> sendSmsCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        if (phone == null || phone.trim().isEmpty()) {
            return Result.error("手机号不能为空");
        }
        try {
            smsCodeService.sendResetCode(phone.trim());
            return Result.success("验证码发送成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/sms/reset")
    public Result<Object> resetBySms(@RequestBody SmsPasswordResetRequest request) {
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return Result.error("手机号不能为空");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }
        try {
            boolean verified = smsCodeService.verify(request.getPhone().trim(), request.getCode());
            if (!verified) {
                return Result.error("验证码错误或已过期");
            }
            userService.updatePasswordByPhone(request.getPhone().trim(), request.getNewPassword());
            return Result.success("密码已更新");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

