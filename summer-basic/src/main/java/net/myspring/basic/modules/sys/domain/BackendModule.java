package net.myspring.basic.modules.sys.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import net.myspring.basic.modules.hr.domain.PositionModule;
import net.myspring.common.domain.CompanyEntity;
import net.myspring.common.domain.DataEntity;
import java.util.List;
import com.google.common.collect.Lists;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.text.StringUtils;

@Entity
@Table(name="sys_backend_module")
public class BackendModule extends DataEntity<BackendModule> {
    private String name;
    private Integer version = 0;
    private Backend backend;
    private String backendId;
    private String code;
    private List<String> positionModuleIdList = Lists.newArrayList();
    private List<String> menuCategoryIdList = Lists.newArrayList();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Backend getBackend() {
        return backend;
    }

    public void setBackend(Backend backend) {
        this.backend = backend;
    }

    public String getBackendId() {
        return backendId;
    }

    public void setBackendId(String backendId) {
        this.backendId = backendId;
    }

    public List<String> getPositionModuleIdList() {
        return positionModuleIdList;
    }

    public void setPositionModuleIdList(List<String> positionModuleIdList) {
        this.positionModuleIdList = positionModuleIdList;
    }

    public List<String> getMenuCategoryIdList() {
        return menuCategoryIdList;
    }

    public void setMenuCategoryIdList(List<String> menuCategoryIdList) {
        this.menuCategoryIdList = menuCategoryIdList;
    }
}
