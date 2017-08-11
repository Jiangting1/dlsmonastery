package net.myspring.cloud.modules.kingdee.repository

import net.myspring.cloud.modules.kingdee.domain.CnBankAcnt
import net.myspring.util.time.LocalDateTimeUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

/**
 * 银行账户
 * Created by lihx on 2017/6/9.
 */
@Component
class CnBankAcntRepository @Autowired constructor(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate){

    fun findByNameList(nameList:List<String>):MutableList<CnBankAcnt>? {
        return namedParameterJdbcTemplate.query("""
            SELECT
                t1.FBANKACNTID,
                t1.FNUMBER,
                t1.FMODIFYDATE,
                t2.FNAME,
                t1.FForbidStatus,
                t1.FDOCUMENTSTATUS
            FROM
                T_CN_BANKACNT t1,
                T_CN_BANKACNT_L t2
            WHERE
                t1.FBANKACNTID = t2.FBANKACNTID
                and t1.FFORBIDSTATUS = 'A'
                and t1.FDOCUMENTSTATUS = 'C'
                and t2.FNAME in (:nameList)
        """, Collections.singletonMap("nameList",nameList), BeanPropertyRowMapper(CnBankAcnt::class.java))
    }

    fun findAll():MutableList<CnBankAcnt>? {
        return namedParameterJdbcTemplate.query("""
            SELECT
                t1.FBANKACNTID,
                t1.FNUMBER,
                t1.FMODIFYDATE,
                t2.FNAME,
                t1.FForbidStatus,
                t1.FDOCUMENTSTATUS
            FROM
                T_CN_BANKACNT t1,
                T_CN_BANKACNT_L t2
            WHERE
                t1.FBANKACNTID = t2.FBANKACNTID
                and t1.FFORBIDSTATUS = 'A'
                and t1.FDOCUMENTSTATUS = 'C'
        """, BeanPropertyRowMapper(CnBankAcnt::class.java))
    }

    fun findByMaxModifyDate(modifyDate: LocalDateTime) :MutableList<CnBankAcnt>? {
        return namedParameterJdbcTemplate.query("""
            SELECT
                t1.FBANKACNTID,
                t1.FNUMBER,
                t1.FMODIFYDATE,
                t2.FNAME,
                t1.FForbidStatus,
                t1.FDOCUMENTSTATUS
            FROM
                T_CN_BANKACNT t1,
                T_CN_BANKACNT_L t2
            WHERE
                t1.FBANKACNTID = t2.FBANKACNTID
                and t1.FFORBIDSTATUS = 'A'
                and t1.FDOCUMENTSTATUS = 'C'
                and t1.FMODIFYDATE > :modifyDate
        """,Collections.singletonMap("modifyDate", LocalDateTimeUtils.format(modifyDate)), BeanPropertyRowMapper(CnBankAcnt::class.java))
    }
}