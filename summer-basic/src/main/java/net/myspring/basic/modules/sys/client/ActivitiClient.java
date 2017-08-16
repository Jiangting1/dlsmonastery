package net.myspring.basic.modules.sys.client;

import net.myspring.general.modules.sys.dto.ActivitiCompleteDto;
import net.myspring.general.modules.sys.dto.ActivitiDetailDto;
import net.myspring.general.modules.sys.dto.ActivitiStartDto;
import net.myspring.general.modules.sys.form.ActivitiCompleteForm;
import net.myspring.general.modules.sys.form.ActivitiStartForm;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Created by wangzm on 2017/4/25.
 */
@FeignClient("summer-general")
public interface ActivitiClient {

    @RequestMapping(method = RequestMethod.GET, value = "/sys/activiti/start")
    ActivitiStartDto start(ActivitiStartForm activitiStartForm);

    @RequestMapping(method = RequestMethod.GET, value = "/sys/activiti/complete")
    ActivitiCompleteDto complete(ActivitiCompleteForm activitiCompleteForm);

    @RequestMapping(method = RequestMethod.GET, value = "/sys/activiti/completeBatch")
    Map<String,ActivitiCompleteDto> completeBatch(List<ActivitiCompleteForm> activitiCompleteFormList);

    @RequestMapping(method = RequestMethod.GET, value = "/sys/activiti/detail")
    List<ActivitiDetailDto> getActivitiDetail(@RequestParam("processInstanceId") String processInstanceId);
}
