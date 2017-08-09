package net.myspring.basic.modules.hr.web.controller;

import net.myspring.basic.common.enums.AccountChangeTypeEnum;
import net.myspring.basic.modules.hr.dto.AccountChangeDto;
import net.myspring.basic.modules.hr.service.AccountChangeService;
import net.myspring.basic.modules.hr.service.PositionService;
import net.myspring.basic.modules.hr.web.form.AccountChangeForm;
import net.myspring.basic.modules.hr.web.query.AccountChangeQuery;
import net.myspring.basic.modules.sys.service.OfficeService;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.util.excel.ExcelView;
import net.myspring.util.excel.SimpleExcelBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RestController
@RequestMapping(value = "hr/accountChange")
public class AccountChangeController {

    @Autowired
    private AccountChangeService accountChangeService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private PositionService positionService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'hr:accountChange:view')")
    public Page<AccountChangeDto> list(Pageable pageable, AccountChangeQuery accountChangeQuery){
        Page<AccountChangeDto> page = accountChangeService.findPage(pageable,accountChangeQuery);
        return page;
    }

    @RequestMapping(value="getQuery")
    public AccountChangeQuery getQuery(AccountChangeQuery accountChangeQuery){
        accountChangeQuery.getExtra().put("areaList", officeService.findByOfficeRuleName("办事处"));
        accountChangeQuery.getExtra().put("typeList", AccountChangeTypeEnum.getList());
        return accountChangeQuery;
    }

    @RequestMapping(value = "findData")
    public AccountChangeForm getForm(AccountChangeQuery accountChangeQuery){
        AccountChangeForm accountChangeForm=accountChangeService.getForm(accountChangeQuery);
        accountChangeForm.getExtra().put("typeList",AccountChangeTypeEnum.getList());
        accountChangeForm.getExtra().put("positionList",positionService.findAll());
        return accountChangeForm;
    }

    @RequestMapping(value = "batchPass", method = RequestMethod.GET)
    public RestResponse batchPass(@RequestParam(value = "ids[]") String[] ids, boolean pass) {
        RestResponse restResponse=new RestResponse("审核成功",ResponseCodeEnum.audited.name());
        accountChangeService.batchPass(ids,pass);
        return restResponse;
    }

    @RequestMapping(value="audit",method=RequestMethod.GET)
    public RestResponse audit(String id,boolean pass){
        RestResponse restResponse=new RestResponse("审核成功",ResponseCodeEnum.audited.name());
        accountChangeService.pass(id,pass);
        return restResponse;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @PreAuthorize("hasPermission(null,'hr:accountChange:edit')")
    public RestResponse save( AccountChangeForm accountChangeForm) {
        accountChangeService.save(accountChangeForm);
        return new RestResponse("员工信息调整成功",null);
    }

    @RequestMapping(value="delete")
    @PreAuthorize("hasPermission(null,'hr:accountChange:delete')")
    public RestResponse delete(String id){
        accountChangeService.logicDelete(id);
        return new RestResponse("删除成功", ResponseCodeEnum.removed.name());
    }

    @RequestMapping(value = "import/template", method = RequestMethod.GET)
    public ModelAndView impotTemplate() throws IOException {
        SimpleExcelBook simpleExcelSheet = accountChangeService.findSimpleExcelSheet();
        ExcelView excelView = new ExcelView();
        return new ModelAndView(excelView, "simpleExcelBook", simpleExcelSheet);
    }

    @RequestMapping(value = "import", method = RequestMethod.POST)
    public RestResponse importFile(@RequestParam(value = "folderFileId", required = true) String folderFileId) {
        accountChangeService.batchSave(folderFileId);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }
}
