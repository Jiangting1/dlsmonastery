package net.myspring.cloud.modules.input.dto;

import net.myspring.cloud.modules.sys.domain.KingdeeBook;

/**
 * Created by liuj on 2016-06-20.
 */
public class KingdeeSynDto {
    private Boolean success;
    private KingdeeBook kingdeeBook;
    private String formId;
    private String content;
    private String billNo;
    private String result;
    private Boolean autoAudit = true;

    public KingdeeSynDto(String formId, String content,KingdeeBook kingdeeBook) {
        this.formId = formId;
        this.content = content;
        this.kingdeeBook = kingdeeBook;
    }

    public KingdeeSynDto(Boolean success, String result) {
        this.success = success;
        this.result = result;
    }

    public KingdeeSynDto() {
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public KingdeeBook getKingdeeBook() {
        return kingdeeBook;
    }

    public void setKingdeeBook(KingdeeBook kingdeeBook) {
        this.kingdeeBook = kingdeeBook;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Boolean getAutoAudit() {
        return autoAudit;
    }

    public void setAutoAudit(Boolean autoAudit) {
        this.autoAudit = autoAudit;
    }
}
