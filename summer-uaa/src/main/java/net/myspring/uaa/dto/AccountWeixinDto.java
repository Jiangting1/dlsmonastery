package net.myspring.uaa.dto;

/**
 * Created by liuj on 2017/4/11.
 */
public class AccountWeixinDto {
    private String accountId;
    private String companyId;
    private String openId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}