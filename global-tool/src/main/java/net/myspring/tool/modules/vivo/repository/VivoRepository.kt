package net.myspring.tool.modules.vivo.repository;

import com.google.common.collect.Maps
import net.myspring.tool.modules.vivo.domain.*
import net.myspring.tool.modules.vivo.model.SCustomersM13e00
import net.myspring.tool.modules.vivo.model.SZonesM13e00
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*


/**
 * Created by admin on 2016/10/17.
 */
@Component
class VivoRepository @Autowired constructor(val jdbcTemplate: JdbcTemplate,val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) {

   fun products(): MutableList<VivoProducts>{
      return namedParameterJdbcTemplate.query("""
       select *  from VR_Products
        """,  BeanPropertyRowMapper(VivoProducts::class.java));
   }

   fun plantProducts(): MutableList<VivoPlantProducts>{
      return namedParameterJdbcTemplate.query("""
      select *  from vr_products_m13e00
      """,BeanPropertyRowMapper(VivoPlantProducts::class.java));
   }

   fun plantSendimei(dateStart: LocalDate, dateEnd: LocalDate, agentCodes: MutableList<String>): MutableList<VivoPlantSendimei>{
      var paramMap=Maps.newHashMap<String,Any>();
      paramMap.put("dateStart",dateStart);
      paramMap.put("dateEnd",dateEnd);
      paramMap.put("agentCodes",agentCodes);
      return namedParameterJdbcTemplate.query("""
      select *  from vr_plant_sendimei_m13e00 t1
        where t1.createdTime >= :dateStart
        and t1.createdTime <= :dateEnd
        and t1.companyId in :agentCodes
      """,paramMap,BeanPropertyRowMapper(VivoPlantSendimei::class.java));
   }

   fun plantElectronicsn(dateStart: LocalDate, dateEnd: LocalDate): MutableList<VivoPlantElectronicsn>{
      var paramMap=Maps.newHashMap<String,Any>();
      paramMap.put("dateStart",dateStart);
      paramMap.put("dateEnd",dateEnd);
      return namedParameterJdbcTemplate.query("""
      select snImei,companyId,productId,retailDate,createTime
        from vr_plant_electronicsn_m13e00  t1
        where t1.retailDate >= :dateStart
        and t1.retailDate <= :dateEnd
      """,paramMap,BeanPropertyRowMapper(VivoPlantElectronicsn::class.java));
   }

   fun deleteZones(mainCode: String): Int{
      return namedParameterJdbcTemplate.update("""
      delete from S_ZONES_M13E00 where ZoneId = :mainCode
      """,Collections.singletonMap("mainCode",mainCode));
   }

   fun findCustomerIDs(mainCode: String): MutableList<String>{
      return namedParameterJdbcTemplate.query("""
      select distinct CustomerID from S_Customers_M13e00 where CompanyID=  :mainCode
     """,Collections.singletonMap("mainCode",mainCode),BeanPropertyRowMapper(String::class.java));
   }

   fun findIdvivoCustomerIDs(): MutableList<String>{
      return namedParameterJdbcTemplate.query("""
         SELECT
        DISTINCT CustomerID
        FROM
        S_Customers_R250082
        UNION
        SELECT
        DISTINCT CustomerID
        FROM
        S_Customers_R2500821
        union
        SELECT
        DISTINCT CustomerID
        FROM
        S_Customers_R2500822
        union
        SELECT
        DISTINCT CustomerID
        FROM
        S_Customers_R2500823
      """,BeanPropertyRowMapper(String::class.java));
   }

   fun getSPlantStockSupply(date: String, mainCode: String): MutableList<SPlantStockSupplyM13e00>{
      var paramMap=Maps.newHashMap<String,Any>();
      paramMap.put("date",date);
      paramMap.put("mainCode",mainCode);
      return namedParameterJdbcTemplate.query("""
      select *  from S_PlantStockSupply_M13e00
        where CompanyID= :mainCode
        and AccountDate= :date
      """,paramMap,BeanPropertyRowMapper(SPlantStockSupplyM13e00::class.java));
   }

