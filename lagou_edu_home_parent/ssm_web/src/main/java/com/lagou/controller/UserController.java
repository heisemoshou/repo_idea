package com.lagou.controller;

import com.github.pagehelper.PageInfo;
import com.lagou.domain.ResponseResult;
import com.lagou.domain.Role;
import com.lagou.domain.User;
import com.lagou.domain.UserVo;
import com.lagou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 分页获取用户数据&条件查询用户数据
    @RequestMapping("/findAllUserByPage")
    public ResponseResult findAllUserByPage(@RequestBody UserVo userVo){

        PageInfo page = userService.findAllUserByPage(userVo);
        return new ResponseResult(true,200,"分页多条件查询成功",page);
    }

    // 修改用户状态
    @RequestMapping("/updateUserStatus")
    public ResponseResult updateUserStatus(Integer id,String status){

        userService.updateUserStatus(id, status);
        Map<String,String> map = new HashMap<>();
        map.put("status",status);
        return new ResponseResult(true,200,"修改成功",map);
    }

    // 用户登录
    @RequestMapping("/login")
    public ResponseResult login(User user, HttpServletRequest request) throws Exception {

        User login = userService.login(user);

        if (login != null){

            // 保存用户id及access_token到session中
            HttpSession session = request.getSession();
            String access_token = UUID.randomUUID().toString();
            session.setAttribute("access_token",access_token);
            session.setAttribute("user_id",login.getId());

            // 将查询出来的信息响应给前台
            Map<String,Object> map = new HashMap<>();
            map.put("access_token",access_token);
            map.put("user_id",login.getId());

            return new ResponseResult(true,200,"登陆成功",map);

        } else {
            return new ResponseResult(true,400,"用户名或密码错误",null);
        }
    }

    // 分配角色(回显)
    @RequestMapping("/findUserRoleById")
    public ResponseResult  findUserRelationRoleById(Integer id){

        List<Role> roleById = userService.findUserRelationRoleById(id);

        return new ResponseResult(true,200,"分配角色回显成功",roleById);
    }

    // 分配角色
    @RequestMapping("/userContextRole")
    public ResponseResult userContextRole(@RequestBody UserVo userVo){

        userService.userContextRole(userVo);
        return new ResponseResult(true,200,"分配角色成功",null);
    }

    // 获取用户拥有的菜单权限
    @RequestMapping("/getUserPermissions")
    public ResponseResult geiUserPermissions(HttpServletRequest request){

        // 1.获取请求头中的token
        String header_token = request.getHeader("Authorization");

        // 2.获取session中的token
        HttpSession session = request.getSession();
        String session_token = (String) session.getAttribute("access_token");

        // 3.判断token是否一致
        if (header_token.equals(session_token)) {
            // 获取用户id
            Integer user_id = (Integer) request.getSession().getAttribute("user_id");
            // 调用Service进行菜单查询
            return userService.getUserPermissions(user_id);
        } else {
            return new ResponseResult(true,400,"获取菜单信息失败",null);
        }
    }
}
