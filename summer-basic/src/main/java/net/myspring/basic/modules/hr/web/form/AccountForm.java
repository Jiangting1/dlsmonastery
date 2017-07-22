package net.myspring.basic.modules.hr.web.form;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.basic.modules.hr.domain.Account;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.form.BaseForm;
import net.myspring.basic.modules.hr.dto.PositionDto;
import net.myspring.util.cahe.annotation.CacheInput;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.text.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by liuj on 2017/3/19.
 */

public class AccountForm extends BaseForm<Account> {
    private String password;
    private String type;
    private String employeeId;
    @CacheInput(inputKey = "employees",inputInstance = "employeeId",outputInstance = "name")
    private String employeeName;
    private String loginName;
    private String officeId;
    @CacheInput(inputKey = "offices",inputInstance = "officeId",outputInstance = "name")
    private String officeName;
    private String leaderId;
    @CacheInput(inputKey = "accounts",inputInstance = "leaderId",outputInstance = "loginName")
    private String leaderName;
    private Boolean viewReport;
    private String outId;
    private String outPassword;
    private String remarks;
    private String positionId;
    private List<String> permissionIdList=Lists.newArrayList();
    private String roleIds;
    private List<String> roleIdList=Lists.newArrayList();
    private String officeIds;
    private List<String> officeIdList=Lists.newArrayList();
    @CacheInput(inputKey = "offices",inputInstance = "officeIdList",outputInstance = "name")
    private List<String> officeListName=Lists.newArrayList();

    public String getRoleIds() {
        if(StringUtils.isNotBlank(roleIds)&&CollectionUtil.isNotEmpty(roleIdList)){
            this.roleIds=StringUtils.join(roleIdList,CharConstant.COMMA);
        }
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public List<String> getRoleIdList() {
        if(CollectionUtil.isEmpty(roleIdList)&& StringUtils.isNotBlank(roleIds)){
            this.roleIdList=StringUtils.getSplitList(roleIds, CharConstant.COMMA);
        }
        return roleIdList;
    }

    public void setRoleIdList(List<String> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public String getOfficeIds() {
        if(StringUtils.isNotBlank(officeIds)&&CollectionUtil.isNotEmpty(officeIdList)){
            this.officeIds=StringUtils.join(officeIdList,CharConstant.COMMA);
        }
        return officeIds;
    }

    public void setOfficeIds(String officeIds) {
        this.officeIds = officeIds;
    }

    public List<String> getOfficeIdList() {
        if(CollectionUtil.isEmpty(officeIdList)&&StringUtils.isNotBlank(officeIds)){
            this.officeIdList=StringUtils.getSplitList(officeIds, CharConstant.COMMA);
        }
        return officeIdList;
    }

    public void setOfficeIdList(List<String> officeIdList) {
        this.officeIdList = officeIdList;
    }


    public List<String> getPermissionIdList() {
        return permissionIdList;
    }

    public void setPermissionIdList(List<String> permissionIdList) {
        this.permissionIdList = permissionIdList;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getOfficeListName() {
        return officeListName;
    }

    public void setOfficeListName(List<String> officeListName) {
        this.officeListName = officeListName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public Boolean getViewReport() {
        return viewReport;
    }

    public void setViewReport(Boolean viewReport) {
        this.viewReport = viewReport;
    }

    public String getOutId() {
        return outId;
    }

    public void setOutId(String outId) {
        this.outId = outId;
    }

    public String getOutPassword() {
        return outPassword;
    }

    public void setOutPassword(String outPassword) {
        this.outPassword = outPassword;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    @Override
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

}
