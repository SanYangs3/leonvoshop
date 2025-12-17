// file: SmsController.java
package com.lenovo.mall_backend.controller;

import com.lenovo.mall_backend.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
@CrossOrigin(origins = "http://localhost:8080")
public class SmsController {

    @Autowired
    private SmsService smsService;

    /**
     * 发送短信验证码
     */
    @PostMapping("/send-code")
    public ResponseEntity<Map<String, Object>> sendSmsCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");

        Map<String, Object> response = new HashMap<>();

        if (phone == null || phone.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "手机号不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查是否可以发送
        if (!smsService.canResend(phone)) {
            long remaining = smsService.getRemainingSeconds(phone);
            response.put("success", false);
            response.put("message", String.format("请等待%d秒后重试", remaining));
            response.put("remaining", remaining);
            return ResponseEntity.ok(response);
        }

        boolean sent = smsService.sendSmsCode(phone);

        if (sent) {
            response.put("success", true);
            response.put("message", "验证码发送成功");
        } else {
            response.put("success", false);
            response.put("message", "验证码发送失败，请稍后重试");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 验证短信验证码
     */
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifySmsCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String code = request.get("code");

        Map<String, Object> response = new HashMap<>();

        if (phone == null || code == null) {
            response.put("success", false);
            response.put("message", "手机号和验证码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        boolean isValid = smsService.verifyCode(phone, code);

        if (isValid) {
            response.put("success", true);
            response.put("message", "验证码验证成功");
        } else {
            response.put("success", false);
            response.put("message", "验证码错误或已过期");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 检查是否可以重新发送
     */
    @GetMapping("/can-resend/{phone}")
    public ResponseEntity<Map<String, Object>> canResend(@PathVariable String phone) {
        boolean canResend = smsService.canResend(phone);
        long remaining = smsService.getRemainingSeconds(phone);

        Map<String, Object> response = new HashMap<>();
        response.put("canResend", canResend);
        response.put("remaining", remaining);

        return ResponseEntity.ok(response);
    }
}