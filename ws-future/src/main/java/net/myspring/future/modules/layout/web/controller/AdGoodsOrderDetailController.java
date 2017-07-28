package net.myspring.future.modules.layout.web.controller;

import net.myspring.future.common.enums.BillTypeEnum;
import net.myspring.future.common.enums.OfficeRuleEnum;
import net.myspring.future.common.enums.SimpleProcessTypeEnum;
import net.myspring.future.modules.basic.client.OfficeClient;
import net.myspring.future.modules.layout.dto.AdGoodsOrderDetailDto;
import net.myspring.future.modules.layout.service.AdGoodsOrderDetailService;
import net.myspring.future.modules.layout.web.query.AdGoodsOrderDetailQuery;
import net.myspring.future.modules.layout.web.query.AdGoodsOrderQuery;
import net.myspring.util.excel.ExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "layout/adGoodsOrderDetail")
public class AdGoodsOrderDetailController {

    @Autowired
    private OfficeClient officeClient;
    @Autowired
    private AdGoodsOrderDetailService adGoodsOrderDetailService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<AdGoodsOrderDetailDto> list(Pageable pageable, AdGoodsOrderDetailQuery adGoodsOrderDetailQuery) {
        return adGoodsOrderDetailService.findPage(pageable, adGoodsOrderDetailQuery);
    }

    @RequestMapping(value = "getQuery")
    public AdGoodsOrderDetailQuery getQuery(AdGoodsOrderDetailQuery adGoodsOrderDetailQuery) {
        adGoodsOrderDetailQuery.getExtra().put("adGoodsOrderBillTypeList", BillTypeEnum.getList());
        adGoodsOrderDetailQuery.getExtra().put("adGoodsOrderShopAreaList", officeClient.findByOfficeRuleName(OfficeRuleEnum.办事处.name()));
        adGoodsOrderDetailQuery.getExtra().put("statusList", SimpleProcessTypeEnum.柜台订货.getAllProcessStatuses());
        return adGoodsOrderDetailQuery;
    }


    @RequestMapping(value="export")
    public ModelAndView export(AdGoodsOrderDetailQuery adGoodsOrderDetailQuery) {
        return new ModelAndView(new ExcelView(),"simpleExcelBook",adGoodsOrderDetailService.export(adGoodsOrderDetailQuery));
    }



}
