package net.myspring.basic.modules.sys.repository

import net.myspring.basic.modules.sys.domain.Backend
import net.myspring.basic.modules.sys.dto.BackendMenuDto
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

/**
 * Created by haos on 2017/5/24.
 */
interface BackendRepository{
    @Cacheable
     fun findOne(id: String): Backend

    @CachePut(key="#id")
    fun save(backend: Backend): Backend

    @Query("""
        SELECT t1.*
        FROM sys_backend t1
        where t1.enabled=1
        and t1.name like %?1%
     """, nativeQuery = true)
    fun findByNameLike(name:String):List<Backend>

    @Query("""
        SELECT
            t1.id,
            t1.name,
            t1.code,
            t2.id as 'moduleId',
            t2.name as 'moduleName',
            t2.code as 'moduleCode',
            t2.icon as 'moduleIcon',
            t3.id as 'categoryId',
            t3.name as 'categoryName',
            t3.code as 'categoryCode',
            t4.id as 'menuId',
            t4.name as 'menuName',
            t4.code as 'menuCode'
        FROM
            sys_backend t1,sys_backend_module t2,sys_menu_category t3,sys_menu t4
        where
        t4.menu_category_id=t3.id
        and t3.backend_module_id=t2.id
        and t2.backend_id=t1.id
        and t1.enabled=1
        and t2.enabled=1
        and t3.enabled=1
        and t4.enabled=1
        and t4.id IN ?1
     """, nativeQuery = true)
    fun findByMenuList(menuList:List<String>):List<BackendMenuDto>

    @Query("""
        SELECT
        t1.id,
        t1.name,
        t1.code,
        t2.id as 'moduleId',
        t2.name as 'moduleName',
        t2.code as 'moduleCode',
        t2.icon as 'moduleIcon',
        t3.id as 'categoryId',
        t3.name as 'categoryName',
        t3.code as 'categoryCode',
        t4.id as 'menuId',
        t4.name as 'menuName',
        t4.code as 'menuCode'
        FROM
        sys_backend t1,sys_backend_module t2,sys_menu_category t3,sys_menu t4,sys_role_module t5
        where
        t4.menu_category_id=t3.id
        and t5.backend_module_id=t2.id
        and t3.backend_module_id=t2.id
        and t2.backend_id=t1.id
        and t1.enabled=1
        and t2.enabled=1
        and t3.enabled=1
        and t4.enabled=1
        and t5.enabled=1
        and t5.role_id=:roleId
     """, nativeQuery = true)
    fun findByRoleId(@Param("roleId")roleId:String):List<BackendMenuDto>

    @Query("""
        SELECT
        t1.id,
        t1.name,
        t1.code,
        t2.id as 'moduleId',
        t2.name as 'moduleName',
        t2.code as 'moduleCode',
        t2.icon as 'moduleIcon',
        t3.id as 'categoryId',
        t3.name as 'categoryName',
        t3.code as 'categoryCode',
        t4.id as 'menuId',
        t4.name as 'menuName',
        t4.code as 'menuCode'
        FROM
        sys_backend t1,sys_backend_module t2,sys_menu_category t3,sys_menu t4,sys_role_module t5,sys_role_permission t6,sys_permission t7
        where
        t4.menu_category_id=t3.id
        and t5.backend_module_id=t2.id
        and t3.backend_module_id=t2.id
        and t2.backend_id=t1.id
        and t5.role_id=t6.role_id
        and t6.permission_id=t7.id
        and t7.menu_id=t4.id
        and t1.enabled=1
        and t2.enabled=1
        and t3.enabled=1
        and t4.enabled=1
        and t5.enabled=1
        and t6.enabled=1
        and t6.role_id=:roleId
     """, nativeQuery = true)
    fun findRolePermissionByRoleId(@Param("roleId")roleId:String):List<BackendMenuDto>

}