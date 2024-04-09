package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Grade;
import com.atguigu.zhxy.service.GradeService;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "年级控制器")
@RestController
@RequestMapping("/sms/gradeController")
public class GradeController {

    @Autowired
    private GradeService gradeService;



    @ApiOperation("获取全部年级")
    @GetMapping("/getGrades")
    public Result getGrades(){
        List<Grade> grades=gradeService.getGrades();
        return Result.ok(grades);
    }




    @ApiOperation("分页条件下根据年级名称模糊查询")
    @GetMapping("/getGrades/{pageNo}/{pageSize}")//@PathVariable是用来提取请求参数的
    public Result getGrades(@ApiParam("分页查询的页码数")@PathVariable("pageNo") Integer pageNo,
                            @ApiParam("分页查询的页大小")@PathVariable("pageSize") Integer pageSize,
                            /*@RequestParam("gradeName")可以省略该方法，因为传入的参数名和方法的参数名一致*/
                            @ApiParam("分页查询的模糊匹配名称") String gradeName){
        //分页 带条件查询
        Page<Grade>page=new Page<>(pageNo,pageSize);//Mybatis-Plus自带Page类，用来处理分页信息
        //通过服务层
        IPage<Grade> pageRs=gradeService.getGradeByOpr(page,gradeName);

        //封装Result对象并返回
        return Result.ok(pageRs);
    }


    @ApiOperation("新增或者修改grade,有id属性是修改，没有则是增加")
    @PostMapping("/saveOrUpdateGrade")
    public Result saveOrUpdateGrade(@ApiParam("JSON格式的grade对象")@RequestBody Grade grade){
        //接收参数  因为是在请求体中的json串，所以需要RequestBody

        //调用服务层方法完成增减或者修改
        gradeService.saveOrUpdate(grade);
        return Result.ok();
    }
    @ApiOperation("删除grade信息")
    @DeleteMapping("/deleteGrade")
    public Result deleteGrade(@ApiParam("要删除的所有的grade的id的JSON集合") @RequestBody List<Integer>ids){
        gradeService.removeByIds(ids);
        return Result.ok();
    }
}
