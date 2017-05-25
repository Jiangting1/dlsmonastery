
package net.myspring.basic.modules.sys.repository

import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.sys.domain.DictMap
import net.myspring.basic.modules.sys.dto.DictMapDto
import net.myspring.basic.modules.sys.web.query.DictMapQuery
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query

/**
 * Created by haos on 2017/5/24.
 */
interface  DictMapRepository :BaseRepository<DictMap,String>{
    @CachePut(key="#id")
    fun save(dictMap: DictMap): DictMap

    @Cacheable
    override fun findOne(id: String): DictMap

    @Query("""
         SELECT DISTINCT
        t1.category
        FROM
        sys_dict_map t1
     """, nativeQuery = true)
    fun findDistinctCategory():List<String>

    @Query("""
        SELECT
        t1.*
        FROM
        sys_dict_map t1
        WHERE
        t1.category=?1
        and t1.enabled=1
     """, nativeQuery = true)
    fun findByCategory(category:String):List<DictMap>

    @Query("""
         SELECT
        t1.*
        FROM
        sys_dict_map t1
        WHERE
        t1.name=?1
        and t1.enabled=1
     """, nativeQuery = true)
    fun findByName(name:String): DictMapDto

    fun findPage(pageable: Pageable, dictMapQuery: DictMapQuery): Page<DictMapDto>
}