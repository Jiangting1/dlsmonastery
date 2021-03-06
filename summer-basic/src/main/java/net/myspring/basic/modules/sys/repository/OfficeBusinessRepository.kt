package net.myspring.basic.modules.sys.repository

import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.sys.domain.OfficeBusiness
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

/**
 * Created by haos on 2017/5/24.
 */
@CacheConfig(cacheNames = arrayOf("officeBusinesses"))
interface  OfficeBusinessRepository: BaseRepository<OfficeBusiness, String> {
    @CachePut(key="#p0.id")
    fun save(officeBusiness: OfficeBusiness): OfficeBusiness

    @Cacheable
    override  fun findOne(id: String): OfficeBusiness

    @Query("""
        SELECT t
        FROM  #{#entityName} t
        where t.enabled=1
        and t.officeId=?1
     """)
    fun findBusinessIdById(id:String):MutableList<OfficeBusiness>

    @Query("""
        SELECT t
        FROM  #{#entityName} t
        where  t.officeId=?1
     """)
    fun findAllBusinessIdById(id:String):MutableList<OfficeBusiness>

    @Query("""
             UPDATE  #{#entityName} t set t.enabled=?1 where t.businessOfficeId IN ?2 and t.officeId=?3
     """)
    @Modifying
    fun setEnabledByOfficeAndBusinessOfficeIds(enabled: Boolean,businessOfficeIds:MutableList<String>,officeId: String):Int

    @Query("""
           UPDATE  #{#entityName} t set t.enabled=?1 where t.officeId IN ?2
     """)
    @Modifying
    fun setEnabledByOfficeId(enabled:Boolean,officeId:String ):Int
}