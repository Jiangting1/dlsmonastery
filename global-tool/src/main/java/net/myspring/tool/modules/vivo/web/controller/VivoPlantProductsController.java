package net.myspring.tool.modules.vivo.web.controller;

import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.tool.modules.future.dto.ProductDto;
import net.myspring.tool.modules.future.service.FutureProductService;
import net.myspring.tool.modules.oppo.dto.OppoPlantAgentProductSelDto;
import net.myspring.tool.modules.oppo.service.OppoPlantAgentProductSelService;
import net.myspring.tool.modules.oppo.web.form.OppoPlantAgentProductSelForm;
import net.myspring.tool.modules.oppo.web.query.OppoPlantAgentProductSelQuery;
import net.myspring.util.collection.CollectionUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "factory/vivo/vivoPlantAgentProductSel")
public class VivoPlantAgentProductSelController {
    @Autowired
    private OppoPlantAgentProductSelService oppoPlantAgentProductSelService;
    @Autowired
    private FutureProductService futureProductService;

    @RequestMapping(value = "findAll")
    public List<OppoPlantAgentProductSelDto> findAll(OppoPlantAgentProductSelQuery oppoPlantAgentProductSelQuery){
        List<OppoPlantAgentProductSelDto> oppoPlantAgentProductSelDtoList = oppoPlantAgentProductSelService.findAll(oppoPlantAgentProductSelQuery);
        return oppoPlantAgentProductSelDtoList;
    }

    @RequestMapping(value = "getQuery")
    public OppoPlantAgentProductSelQuery getQuery(OppoPlantAgentProductSelQuery oppoPlantAgentProductSelQuery){
        return oppoPlantAgentProductSelQuery;
    }

    @RequestMapping(value = "getForm")
    public OppoPlantAgentProductSelForm getForm(OppoPlantAgentProductSelForm oppoPlantAgentProductSelForm){
        oppoPlantAgentProductSelForm = oppoPlantAgentProductSelService.getForm(oppoPlantAgentProductSelForm);
        oppoPlantAgentProductSelForm.getExtra().put("productNames", CollectionUtil.extractToList(futureProductService.findHasImeProduct(),"name"));
        return oppoPlantAgentProductSelForm;
    }

    @RequestMapping(value = "save")
    public RestResponse save(String data){
        if(StringUtils.isNotBlank(data)){
            List<ProductDto> productDtoList = futureProductService.findHasImeProduct();
            oppoPlantAgentProductSelService.save(productDtoList,data);
        }
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

}