package com.group.controller;

import com.group.entity.DTO.DailyUserGrowthDTO;
import com.group.entity.DTO.UserGrowthTrendDTO;
import com.group.entity.Ordered;
import com.group.entity.Result;
import com.group.entity.User;
import com.group.entity.UserIdentity;
import com.group.entity.vo.UserCountVO;
import com.group.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /*
    返回所有用户的全部信息
     */
    @GetMapping
    public Result<List<User>> getAllUser(){
        log.info("返回所有用户信息");
        List<User> userList = userService.getAllUser();
        return Result.success(userList);
    }

    /*
    用户注册，只输入用户名和密码
     */
    @PostMapping("/insert")
    public Result insertUser(@RequestBody User user){
        log.info("用户注册：{}",user);
        userService.insertUser(user);
        return Result.success();
    }

    /*
    根据用户id返回单个用户所有信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id){
        log.info("根据用户id返回单个用户所有信息,id:{}",id);
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    /*
    根据用户id修改用户信息
     */
    @PostMapping("/update")
    public Result updateUser(@RequestBody User user){
        log.info("根据用户id修改用户信息:{}",user);
        userService.updateById(user);
        return Result.success();
    }

    /*
    根据用户id获取用户身份
     */
    @GetMapping("/role/{id}")
    public Result<UserIdentity> getRoleById(@PathVariable Long id){
        log.info("根据用户id获取用户身份,id:{}",id);
        UserIdentity userIdentity = userService.getUserIndentityById(id);
        return Result.success(userIdentity);
    }

    /*
    通过用户id注销用户
     */
    @PostMapping("/status/{id}")
    public Result updateStatus(@PathVariable Integer id){
        log.info("通过用户id注销用户,id:{}",id);
        userService.updateStatus(id);
        return Result.success();
    }

    /*
    获取今日新增用户数
     */
    /*@GetMapping("/add")
    public Result getAddCountToday(@RequestParam(value = "startTime", required = false)
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                   @RequestParam(value = "endTime", required = false)
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime){
        log.info("查询今日新增用户数");
        if (startTime == null && endTime == null) {
            return Result.error("至少提供一个时间信息");
        }
        if (startTime == null) {
            // 如果只提供结束时间，起始时间设为结束时间的前7天
            startTime = endTime.minusDays(7);
        }

        if (endTime == null) {
            // 如果只提供起始时间，结束时间设为当前时间
            endTime = LocalDateTime.now();
        }

        Integer count = userService.getAddCountToday(startTime,endTime);
        return Result.success(count);

    }*/

    @GetMapping("/add")
    public Result getAddCountToday(){
        log.info("查询今日新增用户数");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);

        Integer count = userService.getAddCountToday(startOfDay, now);
        return Result.success(count);
    }
    /*
    获取用户总数
     */
    @GetMapping("/all")
    public Result getAllcount(){
        log.info("获取所有用户总数");
        List<User> allUser = userService.getAllUser();
        Integer count = allUser.size();
        return Result.success(count);
    }

    /*
    编辑用户身份
     */
    @PutMapping("/updaterole/{uid}")
    public Result updaterole(@RequestParam String role,@PathVariable Long uid){
        log.info("编辑用户身份，id：{}",uid);
        userService.updateUserRole(uid,role);
        return Result.success();
    }
    /*
    获取昨天今天和百分比
     */
    @GetMapping("/compare")
    public Result<UserCountVO> compare(){
        UserCountVO userCountVO = userService.getCompare();
        return Result.success(userCountVO);
    }

    @GetMapping("/phonelogin")
    public Result phonelogin(@RequestParam String phone,@RequestParam String password){
        log.info("手机号密码登录");
        Integer count = userService.getByPhoneAndPass(phone,password);
        if(count>0){
            return Result.success();
        }
        else{
            return Result.error("手机号或密码错误");
        }
    }

    @GetMapping("/emaillogin")
    public Result emaillogin(@RequestParam String email,@RequestParam String password){
        log.info("邮箱密码登录");
        Integer count = userService.getByEmailAndPass(email,password);
        if(count>0){
            return Result.success();
        }
        else{
            return Result.error("邮箱号或密码错误");
        }
    }

    @GetMapping("/usernamelogin")
    public Result usernamelogin(@RequestParam String username,@RequestParam String password){
        log.info("用户名密码登录");
        Integer count = userService.getByNameAndPass(username,password);
        if(count>0){
            return Result.success();
        }
        else{
            return Result.error("用户名或密码错误");
        }
    }

    @GetMapping("/namedistinct")
    public Result namedistinct(@RequestParam String username){
        log.info("检测用户名是否已存在");
        Integer count = userService.searchUsername(username);
        if(count>0){
            return Result.error("用户名已存在");
        }else {
            return Result.success();
        }
    }

    @GetMapping("/phonedistinct")
    public Result phonedistinct(@RequestParam String phone){
        log.info("检测手机号是否已存在");
        Integer count = userService.searchPhone(phone);
        if(count>0){
            return Result.error("手机号已存在");
        }else {
            return Result.success();
        }
    }


    @GetMapping("/growth/trend")
    public Result<UserGrowthTrendDTO> getUserGrowthTrend() {
        UserGrowthTrendDTO trend = userService.getUserGrowthTrend();
        return Result.success(trend);
    }

    @GetMapping("/growth/weekly")
    public Result<List<DailyUserGrowthDTO>> getWeeklyGrowth() {
        List<DailyUserGrowthDTO> weeklyGrowth = userService.getWeeklyGrowth();
        return Result.success(weeklyGrowth);
    }

    @GetMapping("/growth/monthly")
    public Result<List<DailyUserGrowthDTO>> getMonthlyGrowth() {
        List<DailyUserGrowthDTO> monthlyGrowth = userService.getMonthlyGrowth();
        return Result.success(monthlyGrowth);
    }

}
