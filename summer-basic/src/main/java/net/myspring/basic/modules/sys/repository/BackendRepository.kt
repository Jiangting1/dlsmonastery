package net.myspring.basic.modules.sys.repository

import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.sys.domain.Backend
import net.myspring.basic.modules.sys.dto.BackendDto
import net.myspring.basic.modules.sys.dto.BackendMenuDto
import net.myspring.basic.modules.sys.dto.CompanyConfigDto
import net.myspring.basic.modules.sys.web.query.BackendQuery
import net.myspring.basic.modules.sys.web.query.CompanyConfigQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import javax.persistence.EntityManager

/**
 * Created by haos on 2017/5/24.
 */
@CacheConfig(cacheNames = arrayOf("backends"))
interface BackendRepository:BaseRepository<Backend,String>,BackendRepositoryCustom{
    @Cacheable
    override fun findOne(id: String): Backend

    @CachePut(key="#id")
    fun save(backend: Backend): Backend

    @Query("""
        SELECT t1.*
        FROM sys_backend t1
        where t1.enabled=1
        and t1.name like %?1%
     """, nativeQuery = true)
    fun findByNameLike(name:String):MutableList<Backend>

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
    fun findByMenuList(menuList:MutableList<String>):MutableList<BackendMenuDto>

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
    fun findByRoleId(@Param("roleId")roleId:String):MutableList<BackendMenuDto>

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
    fun findRolePermissionByRoleId(@Param("roleId")roleId:String):MutableList<BackendMenuDto>


}


interface BackendRepositoryCustom{
    fun findAllEnabled():MutableList<Backend>?

    fun findPage(pageable: Pageable,backendQuery: BackendQuery): Page<BackendDto>?

}

class BackendRepositoryImpl @Autowired constructor(val entityManager: EntityManager): BackendRepositoryCustom{
    override fun findAllEnabled(): MutableList<Backend>? {
        return null
    }

    override fun findPage(pageable: Pageable, backendQuery: BackendQuery): Page<BackendDto>? {
        return null
    }


}