package net.myspring.future.modules.basic.web.controller;

import com.google.common.collect.Maps;
import net.myspring.basic.common.util.CompanyConfigUtil;
import net.myspring.basic.modules.sys.dto.CompanyConfigCacheDto;
import net.myspring.common.enums.*;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.common.enums.*;
import net.myspring.future.modules.basic.client.DictEnumClient;
import net.myspring.future.modules.basic.client.DictMapClient;
import net.myspring.future.modules.basic.client.OfficeClient;
import net.myspring.future.modules.basic.dto.DepotStoreDto;
import net.myspring.future.modules.basic.service.DepotService;
import net.myspring.future.modules.basic.service.DepotStoreService;
import net.myspring.future.modules.basic.web.form.DepotStoreAddForm;
import net.myspring.future.modules.basic.web.form.DepotStoreForm;
import net.myspring.future.modules.basic.web.query.DepotStoreQuery;
import net.myspring.future.modules.crm.web.query.ReportQuery;
import net.myspring.util.excel.ExcelView;
import net.myspring.util.mapper.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * Created by liuj on 2017/5/12.
 */
@RestController
@RequestMapping(value = "basic/depotStore")
public class DepotStoreController {

    @Autowired
    private DepotStoreService depotStoreService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OfficeClient officeClient;
    @Autowired
    private DepotService depotService;
    @Autowired
    private DictMapClient dictMapClient;
    @Autowired
    private DictEnumClient dictEnumClient;

    @RequestMapping(method = RequestMethod.GET)
    public Page<DepotStoreDto> list(Pageable pageable, DepotStoreQuery depotStoreQuery){
        Page<DepotStoreDto> page = depotStoreService.findPage(pageable,depotStoreQuery);
        return page;
    }

    @RequestMapping(value = "findOne")
    public DepotStoreDto findOne(DepotStoreDto depotStoreDto){
        depotStoreDto=depotStoreService.findOne(depotStoreDto);
        return depotStoreDto;
    }

    @RequestMapping(value = "getForm")
    public DepotStoreForm getForm(DepotStoreForm depotStoreForm){
        depotStoreForm.getExtra().put("jointLevelList", JointLevelEnum.getList());
        return depotStoreForm;
    }

    @RequestMapping(value = "getAddForm")
    public DepotStoreAddForm getAddForm(DepotStoreAddForm depotStoreAddForm){
        depotStoreAddForm.getExtra().put("depotStoreTypeList",DepotStoreTypeEnum.getList());
        depotStoreAddForm.getExtra().put("jointLevelList", JointLevelEnum.getList());
        return depotStoreAddForm;
    }

    //代理库保存
    @RequestMapping(value = "save")
    public RestResponse save(DepotStoreForm depotStoreForm){
        depotStoreService.save(depotStoreForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    //仓库保存
    @RequestMapping(value = "saveStore")
    public RestResponse saveStore(DepotStoreAddForm depotStoreAddForm){
        depotStoreService.saveStore(depotStoreAddForm);
        return new RestResponse("保存成功",ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(DepotStoreForm depotStoreForm){
        depotStoreService.logicDelete(depotStoreForm.getId());
        return new RestResponse("删除成功",ResponseCodeEnum.removed.name());
    }

    @RequestMapping(value = "storeReport")
    public Map<String,Object> storeReport(ReportQuery reportQuery){
        Map<String,Object> map=Maps.newHashMap();
        reportQuery.setDepotIdList(depotService.filterDepotIds());
        DepotStoreQuery depotStoreQuery = BeanUtil.map(reportQuery, DepotStoreQuery.class);
        List<DepotStoreDto> list = depotStoreService.findFilter(depotStoreQuery);
        map.put("sum",depotStoreService.setReportData(list,reportQuery));
        map.put("list",list);
        return map;
    }

    @RequestMapping(value = "storeReportDetail")
    public Map<String,Integer> storeReportDetail(ReportQuery reportQuery){
        return depotStoreService.getReportDetail(reportQuery);
    }


    @RequestMapping(value = "export")
    public ModelAndView export(ReportQuery reportQuery) {
        ReportQuery depotStoreQuery;
        depotStoreQuery = reportQuery;
        depotStoreQuery.setDepotIdList(depotService.filterDepotIds());
        return new ModelAndView(new ExcelView(), "simpleExcelBook", depotStoreService.export(depotStoreQuery,reportQuery));
    }

    @RequestMapping(value = "exportDetail")
    public ModelAndView exportDetail(ReportQuery reportQuery) {
        ReportQuery depotStoreQuery;
        depotStoreQuery = reportQuery;
        depotStoreQuery.setDepotIdList(depotService.filterDepotIds());
        return new ModelAndView(new ExcelView() ,"simpleExcelBook",depotStoreService.exportDetail(reportQuery, depotStoreQuery));
    }

    @RequestMapping(value = "getReportQuery")
    public ReportQuery getReportQuery(ReportQuery reportQuery) {
        reportQuery.getExtra().put("typeList", ReportTypeEnum.getList());
        reportQuery.getExtra().put("outTypeList", OutTypeEnum.getList());
        reportQuery.getExtra().put("boolMap", BoolEnum.getMap());
        CompanyConfigCacheDto companyConfigCacheDto = CompanyConfigUtil.findByCode(redisTemplate, CompanyConfigCodeEnum.COMPANY_NAME.name());
        if (companyConfigCacheDto != null && "WZOPPO".equals(companyConfigCacheDto.getValue())) {
            reportQuery.setOutType(ProductImeStockReportOutTypeEnum.核销.name());
        } else {
            reportQuery.setOutType(ProductImeStockReportOutTypeEnum.电子保卡.name());
        }
        reportQuery.setType(ReportTypeEnum.核销.name());
        reportQuery.setScoreType(true);
        return reportQuery;
    }

    @RequestMapping(value = "getQuery")
    public DepotStoreQuery getQuery(DepotStoreQuery depotStoreQuery){
        depotStoreQuery.getExtra().put("areaList",officeClient.findByOfficeRuleName(OfficeRuleEnum.办事处.name()));
        return depotStoreQuery;
    }

}
