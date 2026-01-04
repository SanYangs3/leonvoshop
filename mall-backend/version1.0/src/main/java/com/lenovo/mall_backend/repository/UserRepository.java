package com.lenovo.mall_backend.repository;

import com.lenovo.mall_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // 根据用户名查找用户
    Optional<User> findByUsername(String username);

    // 根据邮箱查找用户
    Optional<User> findByEmail(String email);

    // 根据手机号查找用户
    Optional<User> findByMobile(String mobile);

    // 根据用户名和密码查找用户（用于登录）
    Optional<User> findByUsernameAndPassword(String username, String password);

    // 根据角色查找用户
    List<User> findByRole(String role);

    // 根据状态查找用户
    List<User> findByStatus(String status);

    // 根据用户名模糊搜索
    List<User> findByUsernameContaining(String keyword);

    // 根据昵称模糊搜索
    List<User> findByNicknameContaining(String keyword);

    // 根据用户名或昵称搜索
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.nickname LIKE %:keyword%")
    List<User> searchByUsernameOrNickname(@Param("keyword") String keyword);

    // 根据邮箱模糊搜索
    List<User> findByEmailContaining(String keyword);

    // 根据手机号模糊搜索
    List<User> findByMobileContaining(String keyword);

    // 根据用户名、邮箱或手机号搜索
    @Query("SELECT u FROM User u WHERE " +
            "u.username LIKE %:keyword% OR " +
            "u.email LIKE %:keyword% OR " +
            "u.mobile LIKE %:keyword% OR " +
            "u.nickname LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);

    // 根据角色和状态查询
    List<User> findByRoleAndStatus(String role, String status);

    // 统计用户总数
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    // 根据状态统计用户数量
    long countByStatus(String status);

    // 根据角色统计用户数量
    long countByRole(String role);

    // 根据性别统计用户数量
    long countByGender(String gender);

    // 查找积分大于等于指定值的用户
    List<User> findByScoreGreaterThanEqual(Integer score);

    // 查找积分小于等于指定值的用户
    List<User> findByScoreLessThanEqual(Integer score);

    // 查找积分在指定范围内的用户
    List<User> findByScoreBetween(Integer minScore, Integer maxScore);

    // 根据token查找用户
    Optional<User> findByToken(String token);

    // 检查用户名是否存在
    boolean existsByUsername(String username);

    // 检查邮箱是否存在
    boolean existsByEmail(String email);

    // 检查手机号是否存在
    boolean existsByMobile(String mobile);
}