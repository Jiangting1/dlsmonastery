package net.myspring.basic.common.config;

import net.myspring.basic.common.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;

public class AuditorContextHolder {
    private String accountId;

    private static ThreadLocal<AuditorContextHolder> threadLocal = new ThreadLocal<AuditorContextHolder>();

    public static AuditorContextHolder get() {
        if (threadLocal.get() == null) {
            AuditorContextHolder auditorContextHolder = new AuditorContextHolder();
            threadLocal.set(auditorContextHolder);
        }
        return threadLocal.get();
    }

    public void remove() {
        this.accountId=null;
        threadLocal.remove();
    }

    public String getAccountId() {
        if(StringUtils.isNotBlank(RequestUtils.getAccountId())) {
            accountId = RequestUtils.getAccountId();
        }else if(StringUtils.isBlank(accountId)){
            return "1";
        }
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
