package net.myspring.basic.modules.hr.dto;

import com.google.common.collect.Lists;
import net.myspring.basic.modules.hr.domain.Account;
import net.myspring.util.text.IdUtils;
import net.myspring.util.text.StringUtils;

import java.util.List;

/**
 * Created by admin on 2016/12/23.
 */
public class DutyDto {
    private String id;
    private String prefix;
    private String formatId;
    private String accountName;
    private String dutyType;
    private String dutyDate;
    private String remarks;
    private String dateType;
    private Double hour;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFormatId() {
        if(StringUtils.isBlank(formatId)&&StringUtils.isNotBlank(id)){
            this.formatId= IdUtils.getFormatId(id,prefix);
        }
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getDutyType() {
        return dutyType;
    }

    public void setDutyType(String dutyType) {
        this.dutyType = dutyType;
    }

    public String getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(String dutyDate) {
        this.dutyDate = dutyDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public Double getHour() {
        return hour;
    }

    public void setHour(Double hour) {
        this.hour = hour;
    }
}
