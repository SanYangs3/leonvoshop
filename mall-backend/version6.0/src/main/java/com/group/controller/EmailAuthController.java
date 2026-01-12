package com.group.controller;

import com.group.entity.DTO.EmailPasswordResetRequest;
import com.group.entity.DTO.EmailRegisterRequest;
import com.group.entity.DTO.EmailSendCodeRequest;
import com.group.entity.Result;
import com.group.entity.User;
import com.group.service.UserService;
import com.group.service.email.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailAuthController {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private UserService userService;

    @PostMapping("/send-code")
    public Result<Object> sendCode(@RequestBody EmailSendCodeRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        try {
            emailVerificationService.sendCode(request.getEmail().trim(), request.getPurpose());
            return Result.success("验证码发送成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Object> register(@RequestBody EmailRegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return Result.error("密码不能为空");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }
        try {
            boolean verified = emailVerificationService.verify(request.getEmail().trim(), "register", request.getCode());
            if (!verified) {
                return Result.error("验证码错误或已过期");
            }
            if (userService.searchEmail(request.getEmail().trim()) > 0) {
                return Result.error("邮箱已被注册");
            }

            User user = new User();
            user.setEmail(request.getEmail().trim());
            user.setPassword(request.getPassword());
            user.setUsername(request.getUsername());
            user.setPhone(request.getPhone());
            userService.insertUser(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/reset-password/send-code")
    public Result<Object> sendResetCode(@RequestBody EmailSendCodeRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        try {
            emailVerificationService.sendCode(request.getEmail().trim(), "reset-password");
            return Result.success("验证码发送成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public Result<Object> resetPassword(@RequestBody EmailPasswordResetRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            return Result.error("新密码不能为空");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return Result.error("两次输入的密码不一致");
        }
        try {
            boolean verified = emailVerificationService.verify(request.getEmail().trim(), "reset-password", request.getCode());
            if (!verified) {
                return Result.error("验证码错误或已过期");
            }
            if (userService.searchEmail(request.getEmail().trim()) == 0) {
                return Result.error("邮箱未注册");
            }
            userService.updatePasswordByEmail(request.getEmail().trim(), request.getNewPassword());
            return Result.success("密码已更新");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

