package net.myspring.basic.modules.sys.web.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.basic.common.enums.JointTypeEnum;
import net.myspring.basic.common.enums.OfficeTypeEnum;
import net.myspring.basic.common.utils.Const;
import net.myspring.basic.modules.sys.domain.Office;
import net.myspring.basic.modules.sys.domain.OfficeRule;
import net.myspring.basic.modules.sys.dto.OfficeDto;
import net.myspring.basic.modules.sys.service.OfficeRuleService;
import net.myspring.basic.modules.sys.service.OfficeService;
import net.myspring.basic.modules.sys.web.form.OfficeForm;
import net.myspring.basic.modules.sys.web.query.OfficeQuery;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.common.tree.TreeNode;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "sys/office")
public class OfficeController {

    @Autowired
    private OfficeService officeService;
    @Autowired
    private OfficeRuleService officeRuleService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<OfficeDto> list(Pageable pageable, OfficeQuery officeQuery) {
        Page<OfficeDto> page = officeService.findPage(pageable,officeQuery);
        return page;
    }

    @RequestMapping(value = "search")
    public List<OfficeDto> search(String name,String officeType,String parentOfficeId) {
        List<OfficeDto> officeDtos = Lists.newArrayList();
        if(StringUtils.isNotBlank(name)) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("name", name);
            if (StringUtils.isNotBlank(parentOfficeId)) {
                map.put("parentOfficeId",parentOfficeId);
            }
            officeDtos = officeService.findByFilter(map);
        }
         return officeDtos;
    }

    @RequestMapping(value = "getOfficeFilterIds")
    public List<String> getOfficeFilterIds(String accountId){
        List<String> officeIdList=Lists.newArrayList();
        if(StringUtils.isNotBlank(accountId)){
            officeIdList=officeService.getOfficeFilterIds(accountId);
        }
        return officeIdList;
    }

    @RequestMapping(value = "findByOfficeRuleName")
    public List<OfficeDto> findByOfficeRuleName(String officeRuleName){
        List<OfficeDto> officeList=Lists.newArrayList();
        if(StringUtils.isNotBlank(officeRuleName)){
            officeList= BeanUtil.map(officeService.findByOfficeRuleName(officeRuleName),OfficeDto.class);
        }
        return officeList;
    }

    @RequestMapping(value = "save")
    public RestResponse save(OfficeForm officeForm) {
        if(OfficeTypeEnum.BUSINESS.name().equals(officeForm.getType())){
            OfficeRule topOfficeRule = officeRuleService.findMaxLevel();
            OfficeRule officeRule=officeRuleService.findOne(officeForm.getOfficeRuleId());
            Office parent=officeService.findOne(officeForm.getParentId());
            if(parent!=null&&topOfficeRule.getId().equals(officeForm.getOfficeRuleId())){
                return new RestResponse("顶级业务部门不能设置上级", null);
            }else if(parent!=null){
                if(officeRule.getParentId()!=parent.getOfficeRuleId()){
                    return new RestResponse("业务部门上级类型不正确", null);
                }
            }else {
                return new RestResponse("非顶级业务部门必须设置上级", null);
            }
        }
        officeForm.setOfficeIdList(StringUtils.getSplitList(officeForm.getOfficeIdStr(), Const.CHAR_COMMA));
        officeService.save(officeForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }


    @RequestMapping(value = "findForm")
    public OfficeForm findForm(OfficeForm officeForm){
        officeForm=officeService.findForm(officeForm);
        officeForm.setOfficeRuleList(officeService.findTypeList());
        officeForm.setJointTypeList(JointTypeEnum.getList());
        officeForm.setOfficeTypeList(OfficeTypeEnum.getList());
        return officeForm;
    }

    @RequestMapping(value = "getOfficeTree")
    public TreeNode getOfficeTree(String id){
        if(StringUtils.isNotBlank(id)){
            List<String> officeIds=officeService.findBusinessIdById(id);
            TreeNode treeNode=officeService.getOfficeTree(officeIds);
            return treeNode;
        }
        return null;
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(Office office,BindingResult bindingResult) {
        officeService.logicDeleteOne(office);
        return new RestResponse("删除成功",ResponseCodeEnum.removed.name());
    }
}
