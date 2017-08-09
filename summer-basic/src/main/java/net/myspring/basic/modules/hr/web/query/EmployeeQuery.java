package net.myspring.basic.modules.hr.web.query;

import com.google.common.collect.Lists;
import net.myspring.basic.common.query.BaseQuery;
import net.myspring.common.constant.CharConstant;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by lihx on 2017/4/7.
 */
public class EmployeeQuery extends BaseQuery {
    private List<String> officeIds=Lists.newArrayList();
    private String name;
    private String status;
    private String mobilePhone;
    private String entryDate;
    private String regularDate;
    private String leaveDate;
    private String positionId;
    private String leaderName;
    private String officeId;
    private String leaveDateMonth;
    private String areaId;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getLeaveDateMonth() {
        return leaveDateMonth;
    }

    public void setLeaveDateMonth(String leaveDateMonth) {
        this.leaveDateMonth = leaveDateMonth;
    }

    public LocalDate getLeaveDateMonthStart() {
        if(StringUtils.isNotBlank(leaveDateMonth)) {
            return LocalDateUtils.getFirstDayOfThisMonth(LocalDateUtils.parse(leaveDateMonth));
        } else {
            return null;
        }
    }

    public LocalDate getLeaveDateMonthEnd() {
        if(StringUtils.isNotBlank(leaveDateMonth)) {
            return LocalDateUtils.getLastDayOfThisMonth(LocalDateUtils.parse(leaveDateMonth));
        } else {
            return null;
        }
    }

    public List<String> getOfficeIds() {
        return officeIds;
    }

    public void setOfficeIds(List<String> officeIds) {
        this.officeIds = officeIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public LocalDate getEntryDateStart() {
        if(StringUtils.isNotBlank(entryDate)) {
            return LocalDateUtils.parse(entryDate.split(CharConstant.DATE_RANGE_SPLITTER)[0]);
        } else {
            return null;
        }
    }

    public LocalDate getEntryDateEnd() {
        if(StringUtils.isNotBlank(entryDate)) {
            return LocalDateUtils.parse(entryDate.split(CharConstant.DATE_RANGE_SPLITTER)[1]).plusDays(1);
        } else {
            return null;
        }
    }

    public void setLeaveDate(String leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getLeaveDate() {
        return leaveDate;
    }

    public LocalDate getLeaveDateStart() {
        if(StringUtils.isNotBlank(leaveDate)) {
            return LocalDateUtils.parse(leaveDate.split(CharConstant.DATE_RANGE_SPLITTER)[0]);
        } else {
            return null;
        }
    }

    public LocalDate getLeaveDateEnd() {
        if(StringUtils.isNotBlank(leaveDate)) {
            return LocalDateUtils.parse(leaveDate.split(CharConstant.DATE_RANGE_SPLITTER)[1]).plusDays(1);
        } else {
            return null;
        }
    }

    public void setRegularDate(String regularDate) {
        this.regularDate = regularDate;
    }

    public String getRegularDate() {
        return regularDate;
    }

    public LocalDate getRegularDateStart() {
        if(StringUtils.isNotBlank(regularDate)) {
            return LocalDateUtils.parse(regularDate.split(CharConstant.DATE_RANGE_SPLITTER)[0]);
        } else {
            return null;
        }
    }

    public LocalDate getRegularDateEnd() {
        if(StringUtils.isNotBlank(regularDate)) {
            return LocalDateUtils.parse(regularDate.split(CharConstant.DATE_RANGE_SPLITTER)[1]).plusDays(1);
        }else {
            return null;
        }
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }
}
