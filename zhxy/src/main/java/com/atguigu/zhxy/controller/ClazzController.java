package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Clazz;
import com.atguigu.zhxy.service.ClazzService;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sms/clazzController")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    @GetMapping("/getClazzs")
    public Result getClazzs(){
        List<Clazz> clazzes=clazzService.getClazzs();
        return Result.ok(clazzes);
    }


    @DeleteMapping("/deleteClazz")
    public Result deleteClazz(@RequestBody List<Integer> clazzList){
        clazzService.removeByIds(clazzList);
        return Result.ok();
    }

    @PostMapping("/saveOrUpdateClazz")
    public Result saveOrUpdateClazz(@RequestBody Clazz clazz){
        clazzService.saveOrUpdate(clazz);
        return Result.ok();
    }

    @ApiOperation("分页带条件查询班级信息")
    @GetMapping("/getClazzsByOpr/{pageNo}/{pageSize}")
    public Result getClazzByOpr(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,
                                @ApiParam("分页查询的页大小")@PathVariable("pageSize") Integer pageSize,
                                @ApiParam("分页查询的条件") Clazz clazz
    ){
        Page<Clazz>page=new Page<>(pageNo,pageSize);
        IPage<Clazz> ipage= clazzService.getClazzByOpr(page,clazz);
        return Result.ok(ipage);
    }
}