   fun getIdvivoSPlantStockSupply(): MutableList<SPlantStockSupplyM13e00>{
      return namedParameterJdbcTemplate.query("""
      select * from S_PlantStockSupply_R2500821
        union
        select * from S_PlantStockSupply_R2500822
        union
        select * from S_PlantStockSupply_R2500823
      """,BeanPropertyRowMapper(SPlantStockSupplyM13e00::class.java));
   }

   fun getSPlantStockDealer( date: String,mainCode: String): MutableList<SPlantStockDealerM13e00>{
      var paramMap=Maps.newHashMap<String,Any>();
      paramMap.put("date",date);
      paramMap.put("mainCode",mainCode);
      return namedParameterJdbcTemplate.query("""
      select *  from S_PlantStockDealer_M13e00
        where CompanyID= :mainCode
        and AccountDate= :date
      """,paramMap,BeanPropertyRowMapper(SPlantStockDealerM13e00::class.java));
   }

   fun getIdvivoSPlantStockDealer(): MutableList<SPlantStockDealerM13e00>{
      return namedParameterJdbcTemplate.query("""
      select * from S_PlantStockDealer_R2500821
        union
        select * from S_PlantStockDealer_R2500822
        union
        select * from S_PlantStockDealer_R2500823
      """,BeanPropertyRowMapper(SPlantStockDealerM13e00::class.java));
   }

   fun getSPlantStockStores(date: String,  mainCode: String): MutableList<SPlantStockStoresM13e00>{
      var paramMap=Maps.newHashMap<String,Any>();
      paramMap.put("date",date);
      paramMap.put("mainCode",mainCode);
      return namedParameterJdbcTemplate.query("""
      select *  from s_PlantStockStores_m13e00
        where CompanyID= :mainCode
        and AccountDate= :date
      """,paramMap,BeanPropertyRowMapper(SPlantStockStoresM13e00::class.java));
   }

   fun getIdvivoSPlantStockStores(date: String): MutableList<SPlantStockStoresM13e00>{
      return namedParameterJdbcTemplate.query("""
      select *  from S_PlantStockStores_R250082
        where AccountDate=:date
      """,Collections.singletonMap("date",date),BeanPropertyRowMapper(SPlantStockStoresM13e00::class.java));
   }

  fun sProductItem(): MutableList<String>{
      return namedParameterJdbcTemplate.query("""
      select ProductNo  from S_ProductItem000_M13e00
      """,BeanPropertyRowMapper(String::class.java));
   }

   fun idvivoSProductItem(): MutableList<String>{
      return namedParameterJdbcTemplate.query("""
         select ProductNo  from S_ProductItem000_R2500821
        union
        select ProductNo  from S_ProductItem000_R2500822
        union
        select ProductNo  from S_ProductItem000_R2500823
      """,BeanPropertyRowMapper(String::class.java));
   }

   fun deleteProductItemLend(mainCode: String){
      namedParameterJdbcTemplate.update("""
      delete from S_ProductItemLend_M13e00 where CompanyID =:mainCode
      """,Collections.singletonMap("mainCode",mainCode));
   }

    fun idvivoDeleteZones() {
      jdbcTemplate.update("delete from S_ZONES_R250082");
       jdbcTemplate.update("delete from S_ZONES_R2500821");
       jdbcTemplate.update("delete from S_ZONES_R2500822");
       jdbcTemplate.update("delete from S_ZONES_R2500823");
   }

    fun insertZones(sZonesM13e00s: MutableList<SZonesM13e00>) {
      for(sZonesM13e00 in sZonesM13e00s) {
         namedParameterJdbcTemplate.update("""
           insert into S_ZONES_M13E00
           (zoneID,zoneName,shortCut,zoneDepth,zonePath,fatherID,subCount,zoneTypes)
           values (
            :zoneID,
            :zoneName,
            :shortCut,
            :zoneDepth,
            :zonePath,
            :fatherID,
            :subCount,
            :zoneTypes
        """,Collections.singletonMap("sZonesM13e00s",sZonesM13e00s))
      }
   }

