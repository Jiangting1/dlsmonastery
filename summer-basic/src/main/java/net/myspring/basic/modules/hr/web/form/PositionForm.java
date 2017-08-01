package net.myspring.basic.modules.hr.web.form;


import net.myspring.basic.modules.hr.domain.Position;
import net.myspring.common.form.BaseForm;
import net.myspring.util.cahe.annotation.CacheInput;

/**
 * Created by admin on 2017/4/5.
 */

public class PositionForm extends BaseForm<Position> {

    @CacheInput(inputKey = "roles",inputInstance = "roleId",outputInstance = "name")
    protected String roleName;
    private String name;
    private String permission;
    private String remarks;
    private String roleId;
    private Integer sort;

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    @Override
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
