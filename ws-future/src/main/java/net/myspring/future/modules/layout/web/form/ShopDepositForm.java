package net.myspring.future.modules.layout.web.form;

import net.myspring.common.form.BaseForm;
import net.myspring.future.modules.layout.domain.ShopDeposit;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ShopDepositForm extends BaseForm<ShopDeposit> {

    private BigDecimal imageAmount;
    private BigDecimal marketAmount;
    private BigDecimal demoPhoneAmount ;
    private LocalDate billDate;
    private String bankId;
    private String departMent;
    private String outBillType;
    private String shopId;

    public BigDecimal getDemoPhoneAmount() {
        return demoPhoneAmount;
    }

    public void setDemoPhoneAmount(BigDecimal demoPhoneAmount) {
        this.demoPhoneAmount = demoPhoneAmount;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public BigDecimal getMarketAmount() {
        return marketAmount;
    }

    public void setMarketAmount(BigDecimal marketAmount) {
        this.marketAmount = marketAmount;
    }

    public BigDecimal getImageAmount() {
        return imageAmount;
    }

    public void setImageAmount(BigDecimal imageAmount) {
        this.imageAmount = imageAmount;
    }

    public String getDepartMent() {
        return departMent;
    }

    public void setDepartMent(String departMent) {
        this.departMent = departMent;
    }

    public String getOutBillType() {
        return outBillType;
    }

    public void setOutBillType(String outBillType) {
        this.outBillType = outBillType;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public boolean isImageAmountValid() {
        if (imageAmount != null && ! imageAmount.equals(BigDecimal.ZERO) ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMarketAmountValid() {
        if (marketAmount != null && ! marketAmount.equals(BigDecimal.ZERO)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDemoPhoneAmountValid() {
        if (demoPhoneAmount != null && ! demoPhoneAmount.equals(BigDecimal.ZERO)) {
            return true;
        } else {
            return false;
        }
    }


}
