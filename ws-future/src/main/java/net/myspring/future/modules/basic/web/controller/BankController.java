package net.myspring.future.modules.basic.web.controller;

import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.modules.basic.domain.Bank;
import net.myspring.future.modules.basic.dto.BankDto;
import net.myspring.future.modules.basic.service.BankService;
import net.myspring.future.modules.basic.web.query.BankQuery;
import net.myspring.future.modules.basic.web.form.BankForm;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "basic/bank")
public class BankController {

    @Autowired
    private BankService bankService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<BankDto> list(Pageable pageable,BankQuery bankQuery){
        Page<BankDto> page = bankService.findPage(pageable,bankQuery);
        return page;
    }

    @RequestMapping(value = "save")
    public RestResponse save(BankForm bankForm){
        bankService.save(bankForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "syn")
    public RestResponse syn(){
        bankService.syn();
        return new RestResponse("同步成功",ResponseCodeEnum.updated.name());
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(String id) {
        bankService.logicDelete(id);
        return new RestResponse("删除成功", ResponseCodeEnum.removed.name());
    }

    @RequestMapping(value = "getQuery")
    public BankQuery getQuery(BankQuery bankQuery){
        return bankQuery;
    }

    @RequestMapping(value = "findOne")
    public BankDto findOne(String id){
        return bankService.findOne(id);
    }

    @RequestMapping(value = "findByIds")
    public List<BankDto> findById(String ids) {
        List<BankDto> bankDtoList = Lists.newArrayList();
        if(StringUtils.isNotBlank(ids)){
            List<String> idList=StringUtils.getSplitList(ids, CharConstant.COMMA);
            bankDtoList =bankService.findByIds(idList);
        }
        return bankDtoList;
    }

    @RequestMapping(value = "search")
    public List<BankDto> search(String key, Boolean includeCash) {
        List<BankDto> result = new ArrayList<>();
        if(Boolean.TRUE.equals(includeCash)){
            BankDto cashBankDto = new BankDto();
            cashBankDto.setId("0");
            cashBankDto.setName("现金");
            result.add(cashBankDto);
        }
        result.addAll(bankService.findByEnabledIsTrueAndNameContaining(StringUtils.trimToEmpty(key)));
        return result;
    }

    @RequestMapping(value = "getForm")
    public BankForm getForm(BankForm bankForm){
        return bankForm;
    }
}
