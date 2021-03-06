package net.myspring.future.modules.layout.repository

import net.myspring.future.common.repository.BaseRepository
import net.myspring.future.modules.layout.domain.ShopAllot
import net.myspring.future.modules.layout.dto.ShopAllotDto
import net.myspring.future.modules.layout.web.query.ShopAllotQuery
import net.myspring.util.repository.MySQLDialect
import net.myspring.util.text.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.LocalDateTime
import java.util.*


interface ShopAllotRepository : BaseRepository<ShopAllot,String>,ShopAllotRepositoryCustom{

    @Query("""
    select
        max(t.businessId)
    from
        #{#entityName} t
    where  t.createdDate >= ?1
    """)
    fun findMaxBusinessId(localDateTime: LocalDateTime): String?


}

interface ShopAllotRepositoryCustom{
    fun findPage(pageable: Pageable, shopAllotQuery: ShopAllotQuery): Page<ShopAllotDto>

    fun findShopAllotDto(id: String): ShopAllotDto
}

class ShopAllotRepositoryImpl @Autowired constructor(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate ):ShopAllotRepositoryCustom{
    override fun findShopAllotDto(id: String): ShopAllotDto {
        return namedParameterJdbcTemplate.queryForObject("""
       SELECT
            sum(t2.qty * t2.sale_price) saleTotalPrice,
            sum(t2.qty * t2.return_price) returnTotalPrice,
            fromShop.client_id fromShopClientId,
            fromShop.name fromShopName,
            toShop.client_id toShopClientId,
            toShop.name toShopName,
            fromClient.out_id fromShopClientOutId,
            toClient.out_id toShopClientOutId,
            t1.*
        FROM
            crm_shop_allot t1
            LEFT JOIN crm_shop_allot_detail t2 ON t1.id = t2.shop_allot_id
            LEFT JOIN crm_depot fromShop ON t1.from_shop_id = fromShop.id
            LEFT JOIN crm_client fromClient ON fromShop.client_id = fromClient.id
            LEFT JOIN crm_depot toShop ON t1.to_shop_id = toShop.id
            LEFT JOIN crm_client toClient ON toShop.client_id = toClient.id
        WHERE
            t1.enabled = 1
        AND t1.id = :id
        GROUP BY
            t1.id
          """, Collections.singletonMap("id", id), BeanPropertyRowMapper(ShopAllotDto::class.java))
    }

    override fun findPage(pageable: Pageable, shopAllotQuery: ShopAllotQuery): Page<ShopAllotDto> {
        val sb = StringBuffer()
        sb.append("""
        SELECT
                fromShop.name fromShopName,
                fromShop.client_id fromShopClientId,
                toShop.name toShopName,
                toShop.client_id toShopClientId,
                t1.*
        FROM  crm_shop_allot t1
                   LEFT JOIN crm_depot fromShop ON t1.from_shop_id = fromShop.id
                   LEFT JOIN crm_depot toShop ON t1.to_shop_id = toShop.id
        where t1.enabled=1
        """)
        if(StringUtils.isNotBlank(shopAllotQuery.fromShopId)){
            sb.append("""   and t1.from_shop_id= :fromShopId  """)
        }
        if(StringUtils.isNotBlank(shopAllotQuery.toShopId)){
            sb.append("""  and t1.to_shop_id= :toShopId  """)
        }
        if(StringUtils.isNotBlank(shopAllotQuery.status)){
            sb.append("""  and t1.status= :status  """)
        }
        if(StringUtils.isNotBlank(shopAllotQuery.businessId)){
            sb.append("""  and t1.business_id like concat('%', :businessId,'%') """)
        }
        if(shopAllotQuery.businessIdList!=null){
            sb.append("""  and t1.business_id IN  (:businessIdList) """)
        }
        if(shopAllotQuery.createdDateStart != null){
            sb.append("""  AND t1.created_date >= :createdDateStart """)
        }
        if(shopAllotQuery.createdDateEnd != null){
            sb.append("""  AND t1.created_date < :createdDateEnd """)
        }
        if(shopAllotQuery.auditDateStart != null){
            sb.append("""  AND t1.audit_date >= :auditDateStart """)
        }
        if(shopAllotQuery.auditDateEnd != null){
            sb.append("""   AND t1.audit_date < :auditDateEnd """)
        }

        val pageableSql = MySQLDialect.getInstance().getPageableSql(sb.toString(),pageable)
        val countSql = MySQLDialect.getInstance().getCountSql(sb.toString())
        val paramMap = BeanPropertySqlParameterSource(shopAllotQuery)
        val list = namedParameterJdbcTemplate.query(pageableSql,paramMap, BeanPropertyRowMapper(ShopAllotDto::class.java))
        val count = namedParameterJdbcTemplate.queryForObject(countSql, paramMap, Long::class.java)
        return PageImpl(list,pageable,count)

    }
}