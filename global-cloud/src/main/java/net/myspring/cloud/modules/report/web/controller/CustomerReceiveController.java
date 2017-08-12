package net.myspring.cloud.modules.report.web.controller;

import net.myspring.cloud.modules.kingdee.web.query.BdCustomerQuery;
import net.myspring.cloud.modules.report.dto.CustomerReceiveDetailDto;
import net.myspring.cloud.modules.report.dto.CustomerReceiveDto;
import net.myspring.cloud.modules.report.service.CustomerReceiveService;
import net.myspring.cloud.modules.report.web.query.CustomerReceiveDetailQuery;
import net.myspring.cloud.modules.report.web.query.CustomerReceiveQuery;
import net.myspring.util.excel.ExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 客户应收
 */
@RestController
@RequestMapping(value = "report/customerReceive")
public class CustomerReceiveController {
    @Autowired
    private CustomerReceiveService customerReceiveService;

    @RequestMapping(value = "list",method = RequestMethod.GET)
    public List<CustomerReceiveDto> listGet(CustomerReceiveQuery customerReceiveQuery) {
        List<CustomerReceiveDto> customerReceiveDtoList =  customerReceiveService.findCustomerReceiveDtoList(customerReceiveQuery);
        return customerReceiveDtoList;
    }

    @RequestMapping(value = "list",method = RequestMethod.POST)
    public List<CustomerReceiveDto> list(@RequestBody CustomerReceiveQuery customerReceiveQuery) {
        List<CustomerReceiveDto> customerReceiveDtoList =  customerReceiveService.findCustomerReceiveDtoList(customerReceiveQuery);
        return customerReceiveDtoList;
    }

    @RequestMapping(value = "detail",method = RequestMethod.GET)
    public List<CustomerReceiveDetailDto> detailGet(CustomerReceiveDetailQuery customerReceiveDetailQuery) {
        return customerReceiveService.findCustomerReceiveDetailDtoList(customerReceiveDetailQuery);
    }

    @RequestMapping(value = "detail",method = RequestMethod.POST)
    public List<CustomerReceiveDetailDto> detail(@RequestBody CustomerReceiveDetailQuery customerReceiveDetailQuery) {
        return customerReceiveService.findCustomerReceiveDetailDtoList(customerReceiveDetailQuery);
    }

    @RequestMapping(value = "getQuery")
    public CustomerReceiveQuery getQuery() {
        return customerReceiveService.getQuery();
    }

    @RequestMapping(value = "export")
    public ModelAndView export(BdCustomerQuery bdCustomerQuery) {

        return new ModelAndView(new ExcelView(), "simpleExcelBook", customerReceiveService.export(bdCustomerQuery));
    }

    @RequestMapping(value = "exportDetailOne")
    public ModelAndView exportDetailOne(CustomerReceiveQuery customerReceiveQuery){
        return new ModelAndView(new ExcelView(), "simpleExcelBook", customerReceiveService.exportDetailOne(customerReceiveQuery));
    }

}
