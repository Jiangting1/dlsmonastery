package net.myspring.future.modules.crm.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSFile;
import net.myspring.common.constant.CharConstant;
import net.myspring.future.common.enums.SaleSumTypeEnum;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.client.OfficeClient;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.domain.Product;
import net.myspring.future.modules.basic.repository.ProductRepository;
import net.myspring.future.modules.crm.domain.ProductIme;
import net.myspring.future.modules.crm.dto.ProductImeDto;
import net.myspring.future.modules.crm.dto.ProductImeHistoryDto;
import net.myspring.future.modules.crm.dto.ProductImeSaleReportDto;
import net.myspring.future.modules.crm.repository.ProductImeRepository;
import net.myspring.future.modules.crm.web.query.ProductImeQuery;
import net.myspring.future.modules.crm.web.query.ProductImeSaleQuery;
import net.myspring.future.modules.crm.web.query.ProductImeSaleReportQuery;
import net.myspring.future.modules.crm.web.query.ProductImeStockReportQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.ExcelUtils;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.text.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.Request;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.ByteArrayInputStream;
import java.util.*;

@Service
@Transactional
public class ProductImeService {

    @Autowired
    private ProductImeRepository productImeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private GridFsTemplate tempGridFsTemplate;
    @Autowired
    private OfficeClient officeClient;

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
        return productImeRepository.findProductImeDto(id);
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

    public String export(ProductImeQuery productImeQuery) {

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
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "productImeUploadCreatedDate", "保卡上报日期"));
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "productImeUploadEmployeeName", "保卡上报人"));
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "lastModifiedBy", "更新人"));
            simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "lastModifiedDate", "更新时间"));

            List<ProductImeDto> productImeDtoList = productImeRepository.findPage(new PageRequest(0, 10000) , productImeQuery).getContent();
            cacheUtils.initCacheInput(productImeDtoList);

            SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("串码列表", productImeDtoList, simpleExcelColumnList);
            SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook,"串码列表"+ UUID.randomUUID()+".xlsx",simpleExcelSheet);
            ByteArrayInputStream byteArrayInputStream= ExcelUtils.doWrite(simpleExcelBook.getWorkbook(),simpleExcelBook.getSimpleExcelSheets());
            GridFSFile gridFSFile = tempGridFsTemplate.store(byteArrayInputStream,simpleExcelBook.getName(),"application/octet-stream; charset=utf-8", RequestUtils.getDbObject());
            return StringUtils.toString(gridFSFile.getId());
        }


    public List<ProductIme> findByImeLike(String imeReverse,String shopId){
        List<ProductIme> productImeList = productImeRepository.findTop20ByDepotIdAndImeReverseStartingWithAndEnabledIsTrue(shopId,imeReverse);
        return productImeList;
    }

    public List<ProductImeSaleReportDto> saleReport(ProductImeSaleReportQuery productImeSaleReportQuery) {
        List<ProductImeSaleReportDto> productImeSaleReportList=Lists.newArrayList();
        productImeSaleReportQuery.setOfficeIdList(officeClient.getOfficeFilterIds(RequestUtils.getRequestEntity().getOfficeId()));
        Map<String,List<String>>  childOfficeMap=Maps.newHashMap();
        if(StringUtils.isNotBlank(productImeSaleReportQuery.getOfficeId())){
            productImeSaleReportQuery.getOfficeIdList().addAll(officeClient.getChildOfficeIds(productImeSaleReportQuery.getOfficeId()));
            childOfficeMap=officeClient.getChildOfficeMap(productImeSaleReportQuery.getOfficeId());
        }
        if("电子报卡".equals(productImeSaleReportQuery.getOutType())){
            productImeSaleReportList=productImeRepository.findBaokaSaleReport(productImeSaleReportQuery);
        }else {
            productImeSaleReportList=productImeRepository.findSaleReport(productImeSaleReportQuery);
        }
        if(StringUtils.isNotBlank(productImeSaleReportQuery.getOfficeId())){
            Map<String,ProductImeSaleReportDto> map=Maps.newHashMap();
            for(ProductImeSaleReportDto productImeSaleReportDto:productImeSaleReportList){
                String key=getOfficeKey(childOfficeMap,productImeSaleReportDto.getOfficeId());
                if(map.containsKey(key)){
                    map.get(key).addQty(productImeSaleReportDto.getQty());
                }else {
                    ProductImeSaleReportDto productImeSaleReport=new ProductImeSaleReportDto(key,productImeSaleReportDto.getQty());
                    map.put(key,productImeSaleReport);
                }
            }
            cacheUtils.initCacheInput(map.values());
            return Lists.newArrayList(map.values());
        }else {
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

    public Page stockReport(Pageable pageable, ProductImeStockReportQuery productImeStockReportQuery) {
        return null;
    }
}
