package net.myspring.future.modules.basic.web.controller;

import com.google.common.collect.Lists;
import net.myspring.cloud.modules.kingdee.domain.BdDepartment;
import net.myspring.cloud.modules.report.dto.CustomerReceiveDetailDto;
import net.myspring.cloud.modules.report.web.query.CustomerReceiveDetailQuery;
import net.myspring.common.exception.ServiceException;
import net.myspring.common.response.RestResponse;
import net.myspring.future.common.enums.OfficeRuleEnum;
import net.myspring.future.modules.basic.client.CloudClient;
import net.myspring.future.modules.basic.client.OfficeClient;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.dto.CustomerDto;
import net.myspring.future.modules.basic.dto.DepotAccountDto;
import net.myspring.future.modules.basic.dto.DepotDto;
import net.myspring.future.modules.basic.service.DepotService;
import net.myspring.future.modules.basic.web.query.DepotAccountQuery;
import net.myspring.future.modules.basic.web.query.DepotQuery;
import net.myspring.util.excel.ExcelView;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "basic/depot")
public class DepotController {

    @Autowired
    private DepotService depotService;
    @Autowired
    private CloudClient cloudClient;
    @Autowired
    private OfficeClient officeClient;

    //直营门店查询(POP申请开单类型为配件赠品用这个)
    @RequestMapping(value = "directShop")
    public List<DepotDto>  directShop(DepotQuery depotQuery) {
        depotQuery.setClientIsNull(false);
        return depotService.findShopList(depotQuery);
    }

    //代理门店查询
    @RequestMapping(value = "delegateShop")
    public List<DepotDto>  delegateShop(DepotQuery depotQuery) {
        depotQuery.setClientIsNull(true);
        return depotService.findShopList(depotQuery);
    }

    //门店查询(物料订单的代理门店也用这个)
    @RequestMapping(value = "shop")
    public List<DepotDto>  shop(DepotQuery depotQuery) {
        return depotService.findShopList(depotQuery);
    }

    //物料订单门店
    @RequestMapping(value = "adShop")
    public List<DepotDto>  adShop(DepotQuery depotQuery) {
        depotQuery.setAdShop(true);
        return depotService.findShopList(depotQuery);
    }

    //POP门店
    @RequestMapping(value = "popShop")
    public List<DepotDto>  popShop(DepotQuery depotQuery) {
        depotQuery.setPopShop(true);
        return depotService.findShopList(depotQuery);
    }

    //直营仓库查询
    @RequestMapping(value = "directStore")
    public List<DepotDto>  directStore(DepotQuery depotQuery) {
        depotQuery.setOutIdIsNull(false);
        return depotService.findStoreList(depotQuery);
    }

    //代理仓库查询
    @RequestMapping(value = "delegateStore")
    public List<DepotDto>  delegateStore(DepotQuery depotQuery) {
        depotQuery.setOutIdIsNull(true);
        return depotService.findStoreList(depotQuery);
    }

    //仓库查询
    @RequestMapping(value = "store")
    public List<DepotDto>  store(DepotQuery depotQuery) {
        return depotService.findStoreList(depotQuery);
    }

    @RequestMapping(value = "depot")
    public List<DepotDto>  depot(DepotQuery depotQuery) {
        return depotService.findDepotList(depotQuery);
    }

    @RequestMapping(value = "findByIds")
    public List<DepotDto> findByListIds(@RequestParam("idStr") List<String> ids) {
        return depotService.findByIds(ids);
    }

    @RequestMapping(value = "getDepotAccountQuery")
    public DepotAccountQuery getDepotAccountQuery(DepotAccountQuery depotAccountQuery) {
        LocalDate now = LocalDate.now();
        LocalDate dutyDateStart = now.minusDays(30);
        LocalDate dutyDateEnd = now.plusDays(30);
        depotAccountQuery.setDutyDateRange(LocalDateUtils.format(dutyDateStart) + " - "+LocalDateUtils.format(dutyDateEnd));
        depotAccountQuery.getExtra().put("areaList", officeClient.findByOfficeRuleName(OfficeRuleEnum.办事处.name()));

        return depotAccountQuery;
    }

    @RequestMapping(value = "findDepotAccountList")
    public Page<DepotAccountDto> findDepotAccountList(Pageable pageable, DepotAccountQuery depotAccountQuery) {
        return depotService.findDepotAccountList(pageable, depotAccountQuery,false);
    }

