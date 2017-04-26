package net.myspring.basic.modules.sys.dto;

import com.google.common.collect.Lists;
import net.myspring.basic.modules.sys.domain.Backend;
import net.myspring.basic.modules.sys.domain.MenuCategory;

import java.util.List;

/**
 * Created by wangzm on 2017/4/26.
 */
public class BackendModuleMenuDto {

    private String id;
    private String name;
    private String backendId;
    private String code;
    private List<MenuCategoryMenuDto> menuCategoryList= Lists.newArrayList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackendId() {
        return backendId;
    }

    public void setBackendId(String backendId) {
        this.backendId = backendId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MenuCategoryMenuDto> getMenuCategoryList() {
        return menuCategoryList;
    }

    public void setMenuCategoryList(List<MenuCategoryMenuDto> menuCategoryList) {
        this.menuCategoryList = menuCategoryList;
    }
}