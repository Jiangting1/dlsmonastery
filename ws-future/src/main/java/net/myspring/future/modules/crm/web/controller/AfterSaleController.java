package net.myspring.future.modules.crm.web.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.response.RestResponse;
import net.myspring.future.modules.basic.client.DictEnumClient;
import net.myspring.future.modules.basic.service.ProductService;
import net.myspring.future.modules.crm.domain.AfterSale;
import net.myspring.future.modules.crm.domain.ProductIme;
import net.myspring.future.modules.crm.dto.AfterSaleDto;
import net.myspring.future.modules.crm.dto.ProductImeDto;
import net.myspring.future.modules.crm.repository.AfterSaleRepository;
import net.myspring.future.modules.crm.service.AfterSaleService;
import net.myspring.future.modules.crm.service.ProductImeService;
import net.myspring.future.modules.crm.web.form.AfterSaleForm;
import net.myspring.future.modules.crm.web.query.AfterSaleQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.ExcelView;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "crm/afterSale")
public class AfterSaleController {


    @Autowired
    private AfterSaleService afterSaleService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImeService productImeService;
    @Autowired
    private DictEnumClient dictEnumClient;
    @Autowired
    private AfterSaleRepository afterSaleRepository;

    @RequestMapping(method = RequestMethod.POST)
    public Page<AfterSaleDto> list(Pageable pageable,AfterSaleQuery afterSaleQuery) {
        Page<AfterSaleDto> page = afterSaleService.findPage(pageable, afterSaleQuery);
        return page;
    }

    @RequestMapping(value = "getFromCompanyData")
    public List<AfterSaleDto> getFromCompanyData(AfterSaleQuery afterSaleQuery) {
        List<AfterSaleDto> list=afterSaleService.findFilter(afterSaleQuery);
        return list;
    }

    @RequestMapping(value="getQuery")
    public  AfterSaleQuery getQuery(AfterSaleQuery afterSaleQuery){
        return afterSaleQuery;
    }

    @RequestMapping(value="getForm")
    public AfterSaleForm getForm(AfterSaleForm afterSaleForm){
        return afterSaleForm;
    }

    @RequestMapping(value = "formData", method = RequestMethod.GET)
    public Map<String, Object> formData(String imeStr) {
        List<ProductImeDto> list = Lists.newArrayList();
        StringBuilder stringBuilder=new StringBuilder();
        if(StringUtils.isNotBlank(imeStr)) {//判断imeStr是否为空
            List<String> imeList = StringUtils.getSplitList(imeStr, CharConstant.ENTER);
            List<ProductImeDto> productImeList=productImeService.findByImeList(imeList);
            Map<String,ProductImeDto> productImeMap=CollectionUtil.extractToMap(productImeList,"ime");
            Map<String, AfterSaleDto> afterSaleMap = afterSaleService.findDtoByImeList(imeList);
            for(String ime:imeList){
                if(!productImeMap.containsKey(ime)){
                    stringBuilder.append("串码" + ime + "在系统中不存在\n");
                }else if(afterSaleMap.containsKey(ime)) {
                    stringBuilder.append("串码" + ime + "已经做了售后单据\n");/*此处用来作提示信息*/
                } else  {
                    list.add(productImeMap.get(ime));
                }
            }
        }
        Map<String,Object> map=Maps.newHashMap();
        map.put("list",list);
        map.put("message",stringBuilder);
        return map;
    }

    @RequestMapping(value = "formDataType", method = RequestMethod.GET)
    public Map<String, Object> formDataType(String imeStr) {
        List<AfterSaleDto> list = Lists.newArrayList();
        if(StringUtils.isNotBlank(imeStr)) {//判断imeStr是否为空
            List<String> imeList = StringUtils.getSplitList(imeStr, CharConstant.ENTER);
            List<ProductImeDto> productImeList=productImeService.findByImeList(imeList);
            Map<String,ProductImeDto> productImeMap=CollectionUtil.extractToMap(productImeList,"ime");
            Map<String, AfterSaleDto> afterSaleMap = afterSaleService.findDtoTypeByImeList(imeList);
            for(String ime:imeList){
                    list.add(afterSaleMap.get(ime));
            }
        }
        Map<String,Object> map=Maps.newHashMap();
        map.put("list",list);
        return map;
    }

    @RequestMapping(value = "editFormData", method = RequestMethod.GET)
    public Map<String, Object> editFormData(String imeStr) {
        List<AfterSaleDto> list = Lists.newArrayList();
        StringBuilder stringBuilder=new StringBuilder();
        if(StringUtils.isNotBlank(imeStr)) {
            List<String> imeList = StringUtils.getSplitList(imeStr, CharConstant.ENTER);
            List<ProductImeDto> productImeList=productImeService.findByImeList(imeList);
            Map<String,ProductIme> productImeMap=CollectionUtil.extractToMap(productImeList,"ime");
            Map<String, AfterSaleDto> afterSaleMap = afterSaleService.findDtoByImeList(imeList);
            for(String ime:imeList){
                if(!productImeMap.containsKey(ime)){
                    stringBuilder.append("串码" + ime + "在系统中不存在\n");
                }else if(!afterSaleMap.containsKey(ime)) {
                    stringBuilder.append("串码" + ime + "没有找到售后单据\n");
                } else  {
                    list.add(afterSaleMap.get(ime));
                }
            }
        }
        Map<String,Object> map=Maps.newHashMap();
        map.put("list",list);
        map.put("message",stringBuilder);
        return map;
    }

