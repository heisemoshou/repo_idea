package com.lagou.controller;

import com.github.pagehelper.PageInfo;
import com.lagou.domain.PromotionAd;
import com.lagou.domain.PromotionAdVo;
import com.lagou.domain.ResponseResult;
import com.lagou.service.PromotionAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/PromotionAd")
public class PromotionAdController {

    @Autowired
    private PromotionAdService promotionAdService;

    // 广告分页查询
    @RequestMapping("/findAllPromotionAdByPage")
    public ResponseResult findAllPromotionAdByPage(PromotionAdVo promotionAdVo){

        PageInfo<PromotionAd> pageInfo = promotionAdService.findAllPromotionAdByPage(promotionAdVo);
        return new ResponseResult(true,200,"广告分页查询成功",pageInfo);
    }

    // 图片上传
    @RequestMapping("/PromotionAdUpload")
    public ResponseResult fileUpLoad(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {

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

    // 广告动态上下线
    @RequestMapping("/updatePromotionAdStatus")
    public ResponseResult updatePromotionAdStatus(Integer id,Integer status){
        promotionAdService.updatePromotionAdStatus(id,status);
        Map<String,Integer> map = new HashMap<>();
        map.put("status",status);
        return new ResponseResult(true,200,"响应成功",map);
    }

    // 新建&修改广告接口
    @RequestMapping("/saveOrUpdatePromotionAd")
    public ResponseResult saveOrUpdatePromotionAd(@RequestBody PromotionAd promotionAd){
        if (promotionAd.getId() == null){
            promotionAdService.savePromotionAd(promotionAd);
            return new ResponseResult(true,200,"新建广告接口成功",null);
        } else {
            promotionAdService.updatePromotionAd(promotionAd);
            return new ResponseResult(true,200,"修改广告接口成功",null);
        }
    }

    @RequestMapping("/findPromotionAdById")
    public ResponseResult findPromotionAdById(Integer id){
        PromotionAd promotionAd = promotionAdService.findPromotionAdById(id);
        return new ResponseResult(true,200,"查询具体广告信息成功",promotionAd);
    }
}
