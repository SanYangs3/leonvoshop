// file: UserController.java
package com.lenovo.mall_backend.controller;

import com.lenovo.mall_backend.entity.User;
import com.lenovo.mall_backend.repository.UserRepository;
import com.lenovo.mall_backend.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:8080")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmsService smsService;

    /**
     * 短信验证码注册
     */
    @PostMapping("/register-with-sms")
    public ResponseEntity<Map<String, Object>> registerWithSms(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String tel = request.get("tel");
        String smsCode = request.get("smsCode");

        Map<String, Object> response = new HashMap<>();

        // 验证必填字段
        if (username == null || password == null || tel == null || smsCode == null) {
            response.put("success", false);
            response.put("message", "所有字段都是必填的");
            return ResponseEntity.badRequest().body(response);
        }

        // 验证短信验证码
        boolean isCodeValid = smsService.verifyCode(tel, smsCode);
        if (!isCodeValid) {
            response.put("success", false);
            response.put("message", "短信验证码错误或已过期");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查手机号是否已注册
        if (userRepository.existsByTel(tel)) {
            response.put("success", false);
            response.put("message", "手机号已注册");
            return ResponseEntity.badRequest().body(response);
        }

        // 创建新用户
        User user = new User();
        user.setId("U" + System.currentTimeMillis()); // 生成用户ID
        user.setUsername(username);
        user.setPassword(password); // 注意：实际项目中应该加密存储
        user.setTel(tel);
        user.setCreatedAt(LocalDateTime.now());
        user.setIsVerified(true); // 短信验证过的用户标记为已验证

        // 保存用户
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "注册成功");
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * 登录（支持短信验证码登录和密码登录）
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String tel = request.get("tel");
        String smsCode = request.get("smsCode");

        Map<String, Object> response = new HashMap<>();

        User user = null;

        // 短信验证码登录
        if (tel != null && smsCode != null) {
            // 验证短信验证码
            boolean isCodeValid = smsService.verifyCode(tel, smsCode);
            if (!isCodeValid) {
                response.put("success", false);
                response.put("message", "短信验证码错误或已过期");
                return ResponseEntity.badRequest().body(response);
            }

            // 通过手机号查找用户
            user = userRepository.findByTel(tel);
            if (user == null) {
                response.put("success", false);
                response.put("message", "手机号未注册");
                return ResponseEntity.badRequest().body(response);
            }
        }
        // 密码登录
        else if (username != null && password != null) {
            user = userRepository.findByUsername(username);
            if (user == null || !user.getPassword().equals(password)) {
                response.put("success", false);
                response.put("message", "用户名或密码错误");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.put("success", false);
            response.put("message", "请提供正确的登录信息");
            return ResponseEntity.badRequest().body(response);
        }

        // 更新最后登录时间
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "登录成功");
        response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "tel", user.getTel()
        ));

        return ResponseEntity.ok(response);
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsername(@PathVariable String username) {
        boolean exists = userRepository.existsByUsername(username);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    /**
     * 检查手机号是否存在
     */
    @GetMapping("/check-tel/{tel}")
    public ResponseEntity<Map<String, Object>> checkTel(@PathVariable String tel) {
        boolean exists = userRepository.existsByTel(tel);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }
}