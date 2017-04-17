package net.myspring.basic.modules.hr.web.query;

import com.google.common.collect.Lists;
import net.myspring.basic.modules.hr.domain.Office;
import net.myspring.util.time.LocalDateTimeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by liuj on 2017/3/19.
 */
public class AccountChangeQuery {

    private List<String> officeIds= Lists.newArrayList();
    private LocalDateTime createdDateStart;
    private LocalDateTime createdDateEnd;
    private String type;
    private String createdByName;
    private String officeId;
    private List<String> typeList=Lists.newArrayList();
    private List<Office> areaList=Lists.newArrayList();

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    public List<Office> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<Office> areaList) {
        this.areaList = areaList;
    }

    public List<String> getOfficeIds() {
        return officeIds;
    }

    public void setOfficeIds(List<String> officeIds) {
        this.officeIds = officeIds;
    }

    public LocalDateTime getCreatedDateStart() {
        return createdDateStart;
    }

    public void setCreatedDateStart(LocalDateTime createdDateStart) {
        this.createdDateStart = createdDateStart;
    }

    public LocalDateTime getCreatedDateEnd() {
        return createdDateEnd;
    }

    public void setCreatedDateEnd(LocalDateTime createdDateEnd) {
        this.createdDateEnd = createdDateEnd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }
}
