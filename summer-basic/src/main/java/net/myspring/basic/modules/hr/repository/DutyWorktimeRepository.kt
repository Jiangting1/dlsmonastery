package net.myspring.basic.modules.hr.repository

import net.myspring.basic.common.repository.BaseRepository
import net.myspring.basic.modules.hr.domain.Account
import net.myspring.basic.modules.hr.domain.DutyPublicFree
import net.myspring.basic.modules.hr.domain.DutyWorktime
import net.myspring.basic.modules.hr.dto.DutyWorktimeDto
import net.myspring.basic.modules.hr.web.query.DutyWorktimeQuery
import net.myspring.basic.modules.sys.dto.BackendDto
import net.myspring.util.collection.CollectionUtil
import net.myspring.util.repository.MySQLDialect
import net.myspring.util.text.StringUtils
import org.springframework.data.repository.query.Param
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityManager

/**
 * Created by lihx on 2017/5/24.
 */
interface DutyWorktimeRepository : BaseRepository<DutyWorktime,String>,DutyWorktimeRepositoryCustom{

    @Query("""
        SELECT
        t1
        FROM #{#entityName} t1
        WHERE
        t1.enabled=1
        and t1.employeeId=:employeeId
        AND t1.dutyDate >= :dateStart
        and t1.dutyDate <= :dateEnd
    """)
    fun findByEmployeeAndDate(@Param("employeeId") employeeId: String, @Param("dateStart") dateStart: LocalDate, @Param("dateEnd") dateEnd: LocalDate): MutableList<DutyWorktime>

    @Modifying
    @Query("delete from #{#entityName} t where t.employeeId = ?1 and t.dutyDate=?2 and type=?3 and t.enabled=1")
     fun deleteByEmployeeAndDutyDateAndType(employeeId: String, dutyDate: LocalDate, type: String): Int
}
interface DutyWorktimeRepositoryCustom{
    fun findByAccountIdAndDutyDate(@Param("dateStart") dateStart: LocalDate, @Param("dateEnd") dateEnd: LocalDate, @Param("accountIds") accountIds: MutableList<Long>): MutableList<DutyWorktime>

    fun findPage(pageable: Pageable, dutyWorktimeQuery: DutyWorktimeQuery): Page<DutyWorktimeDto>

    fun findByDutyDate(dateStart: LocalDate,dateEnd: LocalDate,officeId:String): MutableList<DutyWorktime>
}
class DutyWorktimeRepositoryImpl  @Autowired constructor(val jdbcTemplate: JdbcTemplate, val namedParameterJdbcTemplate: NamedParameterJdbcTemplate): DutyWorktimeRepositoryCustom{
    override fun findByDutyDate(dateStart: LocalDate, dateEnd: LocalDate, officeId: String): MutableList<DutyWorktime> {
        var paramMap = HashMap<String, Any>()
        paramMap.put("dateStart", dateStart)
        paramMap.put("dateEnd", dateEnd)
        paramMap.put("officeId", officeId)
        var sb = StringBuilder()
        sb.append("""
          SELECT
            t1.*
            FROM hr_duty_worktime t1 left join hr_employee t2 on t1.employee_id=t2.id
            left join hr_account t3 on t2.account_id=t3.id
            WHERE
            t1.enabled=1
            AND t1.duty_date >= :dateStart and t1.duty_date <=:dateEnd
        """)
        if(StringUtils.isNotBlank(officeId)){
            sb.append("""
               and  t3.office_id in (
                   select office.id
                    from sys_office office
                    where office.enabled=1
                    and (office.parent_ids like concat('%,',:officeId,',%') or office.id=:officeId )
                )
            """)
        }
        return namedParameterJdbcTemplate.query(sb.toString(), paramMap, BeanPropertyRowMapper(DutyWorktime::class.java))
    }

    override fun findPage(pageable: Pageable, dutyWorktimeQuery: DutyWorktimeQuery): Page<DutyWorktimeDto> {
        var sql = StringBuilder("""
                select *
                from hr_duty_worktime t1
                where t1.enabled = 1
            """);
        if(dutyWorktimeQuery.dutyDateStart!=null){
            sql.append("""
                   AND t1.duty_date > :dutyDateStart
                """);
        }
        if(dutyWorktimeQuery.dutyDateEnd!=null){
            sql.append("""
                   AND t1.duty_date < :dutyDateEnd
                """);
        }
        if(StringUtils.isNotBlank(dutyWorktimeQuery.accountId)){
            sql.append("""
                    and t1.employee_id in (
                    select t2.id
                    from hr_employee t2
                    where t2.account_id = :accountId )
                """);
        }
        val pageableSql = MySQLDialect.getInstance().getPageableSql(sql.toString(),pageable)
        val countSql = MySQLDialect.getInstance().getCountSql(sql.toString())
        val list = namedParameterJdbcTemplate.query(pageableSql, BeanPropertySqlParameterSource(dutyWorktimeQuery), BeanPropertyRowMapper(DutyWorktimeDto::class.java))
        val count = namedParameterJdbcTemplate.queryForObject(countSql, BeanPropertySqlParameterSource(dutyWorktimeQuery),Long::class.java)
        return PageImpl(list,pageable,count)
    }

    override fun findByAccountIdAndDutyDate(dateStart: LocalDate, dateEnd: LocalDate, accountIds: MutableList<Long>): MutableList<DutyWorktime> {
        var sb = StringBuilder()
        var paramMap = HashMap<String, Any>()
        paramMap.put("dateStart", dateStart)
        paramMap.put("dateEnd", dateEnd)
        paramMap.put("accountIds", accountIds)
        sb.append("""
            SELECT
            w.employee_id,
            w.duty_date,
            if(min(w.duty_time) <= '12:00:00',min(w.duty_time),'') as dutyTimeStart,
            if(max(w.duty_time) >='12:00:00',max(w.duty_time),'') as dutyTimeEnd
            FROM
            hr_duty_worktime w
            WHERE
            w.duty_date >= :dateStart
            and  w.duty_date <= :dateEnd
        """)
        if (CollectionUtil.isNotEmpty(accountIds)) {
            sb.append(" and w.employee_id in (:accountIds)")
        }
        sb.append("""
            GROUP BY
            w.employee_id,
            w.duty_date ASC
        """)
        return namedParameterJdbcTemplate.query(sb.toString(), paramMap, BeanPropertyRowMapper(DutyWorktime::class.java))

    }

}