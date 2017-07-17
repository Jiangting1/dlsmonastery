package net.myspring.basic.modules.sys.domain;


import net.myspring.basic.common.domain.TreeEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name="sys_office")
public class Office extends TreeEntity<Office> {
    private String name;
    private Integer version = 0;
    private BigDecimal point;
    private Integer level;
    private String jointType;
    private BigDecimal taskPoint;
    private String sort;
    private String officeRuleId;
    private String type;
    private String areaId;
    private String jointLevel;
    private String agentCode;
    //是否拥有所有数据权限
    private Boolean allDataScope;

    public String getJointLevel() {
        return jointLevel;
    }

    public void setJointLevel(String jointLevel) {
        this.jointLevel = jointLevel;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BigDecimal getPoint() {
        return point;
    }

    public void setPoint(BigDecimal point) {
        this.point = point;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getJointType() {
        return jointType;
    }

    public void setJointType(String jointType) {
        this.jointType = jointType;
    }
    public BigDecimal getTaskPoint() {
        return taskPoint;
    }

    public void setTaskPoint(BigDecimal taskPoint) {
        this.taskPoint = taskPoint;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOfficeRuleId() {
        return officeRuleId;
    }

    public void setOfficeRuleId(String officeRuleId) {
        this.officeRuleId = officeRuleId;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    public Boolean getAllDataScope() {
        return allDataScope;
    }

    public void setAllDataScope(Boolean allDataScope) {
        this.allDataScope = allDataScope;
    }
}
