package net.myspring.tool.common.client;

import net.myspring.tool.modules.future.dto.EmployeeDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("summer-basic")
public interface EmployeeClient {

    @RequestMapping(method = RequestMethod.GET, value = "/hr/employee/findAll")
    List<EmployeeDto> findAll();
}
