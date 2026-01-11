package com.group.service;

import com.group.entity.StudentVerification;
import java.util.List;

public interface StudentVerificationService {
    void apply(StudentVerification verification);
    StudentVerification getStatus(Integer uid);
    List<StudentVerification> getPendingList();
    List<StudentVerification> getAllList();
    void verify(Integer id, Integer status, Integer uid);
    Integer countPending();
}