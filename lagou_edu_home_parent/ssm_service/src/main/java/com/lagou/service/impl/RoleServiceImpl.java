package com.lagou.service.impl;

import com.lagou.dao.RoleMapper;
import com.lagou.domain.*;
import com.lagou.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Role> findAllRole(Role role) {
        return roleMapper.findAllRole(role);
    }

    @Override
    public List<Integer> findMenuByRoleId(Integer roleId) {
        return roleMapper.findMenuByRoleId(roleId);
    }

    @Override
    public void roleContextMenu(RoleMenuVo roleMenuVo) {

        // 1.清空中间表的关联关系
        roleMapper.deleteRoleContextMenu(roleMenuVo.getRoleId());

        // 2.为角色分配菜单
        for (Integer mid : roleMenuVo.getMenuIdList()) {
            Role_menu_relation role_menu_relation = new Role_menu_relation();
            role_menu_relation.setMenuId(mid);
            role_menu_relation.setRoleId(roleMenuVo.getRoleId());

            // 封装数据
            Date date = new Date();
            role_menu_relation.setCreatedTime(date);
            role_menu_relation.setUpdatedTime(date);

            role_menu_relation.setCreatedBy("system");
            role_menu_relation.setUpdatedby("system");

            roleMapper.roleContextMenu(role_menu_relation);
        }
    }

    @Override
    public void deleteRole(Integer roleId) {

        // 调用根据roleId清空中间表
        roleMapper.deleteRoleContextMenu(roleId);

        // 调用删除方法
        roleMapper.deleteRole(roleId);
    }

    @Override
    public List<ResourceCategory> findResourceListByRoleId(Integer roleId) {
        // 查询角色关联的资源分类
        List<ResourceCategory> resourceCategoryList = roleMapper.findResourceCategoryListByRoleId(roleId);
        for (ResourceCategory resourceCategory : resourceCategoryList) {
            List<Resource> resources = roleMapper.findSubResourceByCategoryId(roleId, resourceCategory.getId());
            resourceCategory.setResourceList(resources);
        }
        return resourceCategoryList;
    }

    @Override
    public void roleContextResource(RoleResourceVo roleResourceVo) {
        // 清空之前的关联关系
        roleMapper.deleteResourceByRoleId(roleResourceVo.getRoleId());

        // 补全信息
        List<Integer> resourceIdList = roleResourceVo.getResourceIdList();
        for (Integer resourceId : resourceIdList) {
            RoleResourceRelation rrr = new RoleResourceRelation();
            rrr.setRoleId(roleResourceVo.getRoleId());
            rrr.setResourceId(resourceId);
            Date date = new Date();
            rrr.setCreatedTime(date);
            rrr.setUpdatedTime(date);
            rrr.setCreatedBy("system");
            rrr.setUpdatedBy("system");
            roleMapper.roleContextResource(rrr);
        }
    }

}
