package net.myspring.basic.modules.sys.repository

import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.sys.domain.Menu
import net.myspring.basic.modules.sys.dto.MenuDto
import net.myspring.basic.modules.sys.dto.OfficeRuleDto
import net.myspring.basic.modules.sys.web.query.MenuQuery
import net.myspring.basic.modules.sys.web.query.OfficeRuleQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import javax.persistence.EntityManager

/**
 * Created by haos on 2017/5/24.
 */
interface  MenuRepository :BaseRepository<Menu,String>,MenuRepositoryCustom{
    @CachePut(key="#id")
    fun save(menu: Menu): Menu

    @Cacheable
    override fun findOne(id: String): Menu

    @Query("""
         SELECT
        t1.*
        FROM
        sys_menu t1
        where
        t1.enabled=1 and t1.id not in (select DISTINCT menu_id from sys_permission)
     """, nativeQuery = true)
    fun findByPermissionIsEmpty():MutableList<Menu>

    @Query("""
        SELECT
        t1.*
        FROM
        sys_menu t1
        where
        t1.enabled=1 and t1.id  in (select DISTINCT menu_id from sys_permission)
     """, nativeQuery = true)
    fun findByPermissionIsNotEmpty(): MutableList<Menu>

    @Query("""
            select t1.*
            from sys_menu t1
            where t1.enabled = 1 and t1.menu_category_id = ?1
     """, nativeQuery = true)
    fun findByMenuCategoryId(menuCategoryId:String):MutableList<Menu>

    @Query("""
       SELECT t1.*
        FROM sys_menu t1
        where t1.enabled=1
        and t1.visible=1
        <if test="isMobile">
            and t1.mobile=?2
            and t1.mobile_href is not null
        </if>
        and t1.id in ?1
     """, nativeQuery = true)
    fun findByMenuIdsAndMobile(menuIds:MutableList<String>,isMobile:Boolean):MutableList<Menu>

    @Query("""
       SELECT t1.*
        FROM sys_menu t1
        where t1.enabled=1
     """, nativeQuery = true)
    fun findAllEnabled():MutableList<Menu>

}


interface MenuRepositoryCustom{

    fun findPage(pageable: Pageable, menuQuery: MenuQuery): Page<MenuDto>?



}

class MenuRepositoryImpl @Autowired constructor(val entityManager: EntityManager): MenuRepositoryCustom{
    override fun findPage(pageable: Pageable, menuQuery: MenuQuery): Page<MenuDto>? {
        return null
    }


}