package com.group.service.impl;

import com.group.entity.DTO.DailyUserGrowthDTO;
import com.group.entity.DTO.UserGrowthTrendDTO;
import com.group.entity.User;
import com.group.entity.UserIdentity;
import com.group.entity.vo.UserCountVO;
import com.group.exception.BusinessException;
import com.group.mapper.UserMapper;
import com.group.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_AVATAR = "https://randomuser.me/api/portraits/men/1.jpg";
    private static final String DEFAULT_ROLE = "user";
    private static final int ACTIVE_STATUS = 1;

    @Autowired
    private UserMapper userMapper;

    /*
    返回所有用户信息
     */
    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }

    /*
    用户注册，只输入用户名和密码
     */
    @Override
    public void insertUser(User user) {
        LocalDateTime now = LocalDateTime.now();

        // 填充必要的默认值，避免短信注册时产生空列导致后续登录信息缺失
        if (user.getStatus() == null) {
            user.setStatus(ACTIVE_STATUS);
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole(DEFAULT_ROLE);
        }
        if (user.getAvatar() == null || user.getAvatar().trim().isEmpty()) {
            user.setAvatar(DEFAULT_AVATAR);
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                user.setUsername("u_" + user.getPhone().trim());
            } else {
                user.setUsername("user_" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            }
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                user.setEmail(user.getPhone().trim() + "@placeholder.local");
            } else {
                user.setEmail(user.getUsername() + "@placeholder.local");
            }
        }

        user.setCreateTime(now);
        user.setUpdateTime(now);
        userMapper.insertUser(user);
    }

    /*
    根据用户id返回单个用户所有信息
     */
    @Override
    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    /*
    根据用户id修改用户信息
     */
    @Override
    public void updateById(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    /*
    根据用户id获取用户身份
     */
    @Override
    public UserIdentity getUserIndentityById(Long id) {
        return userMapper.getUserIndentityById(id);
    }

    /*
    通过用户id注销用户
     */
    @Override
    public void updateStatus(Integer id) {
        userMapper.updateStatus(id);
    }

    /*
    获取今日新增用户数
     */

    @Override
    public Integer getAddCountToday(LocalDateTime startTime, LocalDateTime endTime) {
        return userMapper.getAddCountToday(startTime,endTime);
    }

    /*
    编辑用户身份
     */

    @Override
    public void updateUserRole(Long uid, String role) {
        userMapper.updateUserRole(uid,role);
    }

    @Override
    public UserCountVO getCompare() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();  // 今天00:00:00
        LocalDateTime todayEnd = LocalDateTime.now();     // 当前时间

        Integer countToday = userMapper.getAddCountToday(todayStart,todayEnd);
        // 获取昨天的时间范围
        LocalDate yesterday = today.minusDays(1);
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();  // 昨天00:00:00
        LocalDateTime yesterdayEnd = yesterday.atTime(23, 59, 59);

        Integer countYesterday = userMapper.getAddCountToday(yesterdayStart,yesterdayEnd);

        UserCountVO userCountVO = new UserCountVO(countToday,countYesterday);
        return userCountVO;
    }

    @Override
    public Integer getByPhoneAndPass(String phone, String password) {
        return userMapper.getByPhoneAndPass(phone, password);
    }

    @Override
    public Integer getByEmailAndPass(String email, String password) {
        return userMapper.getByEmailAndPass(email, password);
    }

    @Override
    public Integer getByNameAndPass(String username, String password) {
        return userMapper.getByNameAndPass(username,password);
    }

    @Override
    public Integer searchUsername(String username) {
        return userMapper.searchUsername(username);
    }

    @Override
    public Integer searchPhone(String phone) {
        return userMapper.searchPhone(phone);
    }

    @Override
    public UserGrowthTrendDTO getUserGrowthTrend() {
        // 获取当前时间
        LocalDate now = LocalDate.now();

        // 计算本周的开始和结束日期
        LocalDate weekStartDate = now.with(DayOfWeek.MONDAY);
        LocalDate weekEndDate = now.with(DayOfWeek.SUNDAY);

        // 计算本月的开始和结束日期
        LocalDate monthStartDate = now.withDayOfMonth(1);
        LocalDate monthEndDate = now.with(TemporalAdjusters.lastDayOfMonth());

        // 获取本周数据
        List<DailyUserGrowthDTO> weeklyGrowth = getDailyGrowthBetween(weekStartDate, weekEndDate);

        // 获取本月数据
        List<DailyUserGrowthDTO> monthlyGrowth = getDailyGrowthBetween(monthStartDate, monthEndDate);

        return UserGrowthTrendDTO.builder()
                .weeklyGrowth(weeklyGrowth)
                .monthlyGrowth(monthlyGrowth)
                .weekStartDate(weekStartDate)
                .weekEndDate(weekEndDate)
                .monthStartDate(monthStartDate)
                .monthEndDate(monthEndDate)
                .build();
    }

    @Override
    public List<DailyUserGrowthDTO> getDailyGrowthBetween(LocalDate startDate, LocalDate endDate) {
        try {
            // 转换日期格式
            String start = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String end = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 从数据库查询数据
            List<DailyUserGrowthDTO> dailyGrowth = userMapper.selectDailyUserGrowth(start, end);

            // 填充缺失的日期（确保返回完整的日期序列）
            return fillMissingDates(startDate, endDate, dailyGrowth);
        } catch (Exception e) {
            log.error("获取用户增长数据失败，开始日期：{}，结束日期：{}", startDate, endDate, e);
            throw new BusinessException("获取用户增长数据失败");
        }
    }

    @Override
    public List<DailyUserGrowthDTO> getWeeklyGrowth() {
        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = now.with(DayOfWeek.SUNDAY);
        return getDailyGrowthBetween(weekStart, weekEnd);
    }

    @Override
    public List<DailyUserGrowthDTO> getMonthlyGrowth() {
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.with(TemporalAdjusters.lastDayOfMonth());
        return getDailyGrowthBetween(monthStart, monthEnd);
    }

    /**
     * 填充缺失的日期，确保返回完整的日期序列
     */
    private List<DailyUserGrowthDTO> fillMissingDates(LocalDate startDate, LocalDate endDate,
                                                      List<DailyUserGrowthDTO> existingData) {
        List<DailyUserGrowthDTO> result = new ArrayList<>();
        Map<String, Long> dataMap = existingData.stream()
                .collect(Collectors.toMap(DailyUserGrowthDTO::getDate, DailyUserGrowthDTO::getUserCount));

        LocalDate currentDate = startDate;
        long cumulativeCount = 0;

        // 生成指定日期范围内的每一天
        while (!currentDate.isAfter(endDate)) {
            String dateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Long count = dataMap.getOrDefault(dateStr, 0L);
            cumulativeCount += count;

            DailyUserGrowthDTO dto = new DailyUserGrowthDTO();
            dto.setDate(dateStr);
            dto.setUserCount(count);
            dto.setCumulativeCount(cumulativeCount);

            result.add(dto);
            currentDate = currentDate.plusDays(1);
        }

        return result;
    }
}