package net.myspring.cloud.modules.sys.web.controller;

import net.myspring.cloud.common.enums.ExtendTypeEnum;
import net.myspring.cloud.modules.input.dto.KingdeeSynDto;
import net.myspring.cloud.modules.sys.domain.KingdeeSyn;
import net.myspring.cloud.modules.sys.dto.KingdeeSynReturnDto;
import net.myspring.cloud.modules.sys.service.KingdeeSynService;
import net.myspring.cloud.modules.sys.web.query.KingdeeSynQuery;
import net.myspring.common.exception.ServiceException;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.util.mapper.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 金蝶同步
 * Created by lihx on 2017/6/21.
 */
@RestController
@RequestMapping(value = "sys/kingdeeSyn")
public class KingdeeSynController {
    @Autowired
    private KingdeeSynService kingdeeSynService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<KingdeeSynDto> list(Pageable pageable, KingdeeSynQuery kingdeeSynQuery){
        Page<KingdeeSynDto> page = kingdeeSynService.findPage(pageable,kingdeeSynQuery);
        return page;
    }

    @RequestMapping(value = "getQuery")
    public KingdeeSynQuery getQuery(){
        KingdeeSynQuery kingdeeSynQuery = new KingdeeSynQuery();
        kingdeeSynQuery.getExtra().put("extendTypeList", ExtendTypeEnum.values());
        return kingdeeSynQuery;
    }

    @RequestMapping(value = "findOne")
    public KingdeeSyn findOne(String id){
        KingdeeSyn kingdeeSyn = kingdeeSynService.findOne(id);
        return kingdeeSyn;
    }

    //重新做单
    @RequestMapping(value = "syn")
    public RestResponse syn(String id){
        RestResponse restResponse;
        KingdeeSynDto kingdeeSyn = kingdeeSynService.syn(id);
        if (kingdeeSyn.getSuccess()){
            restResponse = new RestResponse("同步成功", null,true);
        }else{
            restResponse = new RestResponse("同步失败", null,false);
        }
        return restResponse;
    }

    @RequestMapping(value = "flush")
    public RestResponse flush(){
        try{
            int count = kingdeeSynService.flush();
            return new RestResponse("同步成功:"+count+"条", null,true);
        }catch (Exception e){
            throw new ServiceException("同步失败: "+ e.getMessage());
        }
    }

    @RequestMapping(value = "noPush")
    public List<KingdeeSyn> noPush(){
        return  kingdeeSynService.findNoPushDown();
    }

    @RequestMapping(value = "findByExtendIdAndExtendType",method = RequestMethod.GET)
    public List<KingdeeSynReturnDto> findByExtendIdAndExtendType(String extendId, String extendType){
        List<KingdeeSyn> kingdeeSynList = kingdeeSynService.findByExtendIdAndExtendType(extendId,extendType);
        return BeanUtil.map(kingdeeSynList,KingdeeSynReturnDto.class);
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(String id){
        kingdeeSynService.logicDelete(id);
        RestResponse restResponse = new RestResponse("删除成功", ResponseCodeEnum.removed.name());
        return restResponse;
    }
}
