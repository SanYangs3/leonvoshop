package com.group.controller;

import com.group.entity.Ordered;
import com.group.entity.Result;
import com.group.entity.User;
import com.group.entity.UserIdentity;
import com.group.entity.vo.UserCountVO;
import com.group.service.UserService;
import com.group.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    AliOSSUtils aliOSSUtils;

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

    @PostMapping("/register-with-sms")
    public Result<Object> registerWithSms(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String phone = request.get("phone");
            String smsCode = request.get("smsCode");

            if (username == null || password == null || phone == null || smsCode == null) {
                return Result.error("所有字段都是必填的");
            }

            Integer userId = userService.registerWithSms(username, password, phone, smsCode);

            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("username", username);
            data.put("phone", phone);

            return Result.success(data);

        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Object> login(@RequestBody Map<String, String> request) {
        try {
            String loginName = request.get("loginName");
            String password = request.get("password");
            String smsCode = request.get("smsCode");

            if (loginName == null) {
                return Result.error("登录账号不能为空");
            }

            User user = userService.login(loginName, password, smsCode);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("uid", user.getUid());
            userInfo.put("username", user.getUsername());
            userInfo.put("phone", user.getPhone());
            userInfo.put("email", user.getEmail());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("role", user.getRole());

            return Result.success(userInfo);

        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    //验证用户密码
    @GetMapping("/verifypassword")
    public Result verifypassword(@RequestParam String password,@RequestParam Integer uid){
        String upassword = userService.getPasswordByUserId(uid);
        if(upassword.equals(password)){
            return Result.success("验证通过");
        } else {
            return Result.error("密码错误");
        }
    }

    //修改用户信息
    @PutMapping("/updateinfo")
    public Result updateinfo(@RequestBody User user) {
        userService.updateinfo(user);
        return Result.success();
    }

    @PutMapping("/avator")
    public Result<String> avator(MultipartFile image) throws IOException {
        String url = aliOSSUtils.upload(image);
        System.out.println(url);
        return Result.success(url);
    }


}