    fun insertZonesR250082(sZonesM13e00s: MutableList<SZonesM13e00>, name: String) {
        var paramMap=Maps.newHashMap<String,Any>();
        paramMap.put("sZonesM13e00s",sZonesM13e00s);
        paramMap.put("name",name);
      for (sZonesM13e00 in sZonesM13e00s){
        namedParameterJdbcTemplate.update("""
        insert into S_ZONES_?2
        (zoneID,zoneName,shortCut,zoneDepth,zonePath,fatherID,subCount,zoneTypes)
        values
            (
            :zoneID,
            :zoneName,
            :shortCut,
            :zoneDepth,
            :zonePath,
            :fatherID,
            :subCount,
            :zoneTypes
            )
         """,paramMap)
      }
   }

    fun insertCustomers(sCustomersM13e00s: MutableList<SCustomersM13e00>) {
      for (sCustomersM13e00 in sCustomersM13e00s){
         namedParameterJdbcTemplate.update("""
        insert into S_Customers_M13e00
        (customerID,
        customerName,
        Shortcut,
        customerType,
        customerSort,
        customerKASort,
        address,
        postcode,
        telephone,
        fax,
        linkMan,
        linkTel,
        remark,
        inUse,
        zoneID,
        companyID,
        recordDate,
        customerLevel,
        manager,
        customerstr1,
        customerstr2,
        customerstr3,
        customerstr4,
        customerstr5,
        customerstr6,
        customerstr7,
        customerstr8,
        customerstr9,
        customerstr10,
        latentCustomers
        )VALUES
            (
            :customerID,
            :customerName,
            :Shortcut,
            :customerType,
            :customerSort,
            :customerKASort,
            :address,
            :postcode,
            :telephone,
            :fax,
            :linkMan,
            :linkTel,
            :remark,
            :inUse,
            :zoneID,
            :companyID,
            :recordDate,
            :customerLevel,
            :manager,
            :customerstr1,
            :customerstr2,
            :customerstr3,
            :customerstr4,
            :customerstr5,
            :customerstr6,
            :customerstr7,
            :customerstr8,
            :customerstr9,
            :customerstr10,
            :latentCustomers
            )
         """,Collections.singletonMap("sCustomersM13e00s",sCustomersM13e00s))
      }
   }

    fun insertCustomersR250082(sCustomersM13e00s: MutableList<SCustomersM13e00>, name: String) {
        var paramMap=Maps.newHashMap<String,Any>();
        paramMap.put("sCustomersM13e00s",sCustomersM13e00s);
        paramMap.put("name",name);
      for (sCustomersM13e00 in sCustomersM13e00s){
         namedParameterJdbcTemplate.update("""
        insert into  S_Customers_?2
        (customerID,
        customerName,
        zoneID,
        companyID,
        recordDate,
        customerLevel,
        manager,
        customerstr1,
        customerstr2,
        customerstr3,
        customerstr4,
        customerstr5,
        customerstr6,
        customerstr7,
        customerstr8,
        customerstr9,
        customerstr10,
        latentCustomers
        )VALUES
            (
            :customerID,
            :customerName,
            :zoneID,
            :companyID,
            :recordDate,
            :customerLevel,
            :manager,
            :customerstr1,
            :customerstr2,
            :customerstr3,
            :customerstr4,
            :customerstr5,
            :customerstr6,
            :customerstr7,
            :customerstr8,
            :customerstr9,
            :customerstr10,
            :latentCustomers
            )
         """,paramMap)
      }
   }

    fun insertPlantStockSupply(sPlantStockSupplyM13e00s: MutableList<SPlantStockSupplyM13e00>) {
      for (sPlantStockSupplyM13e00 in sPlantStockSupplyM13e00s){
         namedParameterJdbcTemplate.update("""
         insert into S_PlantStockSupply_M13e00
        (companyID,supplyID,productID,createdTime,sumstock,useablestock,bad,accountDate)
        values
            :companyID,
            :supplyID,
            :productID,
            :createdTime,
            :sumstock,
            :useablestock,
            :bad,
            :accountDate
         """,Collections.singletonMap("sPlantStockSupplyM13e00s",sPlantStockSupplyM13e00s))
      }
   }

