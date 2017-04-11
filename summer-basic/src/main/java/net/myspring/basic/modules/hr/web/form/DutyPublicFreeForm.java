package net.myspring.basic.modules.hr.web.form;


import net.myspring.basic.modules.hr.domain.DutyPublicFree;
import net.myspring.mybatis.form.BaseForm;

/**
 * Created by admin on 2017/4/6.
 */

public class DutyPublicFreeForm implements BaseForm<DutyPublicFree> {
    private String employeeId;
    private String status;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}