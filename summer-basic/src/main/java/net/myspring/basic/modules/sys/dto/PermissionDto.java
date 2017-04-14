package net.myspring.basic.modules.sys.dto;

import com.google.common.collect.Lists;
import net.myspring.basic.common.dto.DataDto;
import net.myspring.basic.modules.hr.domain.Position;
import net.myspring.basic.modules.sys.domain.Menu;
import net.myspring.basic.modules.sys.domain.Permission;
import net.myspring.util.cahe.annotation.CacheInput;

import java.util.List;

/**
 * Created by admin on 2017/4/5.
 */
public class PermissionDto extends DataDto<Permission> {

    private String name;
    private String permission;
    private String menuId;
    @CacheInput(inputKey = "menus",inputInstance = "menuId",outputInstance = "name")
    private String menuName;

    private String locked;
    private String remarks;

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    @Override
    public String getRemarks() {
        return remarks;
    }

    @Override
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
}