    fun insertPlantStockSupplyR250082(sPlantStockSupplyM13e00s: MutableList<SPlantStockSupplyM13e00>, name: String) {
        var paramMap=Maps.newHashMap<String,Any>();
        paramMap.put("sPlantStockSupplyM13e00s",sPlantStockSupplyM13e00s);
        paramMap.put("name",name);
        for (sPlantStockSupplyM13e00 in sPlantStockSupplyM13e00s){
         namedParameterJdbcTemplate.update("""
         insert into S_PlantStockSupply_?2
        (companyID,supplyID,productID,createdTime,sumstock,useablestock,bad,accountDate)
        values
            (
            :companyID,
            :supplyID,
            :productID,
            :createdTime,
            :sumstock,
            :useablestock,
            :bad,
            :accountDate
            )
         """,paramMap)
      }
   }

    fun insertPlantStockDealer(sPlantStockDealerM13e00s: MutableList<SPlantStockDealerM13e00>) {
      for (sPlantStockDealerM13e00 in sPlantStockDealerM13e00s){
         namedParameterJdbcTemplate.update("""
       insert into S_PlantStockDealer_M13e00 (companyID,dealerID,productID,createdTime,sumstock,useablestock,bad,accountDate) values
            (
            :companyID,
            :dealerID,
            :productID,
            :createdTime,
            :sumstock,
            :useablestock,
            :bad,
            :accountDate
            )
         """,Collections.singletonMap("sPlantStockDealerM13e00s",sPlantStockDealerM13e00s))
      }
   }

    fun insertPlantStockDealerR250082(sPlantStockDealerM13e00s: MutableList<SPlantStockDealerM13e00>, name: String) {
        var paramMap=Maps.newHashMap<String,Any>();
        paramMap.put("sPlantStockDealerM13e00s",sPlantStockDealerM13e00s);
        paramMap.put("name",name);
      for (sPlantStockDealerM13e00 in sPlantStockDealerM13e00s){
         namedParameterJdbcTemplate.update("""
         insert into S_PlantStockDealer_?2
        (companyID,dealerID,productID,createdTime,sumstock,useablestock,bad,accountDate) values
            (
            :companyID,
            :dealerID,
            :productID,
            :createdTime,
            :sumstock,
            :useablestock,
            :bad,
            :accountDate
            )
         """,paramMap)
      }
   }

    fun insertPlantStockStores(sPlantStockStoresM13e00s: MutableList<SPlantStockStoresM13e00>) {
      for (sPlantStockStoresM13e00 in sPlantStockStoresM13e00s){
         namedParameterJdbcTemplate.update("""
        insert into S_PlantStockStores_M13e00(companyID,storeID,productID,createdTime,sumstock,useablestock,bad,accountDate) values
            (
            :companyID,
            :storeID,
            :productID,
            :createdTime,
            :sumstock,
            :useablestock,
            :bad,
            :accountDate
            )
         """,Collections.singletonMap("sPlantStockStoresM13e00s",sPlantStockStoresM13e00s))
      }
   }

    fun insertPlantStockStoresR250082(sPlantStockStoresM13e00s: MutableList<SPlantStockStoresM13e00>, name: String) {
        var paramMap=Maps.newHashMap<String,Any>();
        paramMap.put("sPlantStockStoresM13e00s",sPlantStockStoresM13e00s);
        paramMap.put("name",name);
        for (sPlantStockStoresM13e00 in sPlantStockStoresM13e00s){
         namedParameterJdbcTemplate.update("""
        insert into S_PlantStockStores_?2 (companyID,storeID,productID,createdTime,sumstock,useablestock,bad,accountDate)
        values
            (
            :companyID,
            :storeID,
            :productID,
            :createdTime,
            :sumstock,
            :useablestock,
            :bad,
            :accountDate
            )
         """,paramMap)
      }
   }

