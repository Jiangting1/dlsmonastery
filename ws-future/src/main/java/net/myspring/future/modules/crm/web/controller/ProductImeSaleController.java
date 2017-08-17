package net.myspring.future.modules.crm.web.controller;


import net.myspring.common.constant.CharConstant;
import net.myspring.common.exception.ServiceException;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.modules.basic.dto.DepotDto;
import net.myspring.future.modules.basic.service.DepotService;
import net.myspring.future.modules.basic.web.query.DepotQuery;
import net.myspring.future.modules.crm.dto.ProductImeForSaleDto;
import net.myspring.future.modules.crm.dto.ProductImeSaleDto;
import net.myspring.future.modules.crm.service.ProductImeSaleService;
import net.myspring.future.modules.crm.web.form.ProductImeSaleBackForm;
import net.myspring.future.modules.crm.web.form.ProductImeSaleForm;
import net.myspring.future.modules.crm.web.form.ProductImeSaleQtyForm;
import net.myspring.future.modules.crm.web.query.ProductImeSaleQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.ExcelView;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping(value = "crm/productImeSale")
public class ProductImeSaleController {

    @Autowired
    private ProductImeSaleService productImeSaleService;
    @Autowired
    private DepotService depotService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<ProductImeSaleDto> list(Pageable pageable, ProductImeSaleQuery productImeSaleQuery){
        if(StringUtils.isNotBlank(productImeSaleQuery.getIme())&&productImeSaleQuery.getIme().length()<6){
            throw new ServiceException("请输入至少六位串码尾数");
        }
        return productImeSaleService.findPage(pageable, productImeSaleQuery);
    }

    @RequestMapping(value = "getQuery")
    public ProductImeSaleQuery getQuery(ProductImeSaleQuery productImeSaleQuery) {
        return productImeSaleQuery;
    }

    @RequestMapping(value = "findDto")
    public ProductImeSaleDto findDto(String id) {
        if(StringUtils.isBlank(id)){
            return new ProductImeSaleDto();
        }
        return productImeSaleService.findDto(id);
    }

    @RequestMapping(value = "checkForSale")
    public String checkForSale(String imeStr) {

        List<String> imeList = StringUtils.getSplitList(imeStr, CharConstant.ENTER);
        if(imeList.size() == 0){
            return null;
        }
        return productImeSaleService.checkForSale(imeList);
    }

    @RequestMapping(value = "checkForSaleBack")
    public String checkForSaleBack(String imeStr) {
        List<String> imeList = StringUtils.getSplitList(imeStr, CharConstant.ENTER);
        if(imeList.size() == 0){
            return null;
        }
        return productImeSaleService.checkForSaleBack(imeList);
    }

    @RequestMapping(value="findProductImeForSaleDto")
    public List<ProductImeForSaleDto> findProductImeForSaleDto(String imeStr) {
        return productImeSaleService.findProductImeForSaleDto(imeStr);
    }

    @RequestMapping(value = "sale")
    @Deprecated
    public RestResponse sale(ProductImeSaleForm productImeSaleForm) {
        List<String> imeList = productImeSaleForm.getImeList();
        if(CollectionUtil.isEmpty(imeList)){
            throw new ServiceException("没有输入任何有效的串码");
        }

        productImeSaleService.sale(productImeSaleForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "saleIme")
    public RestResponse saleIme(ProductImeSaleForm productImeSaleForm) {
        List<String> imeList = productImeSaleForm.getImeList();
        if(CollectionUtil.isEmpty(imeList)){
            throw new ServiceException("没有输入任何有效的串码");
        }
        if(StringUtils.isBlank(productImeSaleForm.getSaleShopId())){
            throw new ServiceException("核销门店不可以为空");
        }

        productImeSaleService.saleIme(productImeSaleForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "saleBack")
    public RestResponse saleBack(ProductImeSaleBackForm productImeSaleBackForm) {
        List<String> imeList = productImeSaleBackForm.getImeList();
        if(CollectionUtil.isEmpty(imeList)){
            throw new ServiceException("没有输入任何有效的串码");
        }

        productImeSaleService.saleBack(productImeSaleBackForm);

        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value="export")
    public ModelAndView export(ProductImeSaleQuery productImeSaleQuery) {
        if(StringUtils.isNotBlank(productImeSaleQuery.getIme())&&productImeSaleQuery.getIme().length()<6){
            throw new ServiceException("请输入至少六位串码尾数");
        }
        return new ModelAndView(new ExcelView(), "simpleExcelBook", productImeSaleService.export(productImeSaleQuery));
    }

    @RequestMapping(value="getSaleForm")
    public  ProductImeSaleForm getSaleForm(ProductImeSaleForm productImeSaleForm) {
        List<DepotDto> depotList = depotService.findShopList(new DepotQuery());
        if(depotList.size() == 1){
            productImeSaleForm.getExtra().put("defaultSaleShopId", depotList.get(0).getId());
        }else{
            productImeSaleForm.getExtra().put("defaultSaleShopId", null);
        }

        return productImeSaleForm;
    }

    @RequestMapping(value="getSaleBackForm")
    public  ProductImeSaleBackForm getSaleBackForm(ProductImeSaleBackForm productImeSaleBackForm) {
        return productImeSaleBackForm;
    }

    //vivo延保系统调用(获取该导购日销量和月销量)
    @RequestMapping(value = "getSaleQty")
    public ProductImeSaleQtyForm getSaleQty(){
        return productImeSaleService.getSaleQty();
    }
}
