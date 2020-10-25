package com.lagou.controller;

import com.lagou.domain.Course;
import com.lagou.domain.CourseVo;
import com.lagou.domain.ResponseResult;
import com.lagou.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // 组合注解
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // 多条件课程列表查询
    @RequestMapping("/findAllCourse")
    public ResponseResult findCourseByCondition(@RequestBody CourseVo courseVo){

        // 调用Service
        List<Course> courseList = courseService.findCourseByCondition(courseVo);

        return new ResponseResult(true,200,"响应成功",courseList);
    }

    // 课程图片上传
    @RequestMapping("/courseUpload")
    public ResponseResult fileUpLoad(@RequestParam("file")MultipartFile file, HttpServletRequest request) throws IOException {

        // 1.判断接收到的上传文件是否为空
        if (file.isEmpty()) {
            // 如果为空 直接抛出异常
            throw new RuntimeException();
        }

        // 2.获取项目部署路径
        // E:\JAVA\software\Tomcat\apache-tomcat-8.5.57\webapps\ssm-web
        String realPath = request.getServletContext().getRealPath("/");
        // E:\JAVA\software\Tomcat\apache-tomcat-8.5.57\webapps\
        String substring = realPath.substring(0, realPath.indexOf("ssm-web"));

        // 3.获取原文件名
        String originalFilename = file.getOriginalFilename();

        // 4.生成新文件名
        String newFileName = System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));

        // 5.文件上传
        // 目标目录：E:\JAVA\software\Tomcat\apache-tomcat-8.5.57\webapps\ upload\
        String uploadPath = substring + "upload\\";
        File filePath = new File(uploadPath, newFileName);

        // 如果目录不存在就创建目录
        if (!filePath.getParentFile().exists()){
            filePath.getParentFile().mkdirs();
            System.out.println("创建目录：" + filePath);
        }

        // 图片就进行真正的上传了
        file.transferTo(filePath);

        // 6.将文件名和文件路径返回，进行响应 (回显)
        Map<String, String> map = new HashMap<>();
        map.put("fileName",newFileName);
        map.put("filePath","http://localhost:8080/upload/" + newFileName);

        return new ResponseResult(true, 200, "图片上传成功", map);
    }

    // 新增课程信息以及讲师信息
    @RequestMapping("/saveOrUpdateCourse")
    public ResponseResult saveOrUpdateCourse(@RequestBody CourseVo courseVo) throws InvocationTargetException, IllegalAccessException {

        if (courseVo.getId() == null){
            // 调用Service
            courseService.saveCourseOrTeacher(courseVo);
            return new ResponseResult(true,200,"新增成功",null);
        }else {
            courseService.updateCourseOrTeacher(courseVo);
            return new ResponseResult(true,200,"修改成功",null);
        }
    }

    // 根据id查询具体的课程信息及关联的讲师信息 get请求不需要@RequestBody
    @RequestMapping("/findCourseById")
    public ResponseResult findCourseById(Integer id){

        CourseVo courseVo = courseService.findCourseById(id);

        return new ResponseResult(true,200,"根据id查询课程信息成功",courseVo);
    }

    // 课程状态管理
    @RequestMapping("/updateCourseStatus")
    public ResponseResult updateCourseStatus(Integer id,Integer status){

        // 调用Service 传递参数 完成课程状态的变更
        courseService.updateCourseStatus(id,status);

        // 响应数据
        Map<String,Object> map = new HashMap<>();
        map.put("status",status);

        return new ResponseResult(true,200,"课程状态变更成功",map);
    }

}
