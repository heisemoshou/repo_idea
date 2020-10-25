package com.lagou.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lagou.dao.UserMapper;
import com.lagou.domain.*;
import com.lagou.service.UserService;
import com.lagou.utils.Md5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageInfo<User> findAllUserByPage(UserVo userVo) {

        PageHelper.startPage(userVo.getCurrentPage(),userVo.getPageSize());
        List<User> allUserByPage = userMapper.findAllUserByPage(userVo);
        return new PageInfo<>(allUserByPage);
    }

    @Override
    public void updateUserStatus(Integer id, String status) {
        // 封装数据
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setUpdate_time(new Date());

        // 调用mapper
        userMapper.updateUserStatus(user);
    }

    @Override
    public User login(User user) throws Exception {

        // 1.调用mapper方法  login：包含了密文密码
        User login = userMapper.login(user);

        if (login != null && Md5.verify(user.getPassword(),"lagou",login.getPassword())){
            return login;
        } else {
            return null;
        }
    }

    // 分配角色回显
    @Override
    public List<Role> findUserRelationRoleById(Integer id) {
        return userMapper.findUserRelationRoleById(id);
    }

    @Override
    public void userContextRole(UserVo userVo) {
        // 1.根据用户id清空中间表关联关系
        userMapper.deleteUserContextRole(userVo.getUserId());

        // 2.重新建立关联关系
        List<Integer> roleIdList = userVo.getRoleIdList();
        for (Integer roleId : roleIdList) {

            // 封装数据
            User_Role_relation user_role_relation = new User_Role_relation();
            user_role_relation.setUserId(userVo.getUserId());
            user_role_relation.setRoleId(roleId);

            Date date = new Date();
            user_role_relation.setCreatedTime(date);
            user_role_relation.setUpdatedTime(date);

            user_role_relation.setCreatedBy("system");
            user_role_relation.setUpdatedby("system");

            userMapper.userContextRole(user_role_relation);
        }

    }

    // 获取用户权限信息
    @Override
    public ResponseResult getUserPermissions(Integer userId) {

        // 1.获取当前用户拥有的角色
        List<Role> roleList = userMapper.findUserRelationRoleById(userId);

        // 2.获取角色id，保存到List集合中
        List<Integer> roleIds = new ArrayList<>();
        // 遍历集合 单独保存角色id
        for (Role role : roleList) {
            roleIds.add(role.getId());
        }

        // 3.根据角色id查询父级菜单
        List<Menu> parentMenu = userMapper.findParentMenuByRoleId(roleIds);

        // 4.查询封装父菜单关联的子菜单
        for (Menu menu : parentMenu) {
            List<Menu> subMenu = userMapper.findSubMenuByPid(menu.getId());
            menu.setSubMenuList(subMenu);
        }

        // 5.获取资源信息
        List<Resource> resourceList = userMapper.findResourceByRoleId(roleIds);

        // 6.封装数据
        Map<String,Object> map = new HashMap<>();
        map.put("menuList",parentMenu);
        map.put("resourceList",resourceList);

        return new ResponseResult(true,200,"获取用户权限信息成功",map);
    }
}
