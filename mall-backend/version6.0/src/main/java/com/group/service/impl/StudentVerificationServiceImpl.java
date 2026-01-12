package com.group.service.impl;

import com.group.entity.StudentVerification;
import com.group.mapper.StudentVerificationMapper;
import com.group.service.StudentVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentVerificationServiceImpl implements StudentVerificationService {

    @Autowired
    private StudentVerificationMapper verificationMapper;

    @Override
    public void apply(StudentVerification verification) {
        verification.setStatus(0); // 默认为待审核
        verification.setCreateTime(LocalDateTime.now());
        verification.setUpdateTime(LocalDateTime.now());
        verificationMapper.insert(verification);
    }

    @Override
    public StudentVerification getStatus(Integer uid) {
        return verificationMapper.getByUid(uid);
    }

    @Override
    public List<StudentVerification> getPendingList() {
        return verificationMapper.getByStatus(0);
    }

    @Override
    public List<StudentVerification> getAllList() {
        return verificationMapper.getAll();
    }

    @Override
    @Transactional
    public void verify(Integer id, Integer status, Integer uid) {
        verificationMapper.updateStatus(id, status, LocalDateTime.now());
        if (status == 1) { // 如果审核通过
            verificationMapper.updateIdentityStudentStatus(uid);
        }
    }

    @Override
    public Integer countPending() {
        return verificationMapper.countPending();
    }
}