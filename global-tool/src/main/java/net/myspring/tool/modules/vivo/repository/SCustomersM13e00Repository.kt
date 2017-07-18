package net.myspring.tool.modules.vivo.repository

import com.google.common.collect.Maps
import net.myspring.tool.modules.vivo.domain.SCustomersM13e00
import net.myspring.tool.modules.vivo.dto.SCustomerDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class SCustomersM13e00Repository @Autowired constructor(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate){

    fun deleteAll():Int{
        val map = Maps.newHashMap<String,Any>()
        return namedParameterJdbcTemplate.update("DELETE FROM S_Customers_M13E00 WHERE 1=1",map)
    }

    fun findIDvivoCustomerIDs():MutableList<String>{
        val sql=("""
                select
                    distinct CustomerID
                from
                    S_Customers_R250082
                union
                select
                    distinct CustomerID
                from
                    S_Customers_R2500821
                union
                select
                        distinct CustomerID
                from
                    S_Customers_R2500822
                union
                select
                        distinct CustomerID
                from
                    S_Customers_R2500823
                """ )
        return namedParameterJdbcTemplate.query(sql,BeanPropertyRowMapper(String::class.java))
    }

    fun findVivoCustomers(date: LocalDate): MutableList<SCustomerDto> {
        val map = Maps.newHashMap<String, Any>()
        map.put("date",date)
        return namedParameterJdbcTemplate.query("""
            SELECT
                de.area_id AS customerId,
                de.NAME AS customerName,
                de.office_id AS zoneId,
                :date AS recordDate,
                1 AS customerLevel,
                '' AS customerStr1,
                de.area_id AS customerStr4
            FROM
                crm_depot de
            WHERE
                de.area_id IS NOT NULL
            OR de.area_id <> ''
            GROUP BY
                de.area_id
            UNION
                SELECT
                    de.id AS customerId,
                    de.NAME AS CustomerName,
                    de.area_id AS zoneId,
                    :date AS recordDate,
                    2 AS CustomerLevel,
                    de.area_type AS customerStr1,
                    de.area_id AS customerStr4
                FROM
                    crm_depot de
                WHERE
                    de.id IN (
                        SELECT
                            depot_id
                        FROM
                            crm_depot_shop
                        WHERE
                            depot_id NOT IN (
                                SELECT
                                    depot_id
                                FROM
                                    crm_depot_store
                            )
                    )
        """,map, BeanPropertyRowMapper(SCustomerDto::class.java))
    }


    fun batchSave(sCustomersM13e00List: MutableList<SCustomersM13e00>): IntArray? {
        val sb = StringBuffer()
        sb.append("""
           insert into S_Customers_M13E00
            (
                CustomerID,
                CustomerName,
                ZoneID,
                CompanyID,
                RecordDate,
                CustomerLevel,
                Customerstr1,
                Customerstr4,
                Customerstr10
            )
            values(
                :customerId,
                :customerName,
                :zoneId,
                :companyId,
                :recordDate,
                :customerLevel,
                :customerStr1,
                :customerStr4,
                :customerStr10
            )""")
        return namedParameterJdbcTemplate.batchUpdate(sb.toString(), SqlParameterSourceUtils.createBatch(sCustomersM13e00List.toTypedArray()))
    }
}