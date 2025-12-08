package com.lenovo.mall_backend.repository;

import com.lenovo.mall_backend.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {

    // 根据用户名查找管理员
    Optional<AdminUser> findByUsername(String username);

    // 检查用户名是否存在
    boolean existsByUsername(String username);
}