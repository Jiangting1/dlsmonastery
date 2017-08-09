package net.myspring.future.modules.crm.service;

import com.google.common.collect.Lists;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.exception.ServiceException;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.domain.Product;
import net.myspring.future.modules.basic.dto.DepotDto;
import net.myspring.future.modules.basic.manager.DepotManager;
import net.myspring.future.modules.basic.repository.DepotRepository;
import net.myspring.future.modules.basic.repository.ProductRepository;
import net.myspring.future.modules.crm.domain.ProductIme;
import net.myspring.future.modules.crm.domain.ProductImeSale;
import net.myspring.future.modules.crm.dto.ProductImeForSaleDto;
import net.myspring.future.modules.crm.dto.ProductImeSaleDto;
import net.myspring.future.modules.crm.repository.ProductImeRepository;
import net.myspring.future.modules.crm.repository.ProductImeSaleRepository;
import net.myspring.future.modules.crm.web.form.ProductImeSaleBackForm;
import net.myspring.future.modules.crm.web.form.ProductImeSaleDetailForm;
import net.myspring.future.modules.crm.web.form.ProductImeSaleForm;
import net.myspring.future.modules.crm.web.query.ProductImeSaleQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.ExcelUtils;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.text.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProductImeSaleService {

    @Autowired
    private ProductImeSaleRepository productImeSaleRepository;
    @Autowired
    private ProductImeRepository productImeRepository;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private DepotManager depotManager;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CacheUtils cacheUtils;

    public Page<ProductImeSaleDto> findPage(Pageable pageable, ProductImeSaleQuery productImeSaleQuery) {
        productImeSaleQuery.setDepotIdList(depotManager.filterDepotIds(RequestUtils.getAccountId()));
        Page<ProductImeSaleDto> page = productImeSaleRepository.findPage(pageable, productImeSaleQuery);
        cacheUtils.initCacheInput(page.getContent());

        return page;
    }

    public ProductImeSaleDto findDto(String id) {
        ProductImeSaleDto productImeSaleDto = productImeSaleRepository.findDto(id);
        cacheUtils.initCacheInput(productImeSaleDto);
        return productImeSaleDto;
    }

    @Transactional
    public void sale(ProductImeSaleForm productImeSaleForm) {
        List<String> imeList = productImeSaleForm.getImeList();

        String employeeId = RequestUtils.getEmployeeId();
        Integer leftCredit = getLeftCredit(employeeId);

        Map<String, ProductImeSaleDetailForm> detailFormMap = CollectionUtil.extractToMap(productImeSaleForm.getProductImeSaleDetailList(), "productImeId");
        List<ProductIme> productImeList = productImeRepository.findByEnabledIsTrueAndImeIn(imeList);
        Map<String, ProductIme> imeMap = CollectionUtil.extractToMap(productImeList, "ime");
        for(String ime : imeList){
            ProductIme productIme = imeMap.get(ime);
            if(productIme == null) {
                throw new ServiceException("串码：" + ime + "在系统中不存在；");
            }
            String saleShopId = productIme.getDepotId();
            if(detailFormMap.get(productIme.getId())!=null &&StringUtils.isNotBlank(detailFormMap.get(productIme.getId()).getSaleShopId())){
                saleShopId = detailFormMap.get(productIme.getId()).getSaleShopId();
            }

            Depot saleShop = depotRepository.findOne(saleShopId);
            if(saleShop.getIsHidden() != null && saleShop.getIsHidden()){
                throw new ServiceException("串码：" + ime + "的所属地点："+saleShop.getName()+"被隐藏，请联系文员开通门店；");
            }
            if(productIme.getProductImeSaleId()!=null) {
                throw new ServiceException("串码：" + ime + "已核销；");
            }
            if(StringUtils.isNotBlank(saleShop.getDepotStoreId())) {
                throw new ServiceException("串码：" + ime + "的所属地点为："+saleShop.getName()+"，不是门店，无法核销；");
            }
            if(!depotManager.isAccess(saleShop.getId(), true, RequestUtils.getAccountId())) {
                throw new ServiceException("您没有串码：" + ime + "所在门店："+saleShop.getName()+"的核销权限，请先将串码调拨至您管辖的门店；");
            }
            if(productIme.getProductImeUploadId() != null) {
                throw new ServiceException("串码：" + ime + "已上报,不能核销；");
            }

            ProductImeSale productImeSale = new ProductImeSale();
            productImeSale.setEmployeeId(employeeId);
            productImeSale.setProductImeId(productIme.getId());
            productImeSale.setShopId(saleShopId);
            productImeSale.setIsBack(false);
            productImeSale.setRemarks(productImeSaleForm.getRemarks());
            productImeSale.setBuyer(productImeSaleForm.getBuyer());
            productImeSale.setBuyerAge(productImeSaleForm.getBuyerAge());
            productImeSale.setBuyerSex(productImeSaleForm.getBuyerSex());
            productImeSale.setBuyerPhone(productImeSaleForm.getBuyerPhone());

            Product product = productRepository.findOne(productIme.getProductId());
            //设置积分
            Integer credit = 0;
            if(product.getRetailPrice() != null){
                credit = product.getRetailPrice().intValue()/1000;
            }
            leftCredit = leftCredit + credit;
            productImeSale.setCredit(credit);
            productImeSale.setLeftCredit(leftCredit);
            productImeSaleRepository.save(productImeSale);

            productIme.setProductImeSaleId(productImeSale.getId());
            productIme.setDepotId(saleShopId);
            productIme.setRetailShopId(saleShopId);
            productImeRepository.save(productIme);

        }
    }

    private Integer getLeftCredit(String employeeId) {
        ProductImeSale latestProductImeSale= productImeSaleRepository.findTopByEnabledIsTrueAndEmployeeIdOrderByCreatedDateDesc(employeeId);
        Integer leftCredit =0;
        if(latestProductImeSale!=null){
            leftCredit=latestProductImeSale.getLeftCredit();
        }
        return leftCredit;
    }

    public String checkForSaleBack(List<String> imeList) {

        StringBuilder sb = new StringBuilder();
        List<ProductIme> productImeList = productImeRepository.findByEnabledIsTrueAndImeIn( imeList);
        Map<String, ProductIme> imeMap = CollectionUtil.extractToMap(productImeList, "ime");
        for(String ime:imeList){
            ProductIme productIme = imeMap.get(ime);
            if(productIme == null) {
                sb.append("串码：").append(ime).append("在系统中不存在；");
            } else {
                Depot depot = depotRepository.findOne(productIme.getDepotId());

                if(productIme.getProductImeSaleId() ==null) {
                    sb.append("串码：").append(ime).append("还未被核销，不能退回；");
                } else if(!depotManager.isAccess(depot.getId(),true,RequestUtils.getAccountId())) {
                    sb.append("您没有串码：").append(ime).append("所在门店：").append(depot.getName()).append("的核销权限，请先将串码调拨至您管辖的门店；");
                }
                if(productIme.getProductImeUploadId() != null) {
                    sb.append("串码：").append(ime).append("已上报,不能退回；");
                }
            }
        }
        return sb.toString();
    }

    @Transactional
    public void saleBack(ProductImeSaleBackForm productImeSaleBackForm) {
        List<String> imeList = productImeSaleBackForm.getImeList();
        String employeeId = RequestUtils.getEmployeeId();

        List<ProductIme> productImes = productImeRepository.findByEnabledIsTrueAndImeIn(imeList);
        Integer leftCredit = getLeftCredit(employeeId);

        for(ProductIme productIme : productImes) {
            ProductImeSale productImeSale = productImeSaleRepository.findOne(productIme.getProductImeSaleId());

            productImeSale.setEnabled(false);
            productImeSaleRepository.save(productImeSale);

            Integer credit = 0-(productImeSale.getCredit()==null?0:productImeSale.getCredit());
            ProductImeSale saleBack = new ProductImeSale();
            saleBack.setEmployeeId(employeeId);
            saleBack.setProductImeId(productIme.getId());
            saleBack.setShopId(productImeSale.getShopId());
            saleBack.setRemarks(productImeSale.getRemarks());
            saleBack.setCredit(credit);
            leftCredit = leftCredit +credit;
            saleBack.setLeftCredit(leftCredit);
            saleBack.setIsBack(true);
            productImeSaleRepository.save(saleBack);

            productIme.setProductImeSaleId(null);
            productImeRepository.save(productIme);
        }
    }

    public SimpleExcelBook export(ProductImeSaleQuery productImeSaleQuery) {

        Workbook workbook = new SXSSFWorkbook(10000);

        List<SimpleExcelColumn> productImeSaleColumnList = Lists.newArrayList();
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "productImeIme", "串码"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "productImeProductName", "货品型号"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "productImeMeid", "MEID"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "shopAreaName", "办事处"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "shopOfficeName", "考核区域"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "shopName", "门店"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "depotShopAreaType", "区域类型"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "employeeName", "核销人"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "createdByName", "创建人"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "createdDate", "创建时间"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "buyer", "用户姓名"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "buyerAge", "年龄"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "buyerSex", "性别"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "buyerPhone", "电话"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "buyerSchool", "学校"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "buyerGrade", "年级"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "lotteryDate", "抽奖时间"));
        productImeSaleColumnList.add(new SimpleExcelColumn(workbook, "hongbao", "红包"));

        List<ProductImeSaleDto> productImeSaleDtoList = findPage(new PageRequest(0,10000), productImeSaleQuery).getContent();

        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("核销列表", productImeSaleDtoList, productImeSaleColumnList);
        ExcelUtils.doWrite(workbook, simpleExcelSheet);
        return new SimpleExcelBook(workbook,"核销列表"+ LocalDate.now()+".xlsx", simpleExcelSheet);

    }

    public List<ProductImeForSaleDto> findProductImeForSaleDto(String imeStr) {
        if (StringUtils.isBlank(imeStr)) {
            return new ArrayList<>();
        }
        List<String> imeList = StringUtils.getSplitList(imeStr, CharConstant.ENTER);
        List<ProductImeForSaleDto> result = productImeSaleRepository.findProductImeForSaleDto(imeList);
        cacheUtils.initCacheInput(result);

            for(ProductImeForSaleDto productImeForSaleDto : result) {
                productImeForSaleDto.setEditable(false);
                productImeForSaleDto.setFromChain(false);
                    //如果用户对此串码是可以编辑的，那么直接出库的门店为所在门店，否则说明是该串码在连锁门店，需要用户选择对应核销门店。
                    if(productImeForSaleDto.getProductImeSaleId()==null && depotManager.isAccess(productImeForSaleDto.getDepotId(),true, RequestUtils.getAccountId()) && StringUtils.isBlank(productImeForSaleDto.getDepotDepotStoreId())) {
                        productImeForSaleDto.setEditable(true);
                        //如果是关联连锁体系的，核销为自己管辖的连锁体系
                        if(!depotManager.isAccess(productImeForSaleDto.getDepotId(),false, RequestUtils.getAccountId())) {
                            productImeForSaleDto.setFromChain(true);
                            productImeForSaleDto.setAccessChainDepotList(new ArrayList<>());
                            List<Depot> chainDepotList = depotRepository.findByChainId(productImeForSaleDto.getDepotChainId());
                            for(Depot depot : chainDepotList) {
                                if(depotManager.isAccess(depot.getId(), false, RequestUtils.getAccountId())) {
                                    DepotDto depotDto = new DepotDto();
                                    depotDto.setId(depot.getId());
                                    depotDto.setName(depot.getName());
                                    productImeForSaleDto.getAccessChainDepotList().add(depotDto);
                                    if(StringUtils.isBlank(productImeForSaleDto.getSaleShopId())&&chainDepotList.size() == 1){
                                        productImeForSaleDto.setSaleShopId(depot.getId());
                                    }
                                }
                            }
                        }
                    }
            }

        return result;
    }
}
