package net.myspring.basic.modules.hr.web.controller;

import com.google.common.collect.Lists;
import net.myspring.basic.common.utils.Const;
import net.myspring.basic.modules.hr.dto.PositionDto;
import net.myspring.basic.modules.hr.service.JobService;
import net.myspring.basic.modules.hr.web.form.PositionForm;
import net.myspring.basic.modules.hr.web.query.PositionQuery;
import net.myspring.basic.modules.sys.domain.Permission;
import net.myspring.basic.modules.sys.service.BackendModuleService;
import net.myspring.basic.modules.sys.service.PermissionService;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.common.tree.TreeNode;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.text.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import net.myspring.basic.modules.hr.service.PositionService;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "hr/position")
public class PositionController {

    @Autowired
    private PositionService positionService;
    @Autowired
    private JobService jobService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private BackendModuleService backendModuleService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<PositionDto> findPage(Pageable pageable, PositionQuery positionQuery) {
        Page<PositionDto> page = positionService.findPage(pageable, positionQuery);
        return page;
    }

    @RequestMapping(value = "getQuery")
    public PositionQuery getQuery(PositionQuery positionQuery) {
        positionQuery.setJobList(jobService.findAll());
        return positionQuery;
    }

    @RequestMapping(value = "findForm")
    public PositionForm findForm(PositionForm positionForm) {
        positionForm= positionService.findForm(positionForm);
        List<String> backendModuleIdList = positionForm.isCreate()? Lists.newArrayList() : backendModuleService.findBackendModuleIdByPosition(positionForm.getId());
        positionForm.setPermissionTree(permissionService.findBackendTree(backendModuleIdList));
        positionForm.setJobList( jobService.findAll());
        return positionForm;
    }

    @RequestMapping(value = "getTreeNode")
    public TreeNode getTreeNode(String id) {
        if(StringUtils.isNotBlank(id)){
            List<Permission> permissionList=permissionService.findByPositionId(id);
            List<String> permissionIIds = CollectionUtil.extractToList(permissionList, "id");
            TreeNode treeNode=permissionService.findPermissionTree(permissionIIds);
            return treeNode;
        }
       return null;
    }

    @RequestMapping(value = "save")
    public RestResponse save(PositionForm positionForm, String permissionIdStr) {
        positionForm.setPermissionIdList(StringUtils.getSplitList(permissionIdStr, Const.CHAR_COMMA));
        positionService.save(positionForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "saveAuthorityList")
    public RestResponse saveAuthorityList(PositionForm positionForm, String permissionIdStr) {
        positionForm.setPermissionIdList(StringUtils.getSplitList(permissionIdStr, Const.CHAR_COMMA));
        positionService.savePositionAndModule(positionForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(String id) {
        positionService.delete(id);
        RestResponse restResponse = new RestResponse("删除成功", ResponseCodeEnum.removed.name());
        return restResponse;
    }


    @RequestMapping(value = "search")
    public List<PositionDto> search(String name) {
        List<PositionDto> positionDtoList =Lists.newArrayList();
        if(StringUtils.isNotBlank(name)){
            positionDtoList =positionService.findByNameLike(name);
        }
        return positionDtoList;
    }

}
