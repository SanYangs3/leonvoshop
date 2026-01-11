package com.group.controller;

import com.group.entity.Result;
import com.group.entity.StudentVerification;
import com.group.service.StudentVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class StudentVerificationController {

    @Autowired
    private StudentVerificationService verificationService;

    // === 用户端接口 ===

    @PostMapping("/user/student/verify")
    public Result<String> apply(@RequestBody StudentVerification verification) {
        verificationService.apply(verification);
        return Result.success("提交成功，请等待审核");
    }

    @GetMapping("/user/student/status/{uid}")
    public Result<StudentVerification> getStatus(@PathVariable Integer uid) {
        StudentVerification sv = verificationService.getStatus(uid);
        return Result.success(sv);
    }

    // === 管理员接口 ===

    @GetMapping("/admin/student/pending")
    public Result<List<StudentVerification>> getPendingList() {
        return Result.success(verificationService.getPendingList());
    }

    @GetMapping("/admin/student/all")
    public Result<List<StudentVerification>> getAllList() {
        return Result.success(verificationService.getAllList());
    }

    @PostMapping("/admin/student/audit")
    public Result<String> audit(@RequestBody StudentVerification verification) {
        verificationService.verify(verification.getId(), verification.getStatus(), verification.getUid());
        return Result.success("审核完成");
    }

    @GetMapping("/admin/student/pending-count")
    public Result<Integer> getPendingCount() {
        return Result.success(verificationService.countPending());
    }
}