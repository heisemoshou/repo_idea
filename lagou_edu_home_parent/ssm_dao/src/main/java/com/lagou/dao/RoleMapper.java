package com.lagou.dao;

import com.lagou.domain.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {

    // 查询所有角色&条件查询
    public List<Role> findAllRole(Role role);

    // 根据角色id查询该角色关联的菜单信息id [1,2,3,5...]
    public List<Integer> findMenuByRoleId(Integer roleId);

    // 根据roleId清空中间表的关联关系
    public void deleteRoleContextMenu(Integer rid);

    // 为角色分配菜单信息
    public void roleContextMenu(Role_menu_relation role_menu_relation);

    // 删除角色
    public void deleteRole(Integer roleId);

    // 查询当前角色拥有的资源分类信息
    public List<ResourceCategory> findResourceCategoryListByRoleId(Integer roleId);

    // 查询当前角色拥有的资源信息
    public List<Resource> findSubResourceByCategoryId(@Param("roleId") Integer roleId,@Param("categoryId") Integer categoryId);

    // 根据角色ID 删除角色与资源的关联关系
    public void deleteResourceByRoleId(Integer roleId);

    // 为角色分配资源, 删除完成后 ,插入最新的关联关系
    public void roleContextResource(RoleResourceRelation roleResourceRelation);
}
