package net.myspring.task.tool.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by guolm on 2017/6/11.
 */

@FeignClient("ws-future")
public interface FutureOppoClient {

    @RequestMapping(method = RequestMethod.GET, value = "/third/factory/oppo/pullFactoryData")
    String pullFactoryData(@RequestParam(value = "companyName") String companyName,@RequestParam(value = "date") String date);

}
