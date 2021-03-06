package net.myspring.basic.modules.sys.web.controller;

import com.google.common.collect.Lists;
import net.myspring.basic.modules.sys.dto.CompanyConfigDto;
import net.myspring.basic.modules.sys.dto.DictMapDto;
import net.myspring.basic.modules.sys.service.CompanyConfigService;
import net.myspring.basic.modules.sys.web.form.CompanyConfigForm;
import net.myspring.basic.modules.sys.web.query.CompanyConfigQuery;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by zhucc on 2017/4/17.
 */
@RestController
@RequestMapping("sys/companyConfig")
public class CompanyConfigController {


    @Autowired
    private CompanyConfigService companyConfigService;


    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'sys:companyConfig:view')")
    public Page<CompanyConfigDto> list(Pageable pageable, CompanyConfigQuery companyConfigQuery){
        Page<CompanyConfigDto> page=companyConfigService.findPage(pageable,companyConfigQuery);
        return page;
    }

    @RequestMapping(value = "save")
    @PreAuthorize("hasPermission(null,'sys:companyConfig:edit')")
    public RestResponse save(CompanyConfigForm companyConfigForm){
        companyConfigService.save(companyConfigForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "findOne")
    public CompanyConfigDto findOne(CompanyConfigDto companyConfigDto) {
        companyConfigDto=companyConfigService.findOne(companyConfigDto);
        return companyConfigDto;
    }

    @RequestMapping(value = "getValueByCode")
    public String getValueByCode(String code) {
        if(StringUtils.isNotBlank(code)){
            String value=companyConfigService.getValueByCode(code);
            return value;
        }
        return "";
    }

    @RequestMapping(value="delete")
    @PreAuthorize("hasPermission(null,'sys:companyConfig:delete')")
    public RestResponse delete(String id){
        companyConfigService.logicDelete(id);
        RestResponse restResponse =new RestResponse("删除成功",ResponseCodeEnum.removed.name());
        return restResponse;
    }

    @RequestMapping(value="getQuery")
    public CompanyConfigQuery getQuery(CompanyConfigQuery companyConfigQuery){
        return companyConfigQuery;
    }

    @RequestMapping(value="getForm")
    public CompanyConfigForm getForm(CompanyConfigForm companyConfigForm){
        return companyConfigForm;
    }
}