    @RequestMapping(value="searchImeMap" ,method=RequestMethod.GET)
    public Map<String,AfterSaleDto> searchImeMap(String imeStr){
        Map<String,AfterSaleDto> map = Maps.newHashMap();
        if(StringUtils.isNotBlank(imeStr)) {
            List<String> imeList=StringUtils.getSplitList(imeStr,CharConstant.ENTER);
            map=afterSaleService.findDtoByImeList(imeList);
        }
        return map;
    }


    @RequestMapping(value = "save", method = RequestMethod.POST)
    public RestResponse save(String data,String toStoreDate) {
        List<List<String>> datas = ObjectMapperUtils.readValue(HtmlUtils.htmlUnescape(data), ArrayList.class);
        if(CollectionUtil.isEmpty(datas)) {
            return new RestResponse("保存失败", null,false);
        }
        afterSaleService.save(datas, LocalDateUtils.parse(toStoreDate));
        return new RestResponse("保存成功",null);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public RestResponse update(String data) {
        List<List<String>> datas = ObjectMapperUtils.readValue(HtmlUtils.htmlUnescape(data), ArrayList.class);
        if(CollectionUtil.isEmpty(datas)) {
            return new RestResponse("保存失败", null,false);
        }
        afterSaleService.update(datas);
        return new RestResponse("保存成功",null);
    }

    @RequestMapping(value = "fromCompany", method = RequestMethod.POST)
    public RestResponse fromCompany(String data,String fromCompanyDate) {
        List<List<String>> datas = ObjectMapperUtils.readValue(HtmlUtils.htmlUnescape(data), ArrayList.class);
        if(CollectionUtil.isEmpty(datas)) {
            return new RestResponse("保存失败", null,false);
        }
        afterSaleService.fromCompany(datas,LocalDateUtils.parse(fromCompanyDate));
        return new RestResponse("保存成功",null);
    }

    @RequestMapping(value = "toCompany", method = RequestMethod.POST)
    public RestResponse toCompany(String imeStr,AfterSale afterSale) {
        List<String> imes=StringUtils.getSplitList(imeStr,CharConstant.ENTER);
        if(CollectionUtil.isEmpty(imes)) {
            return new RestResponse("保存失败", null,false);
        }
        afterSaleService.toCompany(imes,afterSale.getToCompanyDate(),afterSale.getToCompanyRemarks());
        return new RestResponse("保存成功",null);
    }

    @RequestMapping(value = "toCompanyForm")
    public Map<String, Object> toCompanyForm(String imeStr) {
        Map<String,Object> map=Maps.newHashMap();
        StringBuilder stringBuilder=new StringBuilder();
        if(StringUtils.isNotBlank(imeStr)) {
            final List<String> imeList = StringUtils.getSplitList(imeStr, CharConstant.ENTER);
            Map<String,AfterSaleDto> afterSaleMap =afterSaleService.findDtoByImeList(imeList);
            List<AfterSaleDto> afterSales = Lists.newArrayList();
            for(String ime:imeList) {
                if(!afterSaleMap.containsKey(ime)) {
                    stringBuilder.append("串码" + ime + "没有找到售后单据\n");
                } else if(afterSaleMap.get(ime).getToCompanyToFinance()) {
                    stringBuilder.append("串码" + ime + "已经返回工厂并且数据已同步到财务系统\n");
                } else {
                    afterSales.add(afterSaleMap.get(ime));
                }
            }
            //显示串码数量
            if(CollectionUtil.isNotEmpty(afterSales)) {
                List<String> productImeIdList = Lists.newArrayList();
                for(AfterSaleDto afterSale:afterSales) {
                    productImeIdList.add(afterSale.getBadProductImeId());
                }
                List<ProductIme> productImeList=productImeService.findByIds(productImeIdList);
                Map<String,Integer> qtyMap = productImeService.findProductImeSearchResult(CollectionUtil.extractToList(productImeList,"ime")).getProductQtyMap();
                map.put("qtyMap",qtyMap);
            }
            map.put("message",stringBuilder);
            map.put("list",afterSales);
            map.put("toCompanyDate",LocalDate.now());
        }
        return map;
    }


    @RequestMapping(value = "synToFinance")
    public RestResponse synToFinance() {
        afterSaleService.synToFinance();
        return new RestResponse("同步成功",null);
    }

    @RequestMapping(value = "delete")
    public RestResponse logicDelete(String id) {
        afterSaleService.delete(id);
        return new RestResponse("删除成功",null);
    }

    @RequestMapping(value = "exportData",method = RequestMethod.GET)
    public ModelAndView exportData(AfterSaleQuery afterSaleQuery) throws IOException{
        return new ModelAndView(new ExcelView(),"simpleExcelBook",afterSaleService.export(afterSaleQuery));
    }
}
