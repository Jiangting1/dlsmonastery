package net.myspring.basic.modules.sys.repository

import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.sys.domain.CompanyConfig
import net.myspring.basic.modules.sys.dto.CompanyConfigCacheDto
import net.myspring.basic.modules.sys.dto.CompanyConfigDto
import net.myspring.basic.modules.sys.web.query.CompanyConfigQuery
import net.myspring.util.repository.MySQLDialect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

/**
 * Created by haos on 2017/5/24.
 */
@CacheConfig(cacheNames = arrayOf("companyConfigs"))
interface CompanyConfigRepository :BaseRepository<CompanyConfig,String>, CompanyConfigRepositoryCustom{
    @CachePut(key="#p0.id")
    fun save(companyConfig: CompanyConfig): CompanyConfig

    @Cacheable
    override fun findOne(id: String): CompanyConfig

    @Query("""
        SELECT t
        FROM  #{#entityName} t
        where t.enabled=1
        and t.code=:code
     """)
    fun  findByCode(@Param("code")code:String):CompanyConfig
}

interface CompanyConfigRepositoryCustom{
    fun  findAllCache():MutableList<CompanyConfigCacheDto>

    fun findPage(pageable: Pageable, companyConfigQuery: CompanyConfigQuery): Page<CompanyConfigDto>?


}

class CompanyConfigRepositoryImpl @Autowired constructor(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate): CompanyConfigRepositoryCustom{
    override fun findAllCache(): MutableList<CompanyConfigCacheDto> {
        return namedParameterJdbcTemplate.query("""
                 SELECT t1.*
                FROM sys_company_config t1
                where t1.enabled=1
                """, BeanPropertyRowMapper(CompanyConfigCacheDto::class.java));
    }

    override fun findPage(pageable: Pageable, companyConfigQuery: CompanyConfigQuery): Page<CompanyConfigDto>? {
        var sql = StringBuilder("""
                SELECT
                t1.*
                FROM
                sys_company_config t1
                WHERE
                t1.enabled=1
            """);
        if(companyConfigQuery.name!=null){
            sql.append("""
                    AND t1.name LIKE CONCAT('%',:name,'%')
                """);
        }
        if(companyConfigQuery.code!=null){
            sql.append("""
                    AND t1.code LIKE CONCAT('%',:code,'%')
                """);
        }
        val pageableSql = MySQLDialect.getInstance().getPageableSql(sql.toString(),pageable)
        val countSql = MySQLDialect.getInstance().getCountSql(sql.toString())
        val list = namedParameterJdbcTemplate.query(pageableSql, BeanPropertySqlParameterSource(companyConfigQuery), BeanPropertyRowMapper(CompanyConfigDto::class.java))
        val count = namedParameterJdbcTemplate.queryForObject(countSql, BeanPropertySqlParameterSource(companyConfigQuery),Long::class.java)
        return PageImpl(list,pageable,count)
    }


}