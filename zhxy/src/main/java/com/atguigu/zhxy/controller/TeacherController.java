package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Teacher;
import com.atguigu.zhxy.service.TeacherService;
import com.atguigu.zhxy.util.MD5;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sms/teacherController")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/getTeachers/{pageNo}/{pageSize}")
    public Result getTeachers(
            @PathVariable("pageNo")Integer pageNo,
            @PathVariable("pageSize")Integer pageSize,
            Teacher teacher
    ){
        Page<Teacher>pageParam=new Page<>(pageNo,pageSize);
        IPage<Teacher> page=teacherService.getTeachersByOpr(pageParam,teacher);
        return Result.ok(page);
    }

    @PostMapping("/saveOrUpdateTeacher")
    public Result saveOrUpdateTeacher(@RequestBody Teacher teacher){
        if (teacher.getId()==null||teacher.getId()==0) {
            teacher.setPassword(MD5.encrypt(teacher.getPassword()));
        }
        teacherService.saveOrUpdate(teacher);
        return Result.ok();
    }

    @DeleteMapping("/deleteTeacher")
    public Result deleteTeacher(@RequestBody List<Integer> ids){
        teacherService.removeByIds(ids);
        return Result.ok();
    }
}
