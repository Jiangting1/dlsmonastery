package net.myspring.tool.common.client;

import net.myspring.tool.modules.future.dto.EmployeeDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("summer-basic")
public interface EmployeeClient {

    @RequestMapping(method = RequestMethod.GET, value = "/hr/employee/findAll")
    List<EmployeeDto> findAll(@RequestParam(value = "companyName")String companyName);

    @RequestMapping(method = RequestMethod.GET, value = "/hr/employee/findEmployeeInfo")
    List<EmployeeDto> findEmployeeInfo(@RequestParam(value = "companyName")String companyName);
}
