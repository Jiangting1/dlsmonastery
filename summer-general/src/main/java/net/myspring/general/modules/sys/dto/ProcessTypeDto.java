package net.myspring.general.modules.sys.dto;


import com.google.common.collect.Lists;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.dto.DataDto;
import net.myspring.general.modules.sys.domain.ProcessType;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.text.StringUtils;

import java.util.List;

/**
 * Created by admin on 2017/4/5.
 */
public class ProcessTypeDto extends DataDto<ProcessType> {
    private String type;
    private String name;
    private String viewPositionIds;
    private String createPositionIds;
    private Boolean auditFileType;
    private Boolean enabled;
    private Boolean locked;
    private List<ProcessFlowDto> processFlowList=Lists.newArrayList();
    private List<String> viewPositionIdList=Lists.newArrayList();
    private List<String> createPositionIdList=Lists.newArrayList();

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public List<String> getViewPositionIdList() {
        if(CollectionUtil.isEmpty(viewPositionIdList)&& StringUtils.isNotBlank(viewPositionIds)){
            this.viewPositionIdList=StringUtils.getSplitList(viewPositionIds, CharConstant.COMMA);
        }
        return viewPositionIdList;
    }

    public void setViewPositionIdList(List<String> viewPositionIdList) {
        this.viewPositionIdList = viewPositionIdList;
    }

    public List<String> getCreatePositionIdList() {
        if(CollectionUtil.isEmpty(createPositionIdList)&& StringUtils.isNotBlank(createPositionIds)){
            this.createPositionIdList=StringUtils.getSplitList(createPositionIds, CharConstant.COMMA);
        }
        return createPositionIdList;
    }

    public void setCreatePositionIdList(List<String> createPositionIdList) {
        this.createPositionIdList = createPositionIdList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getViewPositionIds() {
        return viewPositionIds;
    }

    public void setViewPositionIds(String viewPositionIds) {
        this.viewPositionIds = viewPositionIds;
    }

    public String getCreatePositionIds() {
        return createPositionIds;
    }

    public void setCreatePositionIds(String createPositionIds) {
        this.createPositionIds = createPositionIds;
    }

    public Boolean getAuditFileType() {
        return auditFileType;
    }

    public void setAuditFileType(Boolean auditFileType) {
        this.auditFileType = auditFileType;
    }

    public List<ProcessFlowDto> getProcessFlowList() {
        return processFlowList;
    }

    public void setProcessFlowList(List<ProcessFlowDto> processFlowList) {
        this.processFlowList = processFlowList;
    }
}