    @RequestMapping(value = "findDepotAccountDetailList")
    public List<CustomerReceiveDetailDto> findDepotAccountDetailList(String clientOutId, String dateRange) {
        CustomerReceiveDetailQuery customerReceiveDetailQuery = new CustomerReceiveDetailQuery();
        customerReceiveDetailQuery.setCustomerIdList(Collections.singletonList(clientOutId));
        if(StringUtils.isNotBlank(dateRange)){
            String[] tempParamValues = dateRange.split(" - ");
            customerReceiveDetailQuery.setDateStart(LocalDate.parse(tempParamValues[0]));
            customerReceiveDetailQuery.setDateEnd(LocalDate.parse(tempParamValues[1]));
        }

        return cloudClient.getCustomerReceiveDetailList(customerReceiveDetailQuery);
    }

    @RequestMapping(value="depotAccountExportDetail")
    public ModelAndView depotAccountExportDetail(DepotAccountQuery depotAccountQuery) {
        return new ModelAndView(new ExcelView(), "simpleExcelBook", depotService.depotAccountExportDetail(depotAccountQuery));
    }

    @RequestMapping(value="depotAccountExportConfirmation")
    public ModelAndView depotAccountExportConfirmation(DepotAccountQuery depotAccountQuery) {
        return new ModelAndView(new ExcelView(), "simpleExcelBook", depotService.depotAccountExportConfirmation(depotAccountQuery));
    }

    @RequestMapping(value="depotAccountExportAllDepots")
    public ModelAndView depotAccountExportAllDepots(DepotAccountQuery depotAccountQuery) {
        return new ModelAndView(new ExcelView(), "simpleExcelBook", depotService.depotAccountExportAllDepots(depotAccountQuery));
    }

    @RequestMapping(value = "findOne")
    public DepotDto findOne(String id) {
        if(StringUtils.isBlank(id)){
            return new DepotDto();
        }
        return depotService.findDto(id);
    }

    @RequestMapping(value = "findByOfficeId")
    public List<DepotDto> findByOfficeId(String officeId){
        List<DepotDto> depotDtos= Lists.newArrayList();
        if (StringUtils.isNotBlank(officeId)){
            depotDtos=depotService.findByOfficeId(officeId);
        }
        return depotDtos;
    }

    @RequestMapping(value = "findByDepotShopId")
    public DepotDto findByDepotShopId(String depotShopId) {
        if(StringUtils.isBlank(depotShopId)){
            return new DepotDto();
        }
        return depotService.findByDepotShopId(depotShopId);
    }

    @RequestMapping(value = "scheduleSynArea")
    public RestResponse scheduleSynArea() {
        depotService.scheduleSynArea();
        return new RestResponse("同步成功",null);
    }

    @RequestMapping(value = "synArea")
    public RestResponse synArea(DepotQuery depotQuery) {
        if(StringUtils.isBlank(depotQuery.getName())&&StringUtils.isBlank(depotQuery.getAreaId())){
            return new RestResponse("请设置过滤条件",null);
        }
        depotService.synArea(depotQuery);
        return new RestResponse("同步成功",null);
    }


    @RequestMapping(value = "getDefaultDepartMent")
    public String getDefaultDepartMent(String depotId) {
        if(StringUtils.isBlank(depotId)){
            return null;
        }
        BdDepartment bdDepartment=depotService.getDefaultDepartment(depotId);
        if(bdDepartment == null){
            return null;
        }
        return bdDepartment.getFNumber();
    }

    @RequestMapping(value = "getRecentMonthSaleAmount")
    public Map<String, Long>  getRecentMonthSaleAmount(String depotId, int monthQty) {
        if(monthQty <= 0){
            throw new ServiceException("monthQty 必须大于0");
        }
        if(StringUtils.isBlank(depotId)){
            throw new ServiceException("depotId不能为空");
        }
        return depotService.getRecentMonthSaleAmount(depotId, monthQty);
    }

    @RequestMapping(value = "searchDepartment")
    public String searchDepartment(String depotName){
        if(StringUtils.isNotBlank(depotName)){
            Depot depot=depotService.findByName(depotName);
            BdDepartment bdDepartment=depotService.getDefaultDepartment(depot.getId());
            return bdDepartment.getFFullName();
        }
        return null;
    }

    @RequestMapping(value = "getCloudQtyMap")
    public Map<String, Integer> getCloudQtyMap(String storeId) {
        if(StringUtils.isBlank(storeId)){
            return new HashMap<>();
        }
        return depotService.getCloudQtyMap(storeId);
    }

    @RequestMapping(value="findOppoCustomers")
    public List<CustomerDto> findOppoCustomers(){
        return depotService.findOppoCustomers();
    }

    @RequestMapping(value = "findByClientId")
    public List<DepotDto> findByClientId(String clientId){
        return depotService.findByClientId(clientId);
    }
}
