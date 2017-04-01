package net.myspring.basic.modules.sys.web.controller;

import com.google.common.collect.Maps;
import net.myspring.basic.common.utils.RequestUtils;
import net.myspring.basic.modules.sys.domain.DictMap;
import net.myspring.basic.modules.sys.dto.DictMapDto;
import net.myspring.basic.modules.sys.web.form.DictMapForm;
import net.myspring.common.domain.RestResponse;
import net.myspring.common.domain.SearchEntity;
import net.myspring.util.json.ObjectMapperUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import net.myspring.basic.modules.sys.service.DictMapService;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(value = "sys/dictMap")
public class DictMapController {

    @Autowired
    private DictMapService dictMapService;

    @RequestMapping(value = "getFormProperty")
    public String getFormProperty() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("category", dictMapService.findDistinctCategory());
        return ObjectMapperUtils.writeValueAsString(map);
    }

    @RequestMapping(value = "getListProperty")
    public String getListProperty() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("category", dictMapService.findDistinctCategory());
        return ObjectMapperUtils.writeValueAsString(map);
    }

    @RequestMapping
    public String list(HttpServletRequest request) {
        SearchEntity searchEntity = RequestUtils.getSearchEntity(request);
        Page<DictMapDto> page = dictMapService.findPage(searchEntity.getPageable(), searchEntity.getParams());
        return ObjectMapperUtils.writeValueAsString(page);
    }

    @RequestMapping(value = "findOne")
    public String findOne(String id) {
        DictMapDto dictMapDto=dictMapService.findDto(id);
        return ObjectMapperUtils.writeValueAsString(dictMapDto);
    }

    @RequestMapping(value = "save")
    public String save(DictMapForm dictMapForm) {
        dictMapService.save(dictMapForm);
        return ObjectMapperUtils.writeValueAsString(new RestResponse("保存成功"));
    }

    @RequestMapping(value = "delete")
    public String delete(String id) {
        dictMapService.logicDeleteOne(id);
        RestResponse restResponse = new RestResponse("刪除成功");
        return ObjectMapperUtils.writeValueAsString(restResponse);
    }


}