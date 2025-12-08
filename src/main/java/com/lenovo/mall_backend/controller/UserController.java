package com.lenovo.mall_backend.controller;

import com.lenovo.mall_backend.entity.PageBean;
import com.lenovo.mall_backend.entity.Result;
import com.lenovo.mall_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    //根据页码、每页的记录数、用户名（可选/模糊匹配）查询
    @GetMapping("/admin/users")
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       String username) {
        log.info("分页查询，参数{},{},{}",page,pageSize,username);
        PageBean pageBean = userService.page(page,pageSize,username);
        return Result.success(pageBean);
    }

    @DeleteMapping("admin/users/{id}")
    public Result deleteById(@PathVariable Integer id){
        log.info("根据id删除用户{}",id);
        userService.deleteById(id);
        return Result.success();
    }
}
