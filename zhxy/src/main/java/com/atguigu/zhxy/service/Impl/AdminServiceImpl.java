package com.atguigu.zhxy.service.Impl;

import com.atguigu.zhxy.mapper.AdminMapper;
import com.atguigu.zhxy.service.AdminService;
import com.atguigu.zhxy.pojo.Admin;
import com.atguigu.zhxy.pojo.LoginForm;
import com.atguigu.zhxy.util.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("adminServiceImpl")
@Transactional
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public Admin login(LoginForm loginForm) {
        QueryWrapper<Admin> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name",loginForm.getUsername());
        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));

        Admin admin = baseMapper.selectOne(queryWrapper);
        return admin;
    }

    @Override
    public Admin getAdminById(Long userId) {
        QueryWrapper<Admin> queryWrapper=new QueryWrapper<Admin>();
        queryWrapper.eq("id",userId);
        Admin admin = baseMapper.selectOne(queryWrapper);
        return admin;
    }

    @Override
    public IPage<Admin> getAdminByName(Page page, String adminName) {
        QueryWrapper queryWrapper=new QueryWrapper();
        if (adminName!=null) {
            queryWrapper.like("name",adminName);
        }
        queryWrapper.orderByDesc("id");
        Page<Admin>adminPage=baseMapper.selectPage(page,queryWrapper);
        return adminPage;
    }
}
