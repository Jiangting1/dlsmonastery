package net.myspring.tool.modules.oppo.repository

import com.google.common.collect.Maps
import net.myspring.tool.modules.oppo.domain.OppoCustomerAfterSaleImei
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class FutureAfterSaleRepository @Autowired constructor(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate){
    fun findAll(dateStart: String,dateEnd: String): MutableList<OppoCustomerAfterSaleImei>{
        val paramMap = Maps.newHashMap<String, Any>();
        paramMap.put("dateStart",dateStart);
        paramMap.put("dateEnd",dateEnd);
        return namedParameterJdbcTemplate.query("""
         select
            af.bad_depot_id as customerid,
            af.created_date as date,
            im.product_id as productCode,
            im.ime as imei,
            0 as transType
        from
            crm_after_sale af,
            crm_product_ime im
        where
            af.bad_product_ime_id = im.id
            and af.created_date >=:dateStart
            and af.created_date <:dateEnd
            and af.enabled = 1
        """,paramMap, BeanPropertyRowMapper(OppoCustomerAfterSaleImei::class.java));
    }
}