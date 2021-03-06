package net.myspring.report.modules.future.repository
import net.myspring.report.modules.future.dto.DepotReportDto
import net.myspring.report.modules.future.web.query.ReportQuery
import net.myspring.util.collection.CollectionUtil
import net.myspring.util.text.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class DepotShopRepository @Autowired constructor(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) {

     fun findBaokaStoreReport(reportQuery: ReportQuery): MutableList<DepotReportDto> {
        val sb = StringBuffer()
        if(reportQuery.isDetail==null||!reportQuery.isDetail){
            sb.append("""   SELECT t6.id as depotId,t6.name as 'depotName',t6.town_id as 'townId', COUNT(t1.id) AS qty,t8.name as 'chainName',t5.name as 'productTypeName',t6.office_id as 'officeId',t6.area_id as 'areaId',t6.area_type,t6.district_id as 'districtId'""")
        }else if(reportQuery.isDetail){
            sb.append("""
               SELECT t4.id as 'productId',t4.name as 'productName',t1.ime,t6.name as 'depotName',t6.town_id as 'townId',
               t8.name as 'chainName',t5.name as 'productTypeName',t6.office_id as 'officeId',t6.area_id as 'areaId',t6.area_type,t6.district_id as 'districtId',t2.employee_id, t2.created_date as 'saleDate',t1.retail_date,t3.created_date as 'uploadDate'
            """)
        }
        sb.append("""
                    FROM
                    crm_product_ime t1
                    LEFT JOIN crm_product_ime_sale t2 on t1.product_ime_sale_id=t2.id
                    LEFT JOIN crm_product_ime_upload t3 on t3.id = t1.product_ime_upload_id
                    LEFT JOIN crm_product t4 on t1.product_id=t4.id
                    LEFT JOIN crm_product_type t5 on t4.product_type_id=t5.id
                    LEFT JOIN crm_depot t6 on t1.depot_id=t6.id  and t6.depot_store_id is null
                    LEFT JOIN crm_chain t8 on t6.chain_id=t8.id,
                    crm_depot_shop t7
                    WHERE
                    t1.enabled = 1
                    and  t6.depot_shop_id=t7.id
        """)
        if(reportQuery.scoreType!=null){
            sb.append("""  and t5.score_type =:scoreType """)
        }
        if(reportQuery.date!=null){
            sb.append("""
                AND (
                    t1.retail_date IS NULL
                    OR t1.retail_date >= :date
                )
                AND t1.created_date < :date
            """)
        }
        if (StringUtils.isNotBlank(reportQuery.townType)) {
            sb.append(""" and t7.town_type=:townType """)
        }
        if (StringUtils.isNotBlank(reportQuery.areaType)) {
            sb.append(""" and t7.area_type=:areaType """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.productTypeIdList)) {
            sb.append(""" and t5.id in (:productTypeIdList) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.officeIdList)) {
            sb.append(""" and t6.office_id in (:officeIdList) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.officeIds)) {
            sb.append(""" and t6.office_id in (:officeIds) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.depotIdList)) {
            sb.append(""" and t6.id in (:depotIdList) """)
        }
        if (StringUtils.isNotBlank(reportQuery.officeId)) {
            sb.append(""" and t6.office_id =:officeId""")
        }
        if (StringUtils.isNotBlank(reportQuery.depotId)) {
            sb.append(""" and t6.id=:depotId """)
        }
        if((reportQuery.isDetail==null||!reportQuery.isDetail)&&StringUtils.isBlank(reportQuery.exportType)){
            sb.append(""" group by t1.depot_id""")
        }
        if(StringUtils.isNotBlank(reportQuery.exportType)&&reportQuery.exportType=="按数量"){
            sb.append(""" group by t1.depot_id,t5.id order by t6.area_id,t6.office_id,t6.id""")
        }
        print(sb.toString())
        return namedParameterJdbcTemplate.query(sb.toString(), BeanPropertySqlParameterSource(reportQuery), BeanPropertyRowMapper(DepotReportDto::class.java))
    }

     fun findStoreReport(reportQuery: ReportQuery): MutableList<DepotReportDto> {
        val sb = StringBuffer()
        if(reportQuery.isDetail==null||!reportQuery.isDetail){
            sb.append("""  SELECT t6.id as depotId,t6.name as depotName,t6.town_id as 'townId',COUNT(t1.id) AS qty ,t8.name as 'chainName',t5.name as 'productTypeName',t6.office_id as 'officeId',t6.area_id as 'areaId',t6.area_type,t6.district_id as 'districtId'""")
        }else if(reportQuery.isDetail){
            sb.append("""
               SELECT t4.id as 'productId',t4.name as 'productName',t1.ime,t6.name as 'depotName',t6.town_id as 'townId',t8.name as 'chainName',t5.name as 'productTypeName',t6.office_id as 'officeId',t6.area_id as 'areaId',t6.area_type,t6.district_id as 'districtId',t3.employee_id,t3.created_date as 'saleDate',t1.retail_date,t2.created_date as 'uploadDate'
            """)
        }
        sb.append("""
                    FROM
                    crm_product_ime t1
                    LEFT JOIN crm_product_ime_upload t2 on t2.id = t1.product_ime_upload_id
                    LEFT JOIN crm_product_ime_sale t3 on t1.product_ime_sale_id = t3.id
                    LEFT JOIN crm_product t4 on t1.product_id=t4.id
                    LEFT JOIN crm_product_type t5 on t4.product_type_id=t5.id
                    LEFT JOIN crm_depot t6 on t1.depot_id=t6.id and t6.depot_store_id is null
                    LEFT JOIN crm_chain t8 on t6.chain_id=t8.id,
                    crm_depot_shop t7
                    WHERE
                    t1.enabled = 1
                    and t6.depot_shop_id=t7.id
        """)
        if(reportQuery.scoreType!=null){
            sb.append("""  and t5.score_type =:scoreType """)
        }
        if(reportQuery.date!=null){
            sb.append("""
                AND (
                    t3.id IS NULL
                    OR t3.created_date > :date
                )
                AND t1.created_date < :date
            """)
        }
        if (StringUtils.isNotBlank(reportQuery.townType)) {
            sb.append(""" and t7.town_type=:townType """)
        }
        if (StringUtils.isNotBlank(reportQuery.areaType)) {
            sb.append(""" and t7.area_type=:areaType """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.productTypeIdList)) {
            sb.append(""" and t5.id in (:productTypeIdList) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.officeIdList)) {
            sb.append(""" and t6.office_id in (:officeIdList) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.officeIds)) {
            sb.append(""" and t6.office_id in (:officeIds) """)
        }
        if (StringUtils.isNotBlank(reportQuery.officeId)) {
            sb.append(""" and t6.office_id =:officeId""")
        }
        if (CollectionUtil.isNotEmpty(reportQuery.depotIdList)) {
            sb.append(""" and t6.id in (:depotIdList) """)
        }
        if (StringUtils.isNotBlank(reportQuery.depotId)) {
            sb.append(""" and t6.id=:depotId """)
        }
        if((reportQuery.isDetail==null||!reportQuery.isDetail)&&StringUtils.isBlank(reportQuery.exportType)){
            sb.append(""" group by t1.depot_id""")
        }
        if(StringUtils.isNotBlank(reportQuery.exportType)&&reportQuery.exportType=="按数量"){
            sb.append("""group by t1.depot_id,t5.id order by t5.area_id,t5.office_id,t5.id""")
        }
        print(sb.toString())
        return namedParameterJdbcTemplate.query(sb.toString(), BeanPropertySqlParameterSource(reportQuery), BeanPropertyRowMapper(DepotReportDto::class.java))
    }


     fun findBaokaSaleReport(reportQuery: ReportQuery): MutableList<DepotReportDto> {
        val sb = StringBuffer()
        if(reportQuery.isDetail==null||!reportQuery.isDetail){
            sb.append("""  SELECT t4.id as 'depotId',t4.name as 'depotName', t4.town_id as 'townId',COUNT(t1.id) AS qty,t7.name as 'chainName',t3.name as 'productTypeName',t4.office_id as 'officeId',t4.area_id as 'areaId',t4.area_type, t4.district_id as 'districtId' """)
        }else if(reportQuery.isDetail){
            sb.append("""
               SELECT t2.id as 'productId',t2.name as 'productName',t1.ime,t1.retail_date,t6.employee_id,t4.office_id as 'officeId',t4.area_id as 'areaId',t4.town_id as 'townId', t4.district_id as 'districtId',
               t6.created_date as 'saleDate',t4.id as 'depotId',t4.name as 'depotName',t3.name as 'productTypeName',t7.name as 'chainName',t5.area_type
            """)
        }
        sb.append("""
                    FROM
                    crm_product_ime t1
                    LEFT JOIN crm_product t2 ON t1.product_id = t2.id
                    LEFT JOIN crm_product_type t3 on t2.product_type_id=t3.id
                    LEFT JOIN crm_depot t4 on t1.depot_id=t4.id and t4.depot_store_id is null
                    LEFT JOIN crm_chain t7 on t4.chain_id=t7.id
        """)
        if(reportQuery.isDetail!=null&&reportQuery.isDetail){
            sb.append(""" LEFT JOIN crm_product_ime_sale t6 on t1.product_ime_sale_id=t6.id """)
        }
        sb.append(""" ,crm_depot_shop t5   WHERE t1.enabled = 1 and t4.depot_shop_id=t5.id """)
        if(reportQuery.scoreType!=null){
            sb.append("""  and t3.score_type =:scoreType """)
        }
        if(reportQuery.dateStart!=null){
            sb.append(""" and t1.retail_date>=:dateStart """)
        }
        if(reportQuery.dateEnd!=null){
            sb.append(""" and t1.retail_date<:dateEnd """)
        }
        if (StringUtils.isNotBlank(reportQuery.townType)) {
            sb.append(""" and t5.town_type=:townType """)
        }
        if (StringUtils.isNotBlank(reportQuery.areaType)) {
            sb.append(""" and t5.area_type=:areaType """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.productTypeIdList)) {
            sb.append(""" and t3.id in (:productTypeIdList) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.officeIdList)) {
            sb.append(""" and t4.office_id in (:officeIdList) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.officeIds)) {
            sb.append(""" and t4.office_id in (:officeIds) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.depotIdList)) {
            sb.append(""" and t4.id in (:depotIdList) """)
        }
        if (StringUtils.isNotBlank(reportQuery.officeId)) {
            sb.append(""" and t4.office_id =:officeId""")
        }
        if (StringUtils.isNotBlank(reportQuery.depotId)) {
            sb.append(""" and t4.id=:depotId """)
        }
        if((reportQuery.isDetail==null||!reportQuery.isDetail)&&StringUtils.isBlank(reportQuery.exportType)){
            sb.append(""" group by t1.depot_id""")
        }
        if(StringUtils.isNotBlank(reportQuery.exportType)&&(reportQuery.exportType=="按数量"||reportQuery.exportType=="按合计")){
            sb.append(""" group by t1.depot_id,t3.id order by t4.area_id,t4.office_id,t1.depot_id,t3.id""")
        }
        print(sb.toString())
        return namedParameterJdbcTemplate.query(sb.toString(), BeanPropertySqlParameterSource(reportQuery), BeanPropertyRowMapper(DepotReportDto::class.java))
    }

     fun findSaleReport(reportQuery: ReportQuery): MutableList<DepotReportDto> {
        val sb = StringBuffer()
        if(reportQuery.isDetail==null||!reportQuery.isDetail){
            sb.append("""  SELECT t5.id as 'depotId',t5.name as 'depotName', COUNT(t1.id) AS qty,t7.name as 'chainName',t4.name as 'productTypeName',t5.town_id as 'townId',t5.office_id as 'officeId',t5.area_id as 'areaId',t5.area_type,t5.district_id as 'districtId'""")
        }else if(reportQuery.isDetail){
            sb.append("""
               SELECT t3.id as 'productId',t3.name as 'productName',t2.ime,t2.retail_date,t1.employee_id,t5.office_id as 'officeId',t5.area_id as 'areaId',
               t1.created_date as 'saleDate',t5.id as 'depotId',t5.name as 'depotName',t5.town_id as 'townId',t5.district_id as 'districtId',t7.name as 'chainName',t4.name as 'productTypeName',t6.area_type
            """)
        }
        sb.append("""
                    FROM
                    crm_product_ime_sale t1
                    LEFT JOIN crm_product_ime t2 ON t2.product_ime_sale_id = t1.id
                    LEFT JOIN crm_product t3 on t2.product_id=t3.id
                    LEFT JOIN crm_product_type t4 on t3.product_type_id=t4.id
                    LEFT JOIN crm_depot t5 on t1.shop_id=t5.id   and t5.depot_store_id is null
                    LEFT JOIN crm_chain t7 on t5.chain_id=t7.id,
                    crm_depot_shop t6
                    WHERE
                    t1.enabled = 1
                    and t1.is_back=0
                    and t5.depot_shop_id=t6.id
        """)
        if(reportQuery.scoreType!=null){
            sb.append("""  and t4.score_type =:scoreType """)
        }
        if(reportQuery.dateStart!=null){
            sb.append(""" and t1.created_date>=:dateStart """)
        }
        if(reportQuery.dateEnd!=null){
            sb.append(""" and t1.created_date<:dateEnd """)
        }
        if (StringUtils.isNotBlank(reportQuery.townType)) {
            sb.append(""" and t6.town_type=:townType """)
        }
        if (StringUtils.isNotBlank(reportQuery.areaType)) {
            sb.append(""" and t6.area_type=:areaType """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.productTypeIdList)) {
            sb.append(""" and t4.id in (:productTypeIdList) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.officeIdList)) {
            sb.append(""" and t5.office_id in (:officeIdList) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.officeIds)) {
            sb.append(""" and t5.office_id in (:officeIds) """)
        }
        if (CollectionUtil.isNotEmpty(reportQuery.depotIdList)) {
            sb.append(""" and t5.id in (:depotIdList) """)
        }
        if (StringUtils.isNotBlank(reportQuery.officeId)) {
            sb.append(""" and t5.office_id =:officeId""")
        }
        if (StringUtils.isNotBlank(reportQuery.depotId)) {
            sb.append(""" and t5.id=:depotId """)
        }
        if((reportQuery.isDetail==null||!reportQuery.isDetail)&&StringUtils.isBlank(reportQuery.exportType)){
            sb.append(""" group by t1.shop_id""")
        }
        if(StringUtils.isNotBlank(reportQuery.exportType)&&reportQuery.exportType=="按数量"){
            sb.append(""" group by t1.shop_id,t4.id order by t5.area_id,t5.office_id,t5.id""")
        }
        print(sb.toString())
        return namedParameterJdbcTemplate.query(sb.toString(), BeanPropertySqlParameterSource(reportQuery), BeanPropertyRowMapper(DepotReportDto::class.java))
    }


}