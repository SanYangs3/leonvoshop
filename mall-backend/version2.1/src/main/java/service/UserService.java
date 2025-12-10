package com.lenovo.mall_backend.service;

import com.lenovo.mall_backend.entity.User;
import com.lenovo.mall_backend.exception.ResourceNotFoundException;
import com.lenovo.mall_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务类
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ==================== 查询相关方法 ====================

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 分页获取用户
     */
    public Page<User> getUsersByPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * 根据ID获取用户
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在，ID: " + id));
    }

    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在，用户名: " + username));
    }

    /**
     * 根据邮箱获取用户
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在，邮箱: " + email));
    }

    /**
     * 根据手机号获取用户
     */
    public User getUserByMobile(String mobile) {
        return userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在，手机号: " + mobile));
    }

    /**
     * 根据token获取用户
     */
    public User getUserByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在，token: " + token));
    }

    /**
     * 搜索用户
     */
    public List<User> searchUsers(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return userRepository.findAll();
        }
        return userRepository.searchUsers(keyword);
    }

    /**
     * 根据条件查询用户
     */
    public List<User> findUsersByCriteria(String username, String nickname, String role,
                                          String status, String gender, String mobile, String email) {
        Specification<User> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(username)) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + username + "%"));
            }
            if (StringUtils.hasText(nickname)) {
                predicates.add(criteriaBuilder.like(root.get("nickname"), "%" + nickname + "%"));
            }
            if (StringUtils.hasText(role)) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(gender)) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
            }
            if (StringUtils.hasText(mobile)) {
                predicates.add(criteriaBuilder.like(root.get("mobile"), "%" + mobile + "%"));
            }
            if (StringUtils.hasText(email)) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(specification);
    }

    /**
     * 根据角色获取用户
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * 根据状态获取用户
     */
    public List<User> getUsersByStatus(String status) {
        return userRepository.findByStatus(status);
    }

    /**
     * 根据角色和状态获取用户
     */
    public List<User> getUsersByRoleAndStatus(String role, String status) {
        return userRepository.findByRoleAndStatus(role, status);
    }

    // ==================== 新增相关方法 ====================

    /**
     * 注册新用户
     */
    @Transactional
    public User registerUser(User user) {
        // 验证用户数据
        validateUserForRegistration(user);

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在");
        }

        // 检查手机号是否已存在
        if (userRepository.existsByMobile(user.getMobile())) {
            throw new IllegalArgumentException("手机号已存在");
        }

        // 设置默认值
        if (user.getRole() == null) {
            user.setRole("0");  // 默认为普通用户
        }
        if (user.getStatus() == null) {
            user.setStatus("1");  // 默认为启用状态
        }
        if (user.getScore() == null) {
            user.setScore(0);
        }
        if (user.getGender() == null) {
            user.setGender("U");  // 默认为未知
        }

        // 设置创建时间
        user.setCreateTime(LocalDateTime.now().toString());

        // 生成token（简单的UUID）
        user.setToken(generateToken());

        return userRepository.save(user);
    }

    /**
     * 批量添加用户
     */
    @Transactional
    public List<User> addUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("用户列表不能为空");
        }

        // 验证每个用户数据
        for (User user : users) {
            validateUserForRegistration(user);

            // 检查用户名是否已存在
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new IllegalArgumentException("用户名已存在: " + user.getUsername());
            }

            // 设置默认值
            if (user.getRole() == null) {
                user.setRole("0");
            }
            if (user.getStatus() == null) {
                user.setStatus("1");
            }
            if (user.getScore() == null) {
                user.setScore(0);
            }

            // 设置创建时间
            user.setCreateTime(LocalDateTime.now().toString());

            // 生成token
            user.setToken(generateToken());
        }

        return userRepository.saveAll(users);
    }

    // ==================== 更新相关方法 ====================

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);

        // 更新字段（只更新非空字段）
        if (StringUtils.hasText(userDetails.getNickname())) {
            existingUser.setNickname(userDetails.getNickname());
        }

        if (StringUtils.hasText(userDetails.getAvatar())) {
            existingUser.setAvatar(userDetails.getAvatar());
        }

        if (StringUtils.hasText(userDetails.getMobile())) {
            // 检查手机号是否被其他用户使用
            Optional<User> userWithSameMobile = userRepository.findByMobile(userDetails.getMobile());
            if (userWithSameMobile.isPresent() && !userWithSameMobile.get().getId().equals(id)) {
                throw new IllegalArgumentException("手机号已被其他用户使用");
            }
            existingUser.setMobile(userDetails.getMobile());
        }

        if (StringUtils.hasText(userDetails.getEmail())) {
            // 检查邮箱是否被其他用户使用
            Optional<User> userWithSameEmail = userRepository.findByEmail(userDetails.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
                throw new IllegalArgumentException("邮箱已被其他用户使用");
            }
            existingUser.setEmail(userDetails.getEmail());
        }

        if (StringUtils.hasText(userDetails.getGender())) {
            existingUser.setGender(userDetails.getGender());
        }

        if (StringUtils.hasText(userDetails.getDescription())) {
            existingUser.setDescription(userDetails.getDescription());
        }

        if (userDetails.getScore() != null) {
            existingUser.setScore(userDetails.getScore());
        }

        return userRepository.save(existingUser);
    }

    /**
     * 更新用户状态
     */
    @Transactional
    public User updateUserStatus(Long id, String status) {
        if (!"0".equals(status) && !"1".equals(status)) {
            throw new IllegalArgumentException("状态值无效，只能是0或1");
        }

        User user = getUserById(id);
        user.setStatus(status);

        return userRepository.save(user);
    }

    /**
     * 更新用户角色
     */
    @Transactional
    public User updateUserRole(Long id, String role) {
        if (!"0".equals(role) && !"1".equals(role)) {
            throw new IllegalArgumentException("角色值无效，只能是0或1");
        }

        User user = getUserById(id);
        user.setRole(role);

        return userRepository.save(user);
    }

    /**
     * 修改密码
     */
    @Transactional
    public User changePassword(Long id, String oldPassword, String newPassword) {
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("新密码不能为空");
        }

        User user = getUserById(id);

        // 验证旧密码
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("原密码错误");
        }

        user.setPassword(newPassword);

        return userRepository.save(user);
    }

    /**
     * 重置密码
     */
    @Transactional
    public User resetPassword(Long id, String newPassword) {
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("新密码不能为空");
        }

        User user = getUserById(id);
        user.setPassword(newPassword);

        return userRepository.save(user);
    }

    /**
     * 更新用户积分
     */
    @Transactional
    public User updateUserScore(Long id, Integer score) {
        if (score == null || score < 0) {
            throw new IllegalArgumentException("积分不能为负数");
        }

        User user = getUserById(id);
        user.setScore(score);

        return userRepository.save(user);
    }

    /**
     * 增加用户积分
     */
    @Transactional
    public User increaseScore(Long id, Integer points) {
        if (points == null || points <= 0) {
            throw new IllegalArgumentException("增加的积分必须大于0");
        }

        User user = getUserById(id);
        user.setScore(user.getScore() + points);

        return userRepository.save(user);
    }

    /**
     * 减少用户积分
     */
    @Transactional
    public User decreaseScore(Long id, Integer points) {
        if (points == null || points <= 0) {
            throw new IllegalArgumentException("减少的积分必须大于0");
        }

        User user = getUserById(id);
        int newScore = user.getScore() - points;
        if (newScore < 0) {
            throw new IllegalArgumentException("积分不足，当前积分: " + user.getScore());
        }

        user.setScore(newScore);

        return userRepository.save(user);
    }

    /**
     * 更新用户token
     */
    @Transactional
    public User updateToken(Long id, String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("token不能为空");
        }

        User user = getUserById(id);
        user.setToken(token);

        return userRepository.save(user);
    }

    /**
     * 清除用户token
     */
    @Transactional
    public User clearToken(Long id) {
        User user = getUserById(id);
        user.setToken(null);

        return userRepository.save(user);
    }

    // ==================== 删除相关方法 ====================

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    /**
     * 批量删除用户
     */
    @Transactional
    public void deleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }

        // 检查所有用户是否存在
        List<User> existingUsers = userRepository.findAllById(ids);
        if (existingUsers.size() != ids.size()) {
            throw new ResourceNotFoundException("部分用户不存在");
        }

        userRepository.deleteAllById(ids);
    }

    // ==================== 业务逻辑方法 ====================

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        return userRepository.findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new ResourceNotFoundException("用户名或密码错误"));
    }

    /**
     * 检查用户名是否存在
     */
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     */
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 检查手机号是否存在
     */
    public boolean checkMobileExists(String mobile) {
        return userRepository.existsByMobile(mobile);
    }

    /**
     * 获取用户统计信息
     */
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        // 用户总数
        long totalUsers = userRepository.countAllUsers();
        stats.put("totalUsers", totalUsers);

        // 按状态统计
        long activeUsers = userRepository.countByStatus("1");
        long inactiveUsers = userRepository.countByStatus("0");
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);

        // 按角色统计
        long adminUsers = userRepository.countByRole("1");
        long normalUsers = userRepository.countByRole("0");
        stats.put("adminUsers", adminUsers);
        stats.put("normalUsers", normalUsers);

        // 按性别统计
        long maleUsers = userRepository.countByGender("M");
        long femaleUsers = userRepository.countByGender("F");
        long unknownGenderUsers = userRepository.countByGender("U");
        stats.put("maleUsers", maleUsers);
        stats.put("femaleUsers", femaleUsers);
        stats.put("unknownGenderUsers", unknownGenderUsers);

        // 统计积分
        List<User> allUsers = userRepository.findAll();
        int totalScore = allUsers.stream().mapToInt(User::getScore).sum();
        double avgScore = totalUsers > 0 ? (double) totalScore / totalUsers : 0;
        stats.put("totalScore", totalScore);
        stats.put("averageScore", String.format("%.2f", avgScore));

        // 积分分布
        long score0_100 = allUsers.stream().filter(u -> u.getScore() <= 100).count();
        long score101_500 = allUsers.stream().filter(u -> u.getScore() > 100 && u.getScore() <= 500).count();
        long score501_1000 = allUsers.stream().filter(u -> u.getScore() > 500 && u.getScore() <= 1000).count();
        long score1000Plus = allUsers.stream().filter(u -> u.getScore() > 1000).count();

        stats.put("score0_100", score0_100);
        stats.put("score101_500", score101_500);
        stats.put("score501_1000", score501_1000);
        stats.put("score1000Plus", score1000Plus);

        return stats;
    }

    /**
     * 获取活跃用户（最近注册的用户）
     */
    public List<User> getRecentUsers(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;  // 默认返回10个
        }

        List<User> allUsers = userRepository.findAll();

        // 按创建时间排序（这里假设create_time是字符串格式，实际可能需要解析）
        return allUsers.stream()
                .sorted((u1, u2) -> {
                    if (u1.getCreateTime() == null || u2.getCreateTime() == null) {
                        return 0;
                    }
                    // 简单的字符串比较，实际应该解析为日期
                    return u2.getCreateTime().compareTo(u1.getCreateTime());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取高积分用户
     */
    public List<User> getHighScoreUsers(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;  // 默认返回10个
        }

        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .sorted((u1, u2) -> Integer.compare(u2.getScore(), u1.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ==================== 验证方法 ====================

    /**
     * 验证用户注册数据
     */
    private void validateUserForRegistration(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }

        if (!StringUtils.hasText(user.getUsername())) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            throw new IllegalArgumentException("用户名长度必须在3-50个字符之间");
        }

        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("密码不能为空");
        }

        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于6个字符");
        }

        // 验证邮箱格式（简单验证）
        if (StringUtils.hasText(user.getEmail())) {
            if (!user.getEmail().contains("@")) {
                throw new IllegalArgumentException("邮箱格式不正确");
            }
        }

        // 验证手机号格式（简单验证，实际应该更严格）
        if (StringUtils.hasText(user.getMobile())) {
            if (!user.getMobile().matches("\\d{11}")) {
                throw new IllegalArgumentException("手机号必须是11位数字");
            }
        }

        // 验证性别
        if (StringUtils.hasText(user.getGender())) {
            if (!"M".equals(user.getGender()) && !"F".equals(user.getGender()) && !"U".equals(user.getGender())) {
                throw new IllegalArgumentException("性别只能是M、F或U");
            }
        }
    }

    /**
     * 生成token
     */
    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }

    /**
     * 获取用户总数
     */
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * 检查用户是否存在
     */
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}