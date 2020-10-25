package com.lagou.service;

import com.lagou.domain.*;

import java.util.List;

public interface RoleService {

    // 查询所有角色&条件查询
    public List<Role> findAllRole(Role role);

    // 根据角色id查询该角色关联的菜单信息id [1,2,3,5...]
    public List<Integer> findMenuByRoleId(Integer roleId);

    // 为角色分配菜单
    public void roleContextMenu(RoleMenuVo roleMenuVo);

    // 删除角色
    public void deleteRole(Integer roleId);

    // 根据角色id 获取当前角色拥有的 资源信息
    public List<ResourceCategory> findResourceListByRoleId(Integer roleId);

    // 为角色分配资源信息
    public void roleContextResource(RoleResourceVo roleResourceVo);

}
