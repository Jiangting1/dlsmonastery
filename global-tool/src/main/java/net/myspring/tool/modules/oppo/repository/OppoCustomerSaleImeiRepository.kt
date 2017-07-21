package net.myspring.tool.modules.oppo.repository;
import com.google.common.collect.Maps
import net.myspring.tool.common.repository.BaseRepository
import net.myspring.tool.modules.oppo.domain.OppoCustomerSaleCount
import net.myspring.tool.modules.oppo.domain.OppoCustomerSaleImei
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate


interface OppoCustomerSaleImeiRepository : BaseRepository<OppoCustomerSaleImei, String>, OppoCustomerSaleImeiRepositoryCustom {

}
interface OppoCustomerSaleImeiRepositoryCustom{
    fun findAll(dateStart: String,dateEnd: String): MutableList<OppoCustomerSaleImei>
    fun findByDate(dateStart:String,dateEnd:String):MutableList<OppoCustomerSaleImei>
    fun deleteByDate(dateStart: String,dateEnd: String):Int
}
class OppoCustomerSaleImeiRepositoryImpl @Autowired constructor(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : OppoCustomerSaleImeiRepositoryCustom{
    override fun findAll(dateStart: String, dateEnd: String): MutableList<OppoCustomerSaleImei> {
        val paramMap = Maps.newHashMap<String, Any>();
        paramMap.put("dateStart", dateStart);
        paramMap.put("dateEnd", dateEnd);

        return namedParameterJdbcTemplate.query("""
             select
                    im.ime as imei,
                    sa.created_date as saletime,
                    sa.buyer as custname,
                    sa.buyer_phone as custmobile,
                    sa.buyer_sex as custsex,
                    sa.employee_id as salepromoter ,
                    de.id as shopcode,
                    de.name as shopname,
                    de.district_id as province
            from
                    crm_product_ime_sale sa,
                    crm_product_ime im,
                    crm_depot de
            where
                    sa.created_date >=:dateStart
                    and sa.created_date < :dateEnd
                    and sa.product_ime_id = im.id
                    and sa.shop_id = de.id
                    and sa.is_back = 0
                    and sa.enabled = 1
             order by sa.shop_id asc
                """, paramMap, BeanPropertyRowMapper(OppoCustomerSaleImei::class.java));
    }

    override fun findByDate(dateStart:String, dateEnd:String): MutableList<OppoCustomerSaleImei> {
        val paramMap = Maps.newHashMap<String, Any>();
        paramMap.put("dateStart",dateStart);
        paramMap.put("dateEnd",dateEnd);
        return namedParameterJdbcTemplate.query("""
            select *  from oppo_push_customer_sale_imei where saletime >=:dateStart and saletime <:dateEnd
         """,paramMap,BeanPropertyRowMapper(OppoCustomerSaleImei::class.java));
    }

    override fun deleteByDate(dateStart: String, dateEnd: String): Int {
        val map = Maps.newHashMap<String,String>()
        map.put("dateStart",dateStart)
        map.put("dateEnd",dateEnd)
        return namedParameterJdbcTemplate.update("delete from oppo_push_customer_sale_imei where saletime >=:dateStart and saletime <:dateEnd",map)
    }
}
