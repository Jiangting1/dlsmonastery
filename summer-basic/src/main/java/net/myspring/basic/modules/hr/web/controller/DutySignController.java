package net.myspring.basic.modules.hr.web.controller;

import com.google.common.collect.Maps;
import net.myspring.basic.common.config.ExcelView;
import net.myspring.basic.common.utils.SecurityUtils;
import net.myspring.basic.modules.hr.dto.DutySignDto;
import net.myspring.basic.modules.hr.service.DutySignService;
import net.myspring.basic.modules.hr.service.OfficeService;
import net.myspring.basic.modules.hr.service.PositionService;
import net.myspring.basic.modules.hr.web.form.DutySignForm;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.json.ObjectMapperUtils;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by liuj on 2016/11/30.
 */
@RestController
@RequestMapping(value = "hr/dutySign")
public class DutySignController {

    @Autowired
    private DutySignService dutySignService;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private PositionService positionService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(HttpServletRequest request) {
        SearchEntity searchEntity = RequestUtils.getSearchEntity(request);
        Page<DutySignDto> page = dutySignService.findPage(searchEntity.getPageable(), searchEntity.getParams());
        return ObjectMapperUtils.writeValueAsString(page);
    }

    @RequestMapping(value = "getListProperty")
    public String getListProperty() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("officeList", officeService.findAll());
        map.put("positionList", positionService.findAll());
        return ObjectMapperUtils.writeValueAsString(map);
    }

    @RequestMapping(value = "save")
    public String save(DutySignForm dutySignForm, BindingResult bindingResult) {
        RestResponse restResponse = new RestResponse("签到成功", null);
        dutySignService.save(dutySignForm);
        return ObjectMapperUtils.writeValueAsString(restResponse);
    }

    @RequestMapping(value = "delete")
    public String delete(String id) {
        dutySignService.logicDeleteOne(id);
        RestResponse restResponse = new RestResponse("删除成功", ResponseCodeEnum.removed.name());
        return ObjectMapperUtils.writeValueAsString(restResponse);
    }

    @RequestMapping(value = "detail")
    public String detail(String id) {
        DutySignDto dutySignDto = dutySignService.findDto(id);
        return ObjectMapperUtils.writeValueAsString(dutySignDto);
    }

    @RequestMapping(value = "export")
    public ModelAndView export(HttpServletRequest request) {
        SearchEntity searchEntity = RequestUtils.getSearchEntity(request);
        if (searchEntity.getParams().get("dutyDateStart") == null) {
            searchEntity.getParams().put("dutyDateStart", LocalDateTime.now().minusMonths(1));
        }
        if (searchEntity.getParams().get("dutyDateEnd") == null) {
            searchEntity.getParams().put("dutyDateEnd", LocalDateTime.now());
        }
        Workbook workbook = new SXSSFWorkbook(10000);
        SimpleExcelSheet simpleExcelSheet = dutySignService.findSimpleExcelSheet(workbook, searchEntity.getParams());
        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook, "签到列表.xlsx", simpleExcelSheet);
        ExcelView excelView = new ExcelView();
        return new ModelAndView(excelView, "simpleExcelBook", simpleExcelBook);
    }
}
