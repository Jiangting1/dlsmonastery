package net.myspring.future.modules.basic.repository

import net.myspring.future.common.repository.BaseRepository
import net.myspring.future.modules.basic.domain.Chain
import net.myspring.future.modules.basic.dto.ChainDto
import net.myspring.future.modules.basic.web.query.ChainQuery
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

/**
 * Created by zhangyf on 2017/5/24.
 */
@CacheConfig(cacheNames = arrayOf("chains"))
interface ChainRepository : BaseRepository<Chain,String> {

    @Cacheable
    override fun findOne(id: String): Chain

    override fun findAll(): List<Chain>

    @CachePut(key = "#id")
    fun save(chain: Chain): Int

    @Query("""
        SELECT t1.*
        FROM crm_chain t1
        where t1.enabled=1
    """, nativeQuery = true)
    fun findAllEnabled(): List<Chain>

    fun findByIds(ids: List<String>): List<Chain>

    @Query("""
        select id from crm_depot where chain_id=?1
    """, nativeQuery = true)
    fun findDepotIds(id: String): List<String>
}