package net.myspring.basic.modules.hr.web.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.basic.common.enums.AccountChangeTypeEnum;
import net.myspring.basic.common.utils.Const;
import net.myspring.basic.common.utils.RequestUtils;
import net.myspring.basic.modules.hr.dto.AccountChangeDto;
import net.myspring.basic.modules.hr.service.AccountChangeService;
import net.myspring.basic.modules.hr.service.OfficeService;
import net.myspring.basic.modules.hr.service.PositionService;
import net.myspring.basic.modules.hr.web.form.AccountChangeForm;
import net.myspring.common.domain.SearchEntity;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.StringUtils;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "hr/accountChange")
public class AccountChangeController {

    @Autowired
    private AccountChangeService accountChangeService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private PositionService positionService;

    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail(AccountChangeForm accountChangeForm) {
        Map<String,Object> paramMap= Maps.newHashMap();
        paramMap.put("accountChange", accountChangeForm);
        return ObjectMapperUtils.writeValueAsString(paramMap);
    }

    @RequestMapping(value = "audit", method = RequestMethod.GET)
    public String audit(AccountChangeForm accountChangeForm,boolean pass,String comment) {
        return null;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(HttpServletRequest request){
        SearchEntity searchEntity = RequestUtils.getSearchEntity(request);
        Page<AccountChangeDto> page = accountChangeService.findPage(searchEntity.getPageable(),searchEntity.getParams());
        return ObjectMapperUtils.writeValueAsString(page);
    }

    @RequestMapping(value="getListProperty")
    public String getListProperty(){
        Map<String,Object> map = Maps.newHashMap();
        map.put("areas",officeService.findByType(Const.OFFICE_TYPE_AREA));
        map.put("types", AccountChangeTypeEnum.values());
        return ObjectMapperUtils.writeValueAsString(map);
    }

    @RequestMapping(value = "getFormProperty")
    public String getFormProperty(){
        Map<String,Object> map = Maps.newHashMap();
        map.put("types",AccountChangeTypeEnum.values());
        map.put("positions",positionService.findAll());
        return ObjectMapperUtils.writeValueAsString(map);
    }

    @RequestMapping(value = "findOne")
    public String findOne(String id ){
        AccountChangeDto accountChangeDto=accountChangeService.findDto(id);
        return ObjectMapperUtils.writeValueAsString(accountChangeDto);
    }
}
