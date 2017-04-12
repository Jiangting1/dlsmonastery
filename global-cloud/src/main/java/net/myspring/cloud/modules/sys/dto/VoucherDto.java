package net.myspring.cloud.modules.sys.dto;

import com.google.common.collect.Lists;
import net.myspring.cloud.common.dto.DataDto;
import net.myspring.cloud.modules.sys.domain.Voucher;
import net.myspring.cloud.modules.sys.domain.VoucherEntry;

import java.util.List;

/**
 * Created by lihx on 2017/4/5.
 */
public class VoucherDto extends DataDto<Voucher> {
    private String createdName;
    private String companyId;
    private String status;
    //明细
    private List<VoucherEntryDto> voucherEntryDtoList = Lists.newArrayList();
    private List<String> voucherEntryIdList = Lists.newArrayList();

    private Boolean deletable = false;
    private Boolean editable = false;
    private List<String> actionList;

    public String getCreatedName() {
        return createdName;
    }

    public void setCreatedName(String createdName) {
        this.createdName = createdName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<VoucherEntryDto> getVoucherEntryDtoList() {
        return voucherEntryDtoList;
    }

    public void setVoucherEntryDtoList(List<VoucherEntryDto> voucherEntryDtoList) {
        this.voucherEntryDtoList = voucherEntryDtoList;
    }

    public List<String> getVoucherEntryIdList() {
        return voucherEntryIdList;
    }

    public void setVoucherEntryIdList(List<String> voucherEntryIdList) {
        this.voucherEntryIdList = voucherEntryIdList;
    }

    public Boolean getDeletable() {
        return deletable;
    }

    public void setDeletable(Boolean deletable) {
        this.deletable = deletable;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public List<String> getActionList() {
        return actionList;
    }

    public void setActionList(List<String> actionList) {
        this.actionList = actionList;
    }
}