    fun insertProductItemLend(sProductItemLendM13e00s: MutableList<SProductItemLendM13e00>) {
      for (sProductItemLendM13e00 in sProductItemLendM13e00s){
         namedParameterJdbcTemplate.update("""
          insert into S_ProductItemLend_M13e00
        (
        companyID,
        productID,
        productNo,
        storeID,
        customerID,
        subCustomerID,
        status,
        statusInfo,
        isReturnProfit,
        isLock,
        remark
        ) values
            (
            :companyID,
            :productID,
            :productNo,
            :storeID,
            :customerID,
            :subCustomerID,
            :status,
            :statusInfo,
            :isReturnProfit,
            :isLock,
            :remark
            )
         """,Collections.singletonMap("sProductItemLendM13e00s",sProductItemLendM13e00s))
      }
   }

    fun insertProductItem000(sProductItem000M13e00s: MutableList<SProductItem000M13e00>) {
      for (sProductItem000M13e00 in sProductItem000M13e00s){
         namedParameterJdbcTemplate.update("""
          insert into S_ProductItem000_M13e00 (companyID,productID,productNo,storeID,customerID,subCustomerID,status,statusInfo,isReturnProfit,isLock,remark) values
            (
            :companyID,
            :productID,
            :productNo,
            :storeID,
            :customerID,
            :subCustomerID,
            :status,
            :statusInfo,
            :isReturnProfit,
            :isLock,
            :remark
            )
         """,Collections.singletonMap("sProductItem000M13e00s",sProductItem000M13e00s))
      }
   }

    fun deleteProductItem000() {
      jdbcTemplate.execute(" delete from  S_ProductItem000_M13E00")
   }

    fun deleteProductItem000R250082() {
        jdbcTemplate.execute(" delete from  S_ProductItem000_R250082")
        jdbcTemplate.execute(" delete from  S_ProductItem000_R2500821")
        jdbcTemplate.execute(" delete from  S_ProductItem000_R2500822")
        jdbcTemplate.execute(" delete from  S_ProductItem000_R2500823")
   }

    fun deleteSStores() {
        jdbcTemplate.execute(" delete from  S_Stores_M13E00")
   }

    fun deleteSStoresR250082() {
        jdbcTemplate.execute(" delete from  S_Stores_R250082")
        jdbcTemplate.execute(" delete from  S_Stores_R2500821")
        jdbcTemplate.execute(" delete from  S_Stores_R2500822")
        jdbcTemplate.execute(" delete from  S_Stores_R2500823")
   }

    fun insertSStores(sStoresM13e00s: MutableList<SStoresM13e00>) {
      for ( sStoresM13e00 in sStoresM13e00s){
         namedParameterJdbcTemplate.update("""
          insert into S_Stores_M13E00
        (storeID,storeName,remark,shortCut) values
            (
            :storeID,
            :storeName,
            :remark,
            :shortCut
            )
         """,Collections.singletonMap("sStoresM13e00s",sStoresM13e00s))
      }
   }

    fun insertSStoresR250082(sStoresM13e00s: MutableList<SStoresM13e00>, name: String) {
        var paramMap=Maps.newHashMap<String,Any>();
        paramMap.put("sStoresM13e00s",sStoresM13e00s);
        paramMap.put("name",name);
      for (sStoresM13e00 in sStoresM13e00s ){
         namedParameterJdbcTemplate.update("""
          insert into S_Stores_?2
        (storeID,storeName,remark,shortCut) values
            (
            :storeID,
            :storeName,
            :remark,
            :shortCut
            )
         """,paramMap)

      }
   }

    fun insertProductItem000R250082(sProductItem000M13e00s: MutableList<SProductItem000M13e00>, name: String) {
        var paramMap=Maps.newHashMap<String,Any>();
        paramMap.put("sProductItem000M13e00s",sProductItem000M13e00s);
        paramMap.put("name",name);
      for (sProductItem000M13e00 in sProductItem000M13e00s){
         namedParameterJdbcTemplate.update("""
         insert into S_ProductItem000_?2
        (companyID,productID,productNo,storeID,customerID,subCustomerID,status,statusInfo,isReturnProfit,isLock,remark) values
            (
            :companyID,
            :productID,
            :productNo,
            :storeID,
            :customerID,
            :subCustomerID,
            :status,
            :statusInfo,
            :isReturnProfit,
            :isLock,
            :remark
            )
         """,paramMap)
      }
   }

