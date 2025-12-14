package com.group.controller;

import com.group.entity.Order;
import com.group.entity.Result;
import com.group.entity.User;
import com.group.entity.UserIdentity;
import com.group.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
