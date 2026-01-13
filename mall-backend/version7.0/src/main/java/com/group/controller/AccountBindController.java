package com.group.controller;

import com.group.entity.DTO.BindEmailRequest;
import com.group.entity.DTO.BindPhoneRequest;
import com.group.entity.Result;
import com.group.entity.User;
import com.group.service.UserService;
import com.group.service.email.EmailVerificationService;
import com.group.service.impl.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/bind")
public class AccountBindController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private SmsService smsService;

    /**
     * 绑定邮箱（需先调用 /api/email/send-code purpose=bind 给新邮箱发送验证码）
     */
    @PostMapping("/email")
    public Result<Object> bindEmail(@RequestBody BindEmailRequest request) {
        if (request.getUid() == null) {
            return Result.error("用户ID不能为空");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            return Result.error("验证码不能为空");
        }
        User user = userService.getUserById(request.getUid());
        if (user == null) {
            return Result.error("用户不存在");
        }
        String email = request.getEmail().trim();
        if (userService.searchEmail(email) > 0) {
            return Result.error("邮箱已被绑定/注册");
        }
        boolean verified = emailVerificationService.verify(email, "bind", request.getCode());
        if (!verified) {
            return Result.error("验证码错误或已过期");
        }
        userService.updateEmailByUid(request.getUid(), email);
        return Result.success("邮箱绑定成功");
    }

    /**
     * 绑定手机号（需先调用 /api/sms/send-code purpose=bind 给手机号发送验证码）
     */
    @PostMapping("/phone")
    public Result<Object> bindPhone(@RequestBody BindPhoneRequest request) {
        if (request.getUid() == null) {
            return Result.error("用户ID不能为空");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            return Result.error("手机号不能为空");
        }
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            return Result.error("验证码不能为空");
        }
        User user = userService.getUserById(request.getUid());
        if (user == null) {
            return Result.error("用户不存在");
        }
        String phone = request.getPhone().trim();
        if (userService.searchPhone(phone) > 0) {
            return Result.error("手机号已被绑定/注册");
        }
        boolean verified = smsService.verifyCode(phone, request.getCode());
        if (!verified) {
            return Result.error("验证码错误或已过期");
        }
        userService.updatePhoneByUid(request.getUid(), phone);
        return Result.success("手机号绑定成功");
    }

    /**
     * 换绑手机号：需当前密码 + 新手机号验证码
     * 先对新手机号调 /api/sms/send-code purpose=bind 发送验证码
     * body: { uid, currentPassword, newPhone, code }
     */
    @PostMapping("/change/phone")
    public Result<Object> changePhone(@RequestBody Map<String, Object> request) {
        log.info("收到换绑手机号请求: {}", request);
        Long uid = request.get("uid") != null ? Long.valueOf(request.get("uid").toString()) : null;
        String currentPassword = (String) request.get("currentPassword");
        String newPhone = (String) request.get("newPhone");
        String code = (String) request.get("code");

        if (uid == null) {
            return Result.error("用户ID不能为空");
        }
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return Result.error("当前密码不能为空");
        }
        if (newPhone == null || newPhone.trim().isEmpty()) {
            return Result.error("新手机号不能为空");
        }
        if (code == null || code.trim().isEmpty()) {
            return Result.error("验证码不能为空");
        }
        User user = userService.getUserById(uid);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!currentPassword.equals(user.getPassword())) {
            return Result.error("当前密码错误");
        }
        String phone = newPhone.trim();
        if (userService.searchPhone(phone) > 0) {
            return Result.error("手机号已被绑定/注册");
        }
        boolean verified = smsService.verifyCode(phone, code);
        if (!verified) {
            return Result.error("验证码错误或已过期");
        }
        userService.updatePhoneByUid(uid, phone);
        return Result.success("手机号换绑成功");
    }

    /**
     * 换绑邮箱：需当前密码 + 新邮箱验证码
     * 先对新邮箱调 /api/email/send-code purpose=bind 发送验证码
     * body: { uid, currentPassword, newEmail, code }
     */
    @PostMapping("/change/email")
    public Result<Object> changeEmail(@RequestBody Map<String, Object> request) {
        log.info("收到换绑邮箱请求: {}", request);
        Long uid = request.get("uid") != null ? Long.valueOf(request.get("uid").toString()) : null;
        String currentPassword = (String) request.get("currentPassword");
        String newEmail = (String) request.get("newEmail");
        String code = (String) request.get("code");

        if (uid == null) {
            return Result.error("用户ID不能为空");
        }
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return Result.error("当前密码不能为空");
        }
        if (newEmail == null || newEmail.trim().isEmpty()) {
            return Result.error("新邮箱不能为空");
        }
        if (code == null || code.trim().isEmpty()) {
            return Result.error("验证码不能为空");
        }
        User user = userService.getUserById(uid);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!currentPassword.equals(user.getPassword())) {
            return Result.error("当前密码错误");
        }
        String email = newEmail.trim();
        if (userService.searchEmail(email) > 0) {
            return Result.error("邮箱已被绑定/注册");
        }
        boolean verified = emailVerificationService.verify(email, "bind", code);
        if (!verified) {
            return Result.error("验证码错误或已过期");
        }
        userService.updateEmailByUid(uid, email);
        return Result.success("邮箱换绑成功");
    }
}