    fun deleteProductItemStocks() {
      jdbcTemplate.execute(" delete from  S_ProductItemStocks_M13E00")
   }

    fun deleteProductItemStocksR250082() {
        jdbcTemplate.execute(" delete from  S_ProductItemStocks_R250082")
        jdbcTemplate.execute(" delete from  S_ProductItemStocks_R2500821")
        jdbcTemplate.execute(" delete from  S_ProductItemStocks_R2500822")
        jdbcTemplate.execute(" delete from  S_ProductItemStocks_R2500823")
   }

    fun insertProductItemStocks(sProductItemStocksM13e00s: MutableList<SProductItemStocksM13e00>) {
      for (sProductItemStocksM13e00 in sProductItemStocksM13e00s){
         namedParameterJdbcTemplate.update("""
           insert into S_ProductItemStocks_M13E00 (companyID,productID,productNo,storeID,customerID,subCustomerID,status,statusInfo,isReturnProfit,isLock,remark) 
           values
            (
            :companyID,
            :productID,
            :productNo,
            :storeID,
            :customerID,
            :subCustomerID,
            :status,
            :statusInfo,
            :isReturnProfit,
            :isLock,
            :remark
            )
         """,Collections.singletonMap("sProductItemStocksM13e00s",sProductItemStocksM13e00s))

      }
   }

    fun insertProductItemStocksR250082(sProductItemStocksM13e00s: MutableList<SProductItemStocksM13e00>, name: String) {
        var paramMap=Maps.newHashMap<String,Any>();
        paramMap.put("sProductItemStocksM13e00s",sProductItemStocksM13e00s);
        paramMap.put("name",name);
      for (sProductItemStocksM13e00 in sProductItemStocksM13e00s){
         namedParameterJdbcTemplate.update("""
          insert into S_ProductItemStocks_?2 (companyID,productID,productNo,storeID,customerID,subCustomerID,status,statusInfo,isReturnProfit,isLock,remark)
          values
            (
            :companyID,
            :productID,
            :productNo,
            :storeID,
            :customerID,
            :subCustomerID,
            :status,
            :statusInfo,
            :isReturnProfit,
            :isLock,
            :remark
            )
         """,paramMap)

      }
   }

    fun insertSPlantendproductsaleM13e00(sPlantEndProductSaleM13e00s: MutableList<SPlantEndProductSaleM13e00>) {
      for (sPlantEndProductSaleM13e00 in sPlantEndProductSaleM13e00s){
         namedParameterJdbcTemplate.update("""
          insert into S_PlantEndProductSale_M13e00(companyID,endBillID,productID,saleCount,imei,billDate,dealerID,salePrice,createdTime) 
          values
            (
            :companyID,
            :endBillID,
            :productID,
            :saleCount,
            :imei,
            :billDate,
            :dealerID,
            :salePrice,
            :createdTime
            )
         """,Collections.singletonMap("sPlantEndProductSaleM13e00s",sPlantEndProductSaleM13e00s))

      }
   }

    fun insertSPlantendproductsaleR250082(sPlantEndProductSaleM13e00s: MutableList<SPlantEndProductSaleM13e00>, name: String) {
        var paramMap=Maps.newHashMap<String,Any>();
        paramMap.put("sPlantEndProductSaleM13e00s",sPlantEndProductSaleM13e00s);
        paramMap.put("name",name);
      for (sPlantEndProductSaleM13e00 in sPlantEndProductSaleM13e00s){
         namedParameterJdbcTemplate.update("""
         insert into S_PlantEndProductSale_?2 (companyID,endBillID,productID,saleCount,imei,billDate,dealerID,salePrice,createdTime)
         values
            (
            :companyID,
            :endBillID,
            :productID,
            :saleCount,
            :imei,
            :billDate,
            :dealerID,
            :salePrice,
            :createdTime
            )
         """,paramMap)
      }
   }
}

