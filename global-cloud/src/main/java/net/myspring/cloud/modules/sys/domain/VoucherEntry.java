package net.myspring.cloud.modules.sys.domain;


import net.myspring.cloud.common.domain.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 记录凭证信息(不包含核算维度的信息)
 * Created by lihx on 2017/4/5.
 */
@Entity
@Table(name="sys_gl_voucher_entry")
public class VoucherEntry extends IdEntity<VoucherEntry> {
    //摘要
    private String FExplanation;
    //科目编码
    private String FAccountid;
    //科目名称
    private String FAccountName;
    //借方金额
    private BigDecimal FDebit;
    //贷方金额
    private BigDecimal FCredit;
    //凭证
    private String glVoucherId;

    public String getFExplanation() {
        return FExplanation;
    }

    public void setFExplanation(String FExplanation) {
        this.FExplanation = FExplanation;
    }

    public String getFAccountid() {
        return FAccountid;
    }

    public void setFAccountid(String FAccountid) {
        this.FAccountid = FAccountid;
    }

    public String getFAccountName() {
        return FAccountName;
    }

    public void setFAccountName(String FAccountName) {
        this.FAccountName = FAccountName;
    }

    public BigDecimal getFDebit() {
        return FDebit;
    }

    public void setFDebit(BigDecimal FDebit) {
        this.FDebit = FDebit;
    }

    public BigDecimal getFCredit() {
        return FCredit;
    }

    public void setFCredit(BigDecimal FCredit) {
        this.FCredit = FCredit;
    }

    public String getGlVoucherId() {
        return glVoucherId;
    }

    public void setGlVoucherId(String glVoucherId) {
        this.glVoucherId = glVoucherId;
    }
}
