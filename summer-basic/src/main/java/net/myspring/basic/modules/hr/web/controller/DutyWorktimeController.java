package net.myspring.basic.modules.hr.web.controller;

import net.myspring.basic.modules.hr.domain.DutyWorktime;
import net.myspring.basic.modules.hr.dto.DutyWorktimeDto;
import net.myspring.basic.modules.hr.service.DutyWorktimeService;
import net.myspring.common.response.RestResponse;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.time.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping(value = "hr/dutyWorktime")
public class DutyWorktimeController {

    @Autowired
    private DutyWorktimeService dutyWorktimeService;

    @RequestMapping(method = RequestMethod.GET)
    public String findPage(HttpServletRequest request){
        SearchEntity searchEntity = RequestUtils.getSearchEntity(request);
        Page<DutyWorktimeDto> page = dutyWorktimeService.findPage(searchEntity.getPageable(),searchEntity.getParams());
        return ObjectMapperUtils.writeValueAsString(page);
    }

    @RequestMapping(value = "export")
    public ModelAndView export(Long accountId,HttpServletRequest request){
        SearchEntity searchEntity = RequestUtils.getSearchEntity(request);
        String dutyDateBTW = (String)searchEntity.getParams().get("dutyDate");
        if(!dutyDateBTW.equals("")) {
            String[] dutyDateBetween = dutyDateBTW.split(" - ");
            LocalDate dateStart = LocalDateUtils.parse(dutyDateBetween[0]);
            LocalDate dateEnd = LocalDateUtils.parse(dutyDateBetween[1]);
            Map<String, DutyWorktime> getWorktimeMap = dutyWorktimeService.getWorktimeMap(accountId,dateStart,dateEnd);
        }
        return null;
    }

    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importFile(@RequestParam(value = "importFile", required = true) MultipartFile[] importFile, String month, String remarks) {
        RestResponse restResponse =new RestResponse("上传成功",null);
        return ObjectMapperUtils.writeValueAsString(restResponse);
    }

}
