// file: UserRepository.java
package com.lenovo.mall_backend.repository;

import com.lenovo.mall_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);
    User findByTel(String tel);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByTel(String tel);
    boolean existsByEmail(String email);
}