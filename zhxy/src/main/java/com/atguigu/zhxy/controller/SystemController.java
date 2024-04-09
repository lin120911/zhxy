package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.service.AdminService;
import com.atguigu.zhxy.service.StudentService;
import com.atguigu.zhxy.service.TeacherService;
import com.atguigu.zhxy.pojo.Admin;
import com.atguigu.zhxy.pojo.LoginForm;
import com.atguigu.zhxy.pojo.Student;
import com.atguigu.zhxy.pojo.Teacher;
import com.atguigu.zhxy.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Jwt;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/sms/system")
public class SystemController {//非类controller

    @Autowired
    private AdminService adminService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;




    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result updatePwd(
            @RequestHeader("token") String token,
            @PathVariable("oldPwd") String oldPwd,
            @PathVariable("newPwd") String newPwd
    ){
        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration){
            //token过期
            Result.fail().message("token失效，请重新登录后修改密码");
        }
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        oldPwd= MD5.encrypt(oldPwd);
        newPwd=MD5.encrypt(newPwd);
        switch (userType){
            case 1:
                QueryWrapper<Admin> queryWrapper1=new QueryWrapper();
                queryWrapper1.eq("id",userId.intValue());
                queryWrapper1.eq("password",oldPwd);
                Admin admin = adminService.getOne(queryWrapper1);
                if(admin!=null){
                    //修改
                    admin.setPassword(newPwd);
                    adminService.saveOrUpdate(admin);
                }else{
                    return Result.fail().message("原密码有误");
                }
                break;
            case 2:
                QueryWrapper<Student> queryWrapper2=new QueryWrapper();
                queryWrapper2.eq("id",userId.intValue());
                queryWrapper2.eq("password",oldPwd);
                Student student = studentService.getOne(queryWrapper2);
                if(student!=null){
                    //修改
                    student.setPassword(newPwd);
                    studentService.saveOrUpdate(student);
                }else{
                    return Result.fail().message("原密码有误");
                }
                break;
            case 3:
                QueryWrapper<Teacher> queryWrapper3=new QueryWrapper();
                queryWrapper3.eq("id",userId.intValue());
                queryWrapper3.eq("password",oldPwd);
                Teacher teacher = teacherService.getOne(queryWrapper3);
                if(teacher!=null){
                    //修改
                    teacher.setPassword(newPwd);
                    teacherService.saveOrUpdate(teacher);
                }else{
                    return Result.fail().message("原密码有误");
                }
                break;
        }
        return Result.ok();
    }


    // POST /sms/system/headerImgUpload
    @ApiOperation("文件上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(
            @ApiParam("头像文件") @RequestPart("multipartFile")  MultipartFile multipartFile
            ,
            HttpServletRequest request
    ){

        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String newFileName =uuid.concat(originalFilename.substring(i));

        // 保存文件 将文件发送到第三方/独立的图片服务器上,
        String portraitPath="F:\\zhxyProject\\zhxy\\target\\classes\\public\\upload\\".concat(newFileName);
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 响应图片的路径
        String path="upload/".concat(newFileName);
        return Result.ok(path);
    }


    @GetMapping("/getVerifiCodeImage")//生成验证码，并且将其code放入session，为提交做比较准备
    public void getVerifiCodeImage(HttpServletRequest request, HttpServletResponse response){
        //获取图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();
        //获取图片上的验证码
        String verifiCode=new String(CreateVerifiCodeImage.getVerifiCode());
        //将验证码文本放入session域，为下一次验证做准备
        HttpSession session = request.getSession();
        session.setAttribute("verifiCode",verifiCode);
        //将验证码图片响应给浏览器
        try {
            ImageIO.write(verifiCodeImage,"JPEG",response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @PostMapping("/login")
    public Result login(@RequestBody LoginForm loginForm,HttpServletRequest request){
        //验证码校验
        HttpSession session = request.getSession();
        String sessionVerifiCode = (String) session.getAttribute("verifiCode");
        String loginVerifiCode = loginForm.getVerifiCode();
        if("".equals(sessionVerifiCode)||null==sessionVerifiCode){//该处判断所存储的session是否为空，session里边已经没有验证码了==验证码实效，session只存在30min
            return Result.fail().message("验证码失效,请刷新后重试");
        }
        if(!sessionVerifiCode.equals(loginVerifiCode)){
            return Result.fail().message("验证码有误，请重试");
        }
        //从session域中移除验证码
        session.removeAttribute("verifiCode");
        //分用户类型进行校验

        //准备一个map用户存放响应的数据
        Map<String,Object> map=new LinkedHashMap<>();

        switch (loginForm.getUserType()){
            case 1:
                try {
                    Admin admin=adminService.login(loginForm);
                    if (null!=admin) {
                        //用户的类型和id转换成一个密文，以token的形式向客户端反馈
                        String token = JwtHelper.createToken(admin.getId().longValue(), 1);
                        map.put("token",token);
                    }else{
                        throw new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());//e.get==上面抛出的异常信息
                }

            case 2:
                try {
                Student student=studentService.login(loginForm);
                if (null!=student) {
                    //用户的类型和id转换成一个密文，以token的形式向客户端反馈
                    String token = JwtHelper.createToken(student.getId().longValue(), 2);
                    map.put("token",token);
                }else{
                    throw new RuntimeException("用户名或者密码有误");
                }
                return Result.ok(map);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Result.fail().message(e.getMessage());//e.get==上面抛出的异常信息
        }
            case 3:
                try {
                    Teacher teacher= teacherService.login(loginForm);
                    if (null!=teacher) {
                        //用户的类型和id转换成一个密文，以token的形式向客户端反馈
                        String token = JwtHelper.createToken(teacher.getId().longValue(), 3);
                        map.put("token",token);
                    }else{
                        throw new RuntimeException("用户名或者密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());//e.get==上面抛出的异常信息
                }
        }
        return Result.fail().message("查无此用户");
        //
    }

    @GetMapping("/getInfo")
    public Result getInfoByToken(@RequestHeader("token") String token){
        boolean expiration = JwtHelper.isExpiration(token);
        if (expiration) {
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
        //从token中解析用户id和用户类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        Map<String,Object>map=new LinkedHashMap<>();
        switch (userType){
            case 1:
                Admin admin=adminService.getAdminById(userId);
                map.put("userType",1);
                map.put("user",admin);
                break;
            case 2:
                Student student=studentService.getStudentById(userId);
                map.put("userType",2);
                map.put("user",student);
                break;
            case 3:
                Teacher teacher=teacherService.getTeacherById(userId);
                map.put("userType",3);
                map.put("user",teacher);
        }
        return Result.ok(map);
    }
}
