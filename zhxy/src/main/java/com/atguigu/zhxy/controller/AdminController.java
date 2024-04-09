package com.atguigu.zhxy.controller;


import com.atguigu.zhxy.pojo.Admin;
import com.atguigu.zhxy.service.AdminService;
import com.atguigu.zhxy.util.MD5;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  //为什么是RestController？
@RequestMapping("/sms/adminController")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @DeleteMapping("/deleteAdmin")
    public Result deleteAdmin(@RequestBody List<Integer>ids){
        adminService.removeByIds(ids);
        return Result.ok();
    }

    @PostMapping("/saveOrUpdateAdmin")
    public Result saveOrUpdateAdmin(@RequestBody Admin admin){
        Integer id = admin.getId();
        if (id==null||0==id) {//为什么id==0||id==null会空指针
            admin.setPassword(MD5.encrypt(admin.getPassword()));
        }
        adminService.saveOrUpdate(admin);
        return Result.ok();
    }

    @GetMapping("/getAllAdmin/{pageNo}/{pageSize}")
    public Result getAllAdmin(
            @PathVariable("pageNo")Integer pageNo,
            @PathVariable("pageSize")Integer pageSize,
            String adminName
    ){
        Page page=new Page(pageNo,pageSize);
        IPage<Admin> adminList=adminService.getAdminByName(page,adminName);

        return Result.ok(adminList);
    }
}
