package net.myspring.future.modules.layout.web.controller;

import net.myspring.future.common.enums.AuditStatusEnum;
import net.myspring.future.modules.basic.service.DepotService;
import net.myspring.future.modules.basic.service.ProductService;
import net.myspring.future.modules.layout.dto.ShopAllotDto;
import net.myspring.future.modules.layout.service.ShopAllotDetailService;
import net.myspring.future.modules.layout.service.ShopAllotService;
import net.myspring.future.modules.layout.web.form.ShopAllotDetailForm;
import net.myspring.future.modules.layout.web.form.ShopAllotForm;
import net.myspring.future.modules.layout.web.query.ShopAllotQuery;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "crm/shopAllot")
public class ShopAllotController {

    @Autowired
    private ShopAllotService shopAllotService;

    @Autowired
    private DepotService depotService;


    @Autowired
    private ProductService productService;
    @Autowired
    private ShopAllotDetailService shopAllotDetailService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<ShopAllotDto> list(Pageable pageable, ShopAllotQuery shopAllotQuery){
        Page<ShopAllotDto> page = shopAllotService.findPage(pageable, shopAllotQuery);
        return page;
    }

    @RequestMapping(value = "delete")
    public String delete() {
        return null;
    }

    @RequestMapping(value = "save")
    public String save() {
        return null;
    }

    @RequestMapping(value = "audit")
    public String audit() {
        return null;
    }

    @RequestMapping(value = "getShopAllotDetailFormList")
    public ShopAllotForm getShopAllotDetailFormList(ShopAllotForm shopAllotForm) {
        if(shopAllotForm.getFromShopId()==null || shopAllotForm.getToShopId()==null){
            ShopAllotForm resultForm = new ShopAllotForm();
            resultForm.setSuccess(Boolean.TRUE);
            return resultForm;
        }

        String message = shopAllotService.checkShop(shopAllotForm.getFromShopId(), shopAllotForm.getToShopId());

        if(!StringUtils.isBlank(message)){
            ShopAllotForm resultForm = new ShopAllotForm();
            resultForm.setMessage(message);
            resultForm.setSuccess(Boolean.FALSE);
            return resultForm;
        }

        List<ShopAllotDetailForm> resultList = null;
        if(shopAllotForm.isCreate()){
            resultList = shopAllotDetailService.getShopAllotDetailListForNew(shopAllotForm.getFromShopId(), shopAllotForm.getToShopId());
        }else{
            resultList = shopAllotDetailService.getShopAllotDetailListForEdit(shopAllotForm.getId(), shopAllotForm.getFromShopId(), shopAllotForm.getToShopId());
        }

        ShopAllotForm resultForm = new ShopAllotForm();
        resultForm.setShopAllotDetailFormList(resultList);
        resultForm.setSuccess(Boolean.TRUE);

        return resultForm;

    }

    @RequestMapping(value="findForm")
    public ShopAllotForm findForm(ShopAllotForm shopAllotForm ) {
        ShopAllotForm result = shopAllotService.findForm(shopAllotForm);

        return result;
    }


    @RequestMapping(value="getQuery")
    public ShopAllotQuery getQuery(ShopAllotQuery shopAllotQuery) {
        shopAllotQuery.setStatusList(AuditStatusEnum.getList());
        return shopAllotQuery;
    }





}
