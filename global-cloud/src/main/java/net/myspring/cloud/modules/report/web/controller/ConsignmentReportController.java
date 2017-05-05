package net.myspring.cloud.modules.report.web.controller;

import com.google.common.collect.Lists;
import net.myspring.cloud.common.enums.CharEnum;
import net.myspring.cloud.common.enums.DateFormat;
import net.myspring.cloud.common.enums.KingdeeNameEnum;
import net.myspring.cloud.common.utils.SecurityUtils;
import net.myspring.cloud.modules.report.dto.ConsignmentDto;
import net.myspring.cloud.modules.report.service.ConsignmentReportService;
import net.myspring.cloud.modules.report.web.form.ConsignmentReportForm;
import net.myspring.cloud.modules.report.web.query.ConsignmentReportQuery;
import net.myspring.cloud.modules.sys.service.KingdeeBookService;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "report/consignmentReport")
public class ConsignmentReportController {
    @Autowired
    private ConsignmentReportService consignmentReportService;
    @Autowired
    private KingdeeBookService kingdeeBookService;

    @RequestMapping(value = "report")
    public ConsignmentReportForm report(ConsignmentReportQuery consignmentReportQuery) {
        ConsignmentReportForm form = new ConsignmentReportForm();
        String companyId = SecurityUtils.getCompanyId();
        if (kingdeeBookService.isWZOPPO(companyId)) {
            LocalDate dateStart = consignmentReportQuery.getStartDate();
            LocalDate dateEnd = consignmentReportQuery.getEndDate();
            List<ConsignmentDto> consignmentReport = consignmentReportService.findConsignmentReport(dateStart, dateEnd);
            form.setConsignmentDtoList(consignmentReport);
            form.setDateRange(dateStart + " - " + dateEnd);
        }else {
            form.setDateRange("请选择温州的账套");
        }
        return form;
    }

    @RequestMapping(value = "export")
    public ModelAndView export(String dateRange, String companyName) {
        List<ConsignmentDto> body = Lists.newArrayList();
        LocalDate dateStart = LocalDate.now().minusWeeks(1);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        if (StringUtils.isNotBlank(dateRange)) {
            String[] dates = dateRange.split(CharEnum.WAVE_LINE.getValue());
            dateStart = LocalDate.parse(dates[0], DateTimeFormatter.ofPattern(DateFormat.DATE.getValue()));
            dateEnd = LocalDate.parse(dates[1], DateTimeFormatter.ofPattern(DateFormat.DATE.getValue()));
        }
        //暂仅限于WZOPPO
        if (KingdeeNameEnum.WZOPPO.name().equals(companyName) || KingdeeNameEnum.WZOPPO2016.name().equals(companyName)) {
            body = consignmentReportService.findConsignmentReport(dateStart, dateEnd);
        }

        return null;
    }
}
