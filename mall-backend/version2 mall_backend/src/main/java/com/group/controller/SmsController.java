package com.group.controller;

import com.group.entity.Result;

import com.group.service.impl.SmsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    /**
     * 发送短信验证码
     */
    @PostMapping("/send-code")
    public Result<Object> sendSmsCode(@RequestBody Map<String, String> request,
                                      HttpServletRequest httpRequest) {
        String phone = request.get("phone");
        String purpose = request.get("purpose");

        if (phone == null || phone.trim().isEmpty()) {
            return Result.error("手机号不能为空");
        }

        String ipAddress = getClientIp(httpRequest);

        try {
            boolean sent = smsService.sendSmsCode(phone, purpose, ipAddress);
            if (sent) {
                return Result.success("验证码发送成功");
            } else {
                return Result.error("验证码发送失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 验证短信验证码
     */
    @PostMapping("/verify-code")
    public Result<Object> verifySmsCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");

        if (phone == null || code == null) {
            return Result.error("手机号和验证码不能为空");
        }

        boolean isValid = smsService.verifyCode(phone, code);
        if (isValid) {
            return Result.success("验证码验证成功");
        } else {
            return Result.error("验证码错误或已过期");
        }
    }

    /**
     * 检查是否可以重新发送
     */
    @GetMapping("/can-resend/{phone}")
    public Result<Map<String, Object>> canResend(@PathVariable String phone) {
        boolean canResend = smsService.canResend(phone);
        long remaining = smsService.getRemainingSeconds(phone);

        Map<String, Object> data = new HashMap<>();
        data.put("canResend", canResend);
        data.put("remaining", remaining);

        return Result.success(data);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}