package net.myspring.future.modules.basic.repository

import net.myspring.future.common.repository.BaseRepository
import net.myspring.future.modules.basic.domain.PricesystemDetail
import org.springframework.data.repository.query.Param
import org.springframework.data.jpa.repository.Query

/**
 * Created by zhangyf on 2017/5/24.
 */
interface PricesystemDetailRepository : BaseRepository<PricesystemDetail,String>{

    fun findByPricesystemIdIn(pricesystemIds: MutableList<String>): MutableList<PricesystemDetail>

    fun findByPricesystemIdAndProductId( pricesystemId: String, productId: String): PricesystemDetail?

    fun findByProductId(productId:String): MutableList<PricesystemDetail>

    fun findByPricesystemId(pricesystemId: String): MutableList<PricesystemDetail>

    @Query("""
        SELECT
            t1.*
        FROM
            crm_pricesystem_detail t1,
            crm_depot t2
        WHERE
            t1.pricesystem_id = t2.pricesystem_id
        AND t2.id = ?1
    """, nativeQuery = true)
    fun findByDepotId(depotId: String): MutableList<PricesystemDetail>
}