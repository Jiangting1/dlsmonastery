package net.myspring.future.modules.basic.web.controller;

import com.google.common.collect.Lists;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.common.enums.BillTypeEnum;
import net.myspring.future.modules.basic.dto.ProductAdApplyDto;
import net.myspring.future.modules.basic.dto.ProductDto;
import net.myspring.future.modules.basic.service.ProductService;
import net.myspring.future.modules.basic.web.form.ProductBatchForm;
import net.myspring.future.modules.basic.web.form.ProductForm;
import net.myspring.future.modules.basic.web.query.ProductQuery;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "basic/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<ProductDto> list(Pageable pageable, ProductQuery productQuery){
        Page<ProductDto> page = productService.findPage(pageable,productQuery);
        return page;
    }

    @RequestMapping(value = "filter")
    public List<ProductDto> filter(ProductQuery productQuery){
        List<ProductDto> productList = productService.findFilter(productQuery);
        return productList;
    }

    @RequestMapping(value = "findHasImeProduct")
    public List<ProductDto> findHasImeProduct(){
        List<ProductDto> productList= productService.findHasImeProduct();
        return productList;
    }

    @RequestMapping(value = "searchByNameOrCode")
    public List<ProductDto> searchByNameOrCode(String nameOrCode){
        List<ProductDto> productList = Lists.newArrayList();
        String outGroupName = BillTypeEnum.POP.name();
        if(StringUtils.isNotBlank(nameOrCode)){
            productList = productService.findByNameOrCodeAndOutGroupName(nameOrCode,outGroupName);
        }
        return productList;
    }

    @RequestMapping(value = "searchById")
    public List<ProductDto> searchById(String id){
        ProductDto productDto = productService.findOne(id);
        List<ProductDto> productList = Lists.newArrayList();
        productList.add(productDto);
        return productList;
    }

    @RequestMapping(value = "search")
    public List<ProductDto> search(String name,String code){
        List<ProductDto> productList = Lists.newArrayList();
        if(StringUtils.isNotBlank(name)){
            productList = productService.findByNameLikeHasIme(name);
        }else if(StringUtils.isNotBlank(code)){
            productList = productService.findByCodeLikeHasIme(code);
        }
        return productList;
    }

    @RequestMapping(value = "save")
    public RestResponse save(ProductForm productForm) {
        productService.save(productForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "batchSave")
    public RestResponse batchSave(ProductBatchForm productBatchForm){
        productService.batchSave(productBatchForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }


    @RequestMapping(value = "syn")
    public RestResponse syn() {
        productService.syn();
        return new RestResponse("同步成功",ResponseCodeEnum.updated.name());
    }

    @RequestMapping(value = "findOne")
    public ProductDto detail(String id){
        return productService.findOne(id);
    }

    @RequestMapping(value="getQuery")
    public ProductQuery getQuery(ProductQuery productQuery){
        return productService.getQuery(productQuery);
    }

    @RequestMapping(value = "getForm")
    public ProductForm findOne(ProductForm productForm){
        productForm=productService.getForm(productForm);
        return productForm;
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(String id) {
        productService.delete(id);
        return new RestResponse("删除成功", ResponseCodeEnum.removed.name());
    }

    @RequestMapping(value = "findAdProductAndAllowOrder")
    public List<ProductAdApplyDto> findAdProductAndAllowOrder(String billType){
        return productService.findAdProductAndAllowOrder(billType);
    }

    @RequestMapping(value = "findAdProductCodeAndAllowOrder")
    public String findAdProductCodeAndAllowOrder(){
        return productService.findAdProductCodeAndAllowOrder();
    }
}
