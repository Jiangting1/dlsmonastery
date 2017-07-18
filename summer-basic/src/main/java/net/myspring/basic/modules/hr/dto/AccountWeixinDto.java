package net.myspring.basic.modules.hr.dto;

import net.myspring.basic.modules.hr.domain.AccountWeixin;
import net.myspring.common.dto.DataDto;
import net.myspring.basic.modules.hr.domain.Account;

/**
 * Created by liuj on 2017/3/19.
 */
public class AccountWeixinDto extends DataDto<AccountWeixin> {
    private String accountId;
    private String openId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
