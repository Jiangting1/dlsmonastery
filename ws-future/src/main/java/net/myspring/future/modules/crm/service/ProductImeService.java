package net.myspring.future.modules.crm.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.gridfs.GridFSFile;
import net.myspring.common.constant.CharConstant;
import net.myspring.future.common.enums.InputTypeEnum;
import net.myspring.future.common.enums.OutTypeEnum;
import net.myspring.future.common.enums.SumTypeEnum;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.client.OfficeClient;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.domain.Product;
import net.myspring.future.modules.basic.domain.ProductType;
import net.myspring.future.modules.basic.dto.DepotReportDto;
import net.myspring.future.modules.basic.manager.DepotManager;
import net.myspring.future.modules.basic.repository.DepotRepository;
import net.myspring.future.modules.basic.repository.DepotShopRepository;
import net.myspring.future.modules.basic.repository.ProductRepository;
import net.myspring.future.modules.basic.repository.ProductTypeRepository;
import net.myspring.future.modules.crm.domain.ProductIme;
import net.myspring.future.modules.crm.dto.ProductImeDto;
import net.myspring.future.modules.crm.dto.ProductImeHistoryDto;
import net.myspring.future.modules.crm.dto.ProductImeReportDto;
import net.myspring.future.modules.crm.dto.ProductImeReportExportDto;
import net.myspring.future.modules.crm.repository.ProductImeRepository;
import net.myspring.future.modules.crm.web.form.ProductImeBatchChangeForm;
import net.myspring.future.modules.crm.web.form.ProductImeBatchCreateForm;
import net.myspring.future.modules.crm.web.form.ProductImeChangeForm;
import net.myspring.future.modules.crm.web.form.ProductImeCreateForm;
import net.myspring.future.modules.crm.web.query.ProductImeQuery;
import net.myspring.future.modules.crm.web.query.ReportQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.ExcelUtils;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ProductImeService {

    @Autowired
    private ProductImeRepository productImeRepository;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private GridFsTemplate tempGridFsTemplate;
    @Autowired
    private OfficeClient officeClient;
    @Autowired
    private DepotManager depotManager;
    @Autowired
    private DepotShopRepository depotShopRepository;
    @Autowired
    private ProductTypeRepository productTypeRepository;

    //分页，但不查询总数
    public Page<ProductImeDto> findPage(Pageable pageable,ProductImeQuery productImeQuery) {
        Page<ProductImeDto> page = productImeRepository.findPage(pageable, productImeQuery);

        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public List<ProductImeDto> findByImeList(List<String> imeList){
        List<ProductIme> productImeList=productImeRepository.findByImeList(imeList);
        List<ProductImeDto> productImeDtoList= BeanUtil.map(productImeList,ProductImeDto.class);
        cacheUtils.initCacheInput(productImeDtoList);
        return productImeDtoList;
    }

    public Map<String,Integer> findQtyMap(List<String> imeList){
        Map<String,Integer> map= Maps.newHashMap();
        List<ProductIme> productImeList=productImeRepository.findByImeList(imeList);
        if(CollectionUtil.isNotEmpty(productImeList)){
            List<Product> productList=productRepository.findAll(CollectionUtil.extractToList(productImeList,"productId"));
            Map<String,Product> productMap=CollectionUtil.extractToMap(productList,"id");
            Map<String,List<ProductIme>> productImeMap=CollectionUtil.extractToMapList(productImeList,"productId");
            for(Map.Entry<String,List<ProductIme>> entry:productImeMap.entrySet()){
                map.put(productMap.get(entry.getKey()).getName(),entry.getValue().size());
            }
        }
        return map;
    }

    public ProductImeDto getProductImeDetail(String id) {
        ProductImeDto result = productImeRepository.findProductImeDto(id);
        cacheUtils.initCacheInput(result);
        return result;
    }

    public List<ProductImeHistoryDto> getProductImeHistoryList(String productImeId) {

        List<ProductImeHistoryDto> list = productImeRepository.findProductImeHistoryList(productImeId);
        cacheUtils.initCacheInput(list);

        return list;
    }

    public List<ProductImeDto> findDtoListByImes(String imeStr) {
        if(StringUtils.isBlank(imeStr)){
            return new ArrayList<>();
        }
        List<String> imeList = StringUtils.getSplitList(imeStr, CharConstant.ENTER);
        List<ProductImeDto> productImeDtoList  = productImeRepository.findDtoListByImeList(imeList, RequestUtils.getCompanyId());
        cacheUtils.initCacheInput(productImeDtoList);
        return productImeDtoList;
    }

    public String export( List<ProductImeDto> productImeDtoList) {

        Workbook workbook = new SXSSFWorkbook(10000);
        List<SimpleExcelColumn> simpleExcelColumnList = Lists.newArrayList();

        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "boxIme", "箱号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "ime", "串码"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "ime2", "串码2"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "meid", "MEID"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "productName", "货品型号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "productTypeName", "统计型号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "inputType", "入库类型"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "billId", "工厂订单编号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "createdTime", "工厂发货时间"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "createdDate", "创建时间"));

//            TODO 在companyConfig上增加配置
//        if(Const.HAS_PROVINCE){
//            simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "depotRegionName", "大区"));
//            simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "depotProvinceName", "省份"));
//        }
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "depotAreaName", "办事处"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "depotOfficeName", "考核区域"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "depotAreaType", "区域属性"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "depotName", "所在仓库"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "retailDate", "保卡注册日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "productImeSaleCreatedDate", "串码核销日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "productImeSaleEmployeeName", "核销人"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "productImeUploadMonth", "保卡上报月份"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "productImeUploadCreatedDate", "保卡上报日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "productImeUploadEmployeeName", "保卡上报人"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "lastModifiedBy", "更新人"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "lastModifiedDate", "更新时间"));

        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("串码列表", productImeDtoList, simpleExcelColumnList);
        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook,"串码列表"+ LocalDateUtils.format(LocalDate.now())+".xlsx",simpleExcelSheet);
        ByteArrayInputStream byteArrayInputStream= ExcelUtils.doWrite(simpleExcelBook.getWorkbook(),simpleExcelBook.getSimpleExcelSheets());
        GridFSFile gridFSFile = tempGridFsTemplate.store(byteArrayInputStream,simpleExcelBook.getName(),"application/octet-stream; charset=utf-8", RequestUtils.getDbObject());
        return StringUtils.toString(gridFSFile.getId());
    }

    public String export(ProductImeQuery productImeQuery) {
        List<ProductImeDto> productImeDtoList = productImeRepository.findPage(new PageRequest(0, 10000) , productImeQuery).getContent();
        cacheUtils.initCacheInput(productImeDtoList);
        return export(productImeDtoList);
    }


    public List<ProductIme> findByImeLike(String imeReverse,String shopId){
        List<ProductIme> productImeList = productImeRepository.findTop20ByDepotIdAndImeReverseStartingWithAndEnabledIsTrue(shopId,imeReverse);
        return productImeList;
    }

    public List<ProductImeReportDto> productImeReport(ReportQuery reportQuery) {
        reportQuery.setOfficeIdList(officeClient.getOfficeFilterIds(RequestUtils.getRequestEntity().getOfficeId()));
        reportQuery.setDepotIdList(depotManager.filterDepotIds());
        Map<String,List<String>>  lastRuleMap=Maps.newHashMap();
        if(StringUtils.isNotBlank(reportQuery.getOfficeId())){
            reportQuery.getOfficeIdList().addAll(officeClient.getChildOfficeIds(reportQuery.getOfficeId()));
            lastRuleMap=officeClient.getLastRuleMapByOfficeId(reportQuery.getOfficeId());
        }
        List<ProductImeReportDto> productImeSaleReportList=getProductImeReportList(reportQuery);
        if(StringUtils.isNotBlank(reportQuery.getOfficeId())&&SumTypeEnum.区域.name().equals(reportQuery.getSumType())){
            Map<String,ProductImeReportDto> map=Maps.newHashMap();
            for(ProductImeReportDto productImeSaleReportDto:productImeSaleReportList){
                String key=getOfficeKey(lastRuleMap,productImeSaleReportDto.getOfficeId());
                if(StringUtils.isNotBlank(key)){
                    if(map.containsKey(key)){
                        map.get(key).addQty(1);
                    }else {
                        ProductImeReportDto productImeSaleReport=new ProductImeReportDto(key,1);
                        map.put(key,productImeSaleReport);
                    }
                }
            }
            for(String officeId:lastRuleMap.keySet()){
                if(!map.containsKey(officeId)){
                    map.put(officeId,new ProductImeReportDto(officeId,0));
                }
            }
            List<ProductImeReportDto> list=Lists.newArrayList(map.values());
            setPercentage(list);
            cacheUtils.initCacheInput(list);
            return list;
        }else {
            setPercentage(productImeSaleReportList);
            cacheUtils.initCacheInput(productImeSaleReportList);
            return productImeSaleReportList;
        }
    }

    private String getOfficeKey(Map<String,List<String>> officeMap,String officeId){
        for(Map.Entry<String,List<String>> entry:officeMap.entrySet()){
            for (String childId:entry.getValue()){
                if(childId.equals(officeId)){
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private List<ProductImeReportDto> getProductImeReportList(ReportQuery reportQuery){
        List<ProductImeReportDto> productImeReportList=Lists.newArrayList();
        if(OutTypeEnum.电子保卡.name().equals(reportQuery.getOutType())){
            if("销售报表".equals(reportQuery.getType())){
                productImeReportList=productImeRepository.findBaokaSaleReport(reportQuery);
            }else if("库存报表".equals(reportQuery.getType())){
                productImeReportList=productImeRepository.findBaokaStoreReport(reportQuery);
            }
        }else {
            if("销售报表".equals(reportQuery.getType())){
                productImeReportList=productImeRepository.findSaleReport(reportQuery);
            }else if("库存报表".equals(reportQuery.getType())){
                productImeReportList=productImeRepository.findStoreReport(reportQuery);
            }
        }
        return productImeReportList;
    }

    private void setPercentage(List<ProductImeReportDto> productImeReportList) {
        Integer sum = 0;
        for (ProductImeReportDto productImeReportDto : productImeReportList) {
            sum= sum + productImeReportDto.getQty();
        }
        for (ProductImeReportDto productImeReportDto : productImeReportList) {
            productImeReportDto.setPercent(StringUtils.division(sum,productImeReportDto.getQty()));
        }
    }

    public String getMongoDbId(Workbook workbook, List<DepotReportDto> depotReportList,ReportQuery reportQuery){
        List<SimpleExcelColumn> simpleExcelColumnList=Lists.newArrayList();
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"areaName","办事处"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"officeName","考核区域"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"depotName","门店名称"));
        if("按数量".equals(reportQuery.getExportType())){
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"chainName","连锁体系"));
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"productTypeName","产品型号"));
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"qty","数量"));
        }else if("按串码".equals(reportQuery.getExportType())){
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"ime","串码"));
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"productTypeName","产品型号"));
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"chainName","连锁体系"));
        }else if("按合计".equals(reportQuery.getExportType())){
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"chainName","连锁体系"));
            List<ProductType> productTypeList=productTypeRepository.findByScoreType(reportQuery.getScoreType());
            for(ProductType productType:productTypeList){
                simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"extra",productType.getName(),productType.getName()));
            }
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"extra","合计","sum"));
            Map<String,DepotReportDto> map=Maps.newHashMap();
            for(DepotReportDto depotReport:depotReportList){
                if(!map.containsKey(depotReport.getDepotId())){
                    map.put(depotReport.getDepotId(),depotReport);
                }
                map.get(depotReport.getDepotId()).getExtra().put(depotReport.getProductTypeName(),depotReport.getQty());
            }
            depotReportList=Lists.newArrayList(map.values());
            Map<String,Object> sumMap=Maps.newHashMap();
            Integer totalSum=0;
            for(DepotReportDto depotReport:depotReportList){
                Integer sum=0;
                for(ProductType productType:productTypeList){
                    String key=productType.getName();
                    if(!depotReport.getExtra().containsKey(key)){
                        depotReport.getExtra().put(key,0);
                    }
                    if(!sumMap.containsKey(key)){
                        sumMap.put(key,0);
                    }
                    sumMap.put(key,(Integer)sumMap.get(key)+(Integer)depotReport.getExtra().get(key));
                    sum+=(Integer)depotReport.getExtra().get(key);
                    totalSum+=(Integer)depotReport.getExtra().get(key);
                }
                depotReport.getExtra().put("sum",sum);
            }
            DepotReportDto depotReportDto=new DepotReportDto();
            depotReportDto.setChainName("合计");
            depotReportDto.setExtra(sumMap);
            depotReportDto.getExtra().put("sum",totalSum);
            depotReportList.add(depotReportDto);
        }
        cacheUtils.initCacheInput(depotReportList);
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("销售报表"+reportQuery.getExportType(),depotReportList,simpleExcelColumnList);
        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook,"销售报表"+ LocalDateUtils.format(LocalDate.now())+".xlsx",simpleExcelSheet);
        ByteArrayInputStream byteArrayInputStream=ExcelUtils.doWrite(simpleExcelBook.getWorkbook(),simpleExcelBook.getSimpleExcelSheets());
        GridFSFile gridFSFile = tempGridFsTemplate.store(byteArrayInputStream,simpleExcelBook.getName(),"application/octet-stream; charset=utf-8", RequestUtils.getDbObject());
        return StringUtils.toString(gridFSFile.getId());
    }

    public void batchCreate(ProductImeBatchCreateForm productImeBatchCreateForm) {
        if(CollectionUtil.isEmpty(productImeBatchCreateForm.getProductImeCreateFormList())){
            return;
        }
        List<ProductIme> productImes = Lists.newArrayList();
        for (ProductImeCreateForm productImeCreateForm : productImeBatchCreateForm.getProductImeCreateFormList()) {
            ProductIme productIme = new ProductIme();

            productIme.setProductId(productRepository.findByEnabledIsTrueAndCompanyIdAndName(RequestUtils.getCompanyId(), productImeCreateForm.getProductName()).getId());
            Depot depot = depotRepository.findByCompanyIdAndName(RequestUtils.getCompanyId(), productImeCreateForm.getStoreName());
            productIme.setDepotId(depot.getId());
            productIme.setRetailShopId(depot.getId());
            productIme.setIme(productImeCreateForm.getIme().toUpperCase());
            productIme.setIme2(productImeCreateForm.getIme2().toUpperCase());
            productIme.setBoxIme(productImeCreateForm.getBoxIme().toUpperCase());
            productIme.setMeid(productImeCreateForm.getMeid());
            productIme.setBillId(productImeCreateForm.getBillId());
            productIme.setCreatedTime(productImeCreateForm.getCreatedTime());
            productIme.setRemarks(productImeCreateForm.getRemarks());
            productIme.setItemNumber(productImeCreateForm.getItemNumber());
            productIme.setImeReverse(StringUtils.reverse(productIme.getIme()));
            productIme.setInputType(InputTypeEnum.手工入库.name());
            productImes.add(productIme);
        }
        productImeRepository.save(productImes);
    }

    public void batchChange(ProductImeBatchChangeForm productImeBatchChangeForm) {
        if(CollectionUtil.isEmpty(productImeBatchChangeForm.getProductImeChangeFormList())){

            return;
        }
        for(ProductImeChangeForm productImeChangeForm : productImeBatchChangeForm.getProductImeChangeFormList()){

            ProductIme productIme = productImeRepository.findByEnabledIsTrueAndIme(productImeChangeForm.getIme());

            Product product = productRepository.findByEnabledIsTrueAndCompanyIdAndName(RequestUtils.getCompanyId(), productImeChangeForm.getProductName());
            productIme.setProductId(product.getId());
            productImeRepository.save(productIme);
        }
    }

    public List<ProductImeDto> batchQuery(List<String> allImeList) {
        List<ProductImeDto> result = productImeRepository.batchQuery(allImeList, RequestUtils.getCompanyId());
        cacheUtils.initCacheInput(result);
        return result;
    }

}
