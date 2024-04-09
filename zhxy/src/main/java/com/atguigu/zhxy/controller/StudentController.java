package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Student;
import com.atguigu.zhxy.service.StudentService;
import com.atguigu.zhxy.util.MD5;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sms/studentController")
public class StudentController {

    @Autowired
    private StudentService studentService;


    @DeleteMapping("/delStudentById")
    public Result delStudentById(
            @RequestBody List<Integer> ids
            ){
        studentService.removeByIds(ids);
        return Result.ok();
    }


    @PostMapping("/addOrUpdateStudent")
    public Result addOrUpdateStudent(
            @RequestBody Student student
    ){
        Integer id = student.getId();
        if (null==id||0==id) {
            student.setPassword(MD5.encrypt(student.getPassword()));
        }
        studentService.saveOrUpdate(student);
        return Result.ok();
    }

    @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
    public Result getStudentByOpr(@PathVariable Integer pageNo,
                                  @PathVariable Integer pageSize,
                                  Student student){
        //分页信息封装
        Page pageParam=new Page(pageNo,pageSize);
        IPage<Student> studentPage= studentService.getStudentByOpr(pageParam,student);
        return Result.ok(studentPage);
    }

}
