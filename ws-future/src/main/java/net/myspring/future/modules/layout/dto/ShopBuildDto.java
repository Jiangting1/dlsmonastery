package net.myspring.future.modules.layout.dto;

import net.myspring.common.dto.DataDto;
import net.myspring.future.common.constant.FormatterConstant;
import net.myspring.future.common.enums.CompanyNameEnum;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.layout.domain.ShopBuild;
import net.myspring.util.cahe.annotation.CacheInput;
import net.myspring.util.text.IdUtils;
import net.myspring.util.text.StringUtils;

/**
 * Created by zhangyf on 2017/5/6.
 */
public class ShopBuildDto extends DataDto<ShopBuild>{
    private String shopId;
    private String shopName;
    private String address;
    private String officeId;
    @CacheInput(inputKey = "offices", inputInstance = "officeId", outputInstance = "name")
    private String officeName;
    private String areaId;
    @CacheInput(inputKey = "offices", inputInstance = "areaId", outputInstance = "name")
    private String areaName;
    private String areaType;
    private String shopType;

    private String applyAccountId;
    @CacheInput(inputKey = "accounts", inputInstance = "applyAccountId", outputInstance = "loginName")
    private String applyAccountName;
    @CacheInput(inputKey = "accounts", inputInstance = "applyAccountId", outputInstance = "employeeId")
    private String employeeId;
    @CacheInput(inputKey = "employees", inputInstance = "employeeId", outputInstance = "mobilePhone")
    private String applyAccountPhone;

    private String fixtureType;
    private String buildType;
    private String content;
    private String oldContents;
    private String newContents;
    private String scenePhoto;
    private String confirmPhoto;
    private String shopAgreement;
    private String investInCause;

    private String processStatus;
    private String processInstanceId;
    private String processPositionId;
    private Boolean locked;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getShopAgreement() {
        return shopAgreement;
    }

    public void setShopAgreement(String shopAgreement) {
        this.shopAgreement = shopAgreement;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getInvestInCause() {
        return investInCause;
    }

    public void setInvestInCause(String investInCause) {
        this.investInCause = investInCause;
    }

    public String getConfirmPhoto() {
        return confirmPhoto;
    }

    public void setConfirmPhoto(String confirmPhoto) {
        this.confirmPhoto = confirmPhoto;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getShopType() {
        return shopType;
    }

    public void setShopType(String shopType) {
        this.shopType = shopType;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApplyAccountId() {
        return applyAccountId;
    }

    public void setApplyAccountId(String applyAccountId) {
        this.applyAccountId = applyAccountId;
    }

    public String getApplyAccountName() {
        return applyAccountName;
    }

    public void setApplyAccountName(String applyAccountName) {
        this.applyAccountName = applyAccountName;
    }

    public String getApplyAccountPhone() {
        return applyAccountPhone;
    }

    public void setApplyAccountPhone(String applyAccountPhone) {
        this.applyAccountPhone = applyAccountPhone;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getFixtureType() {
        return fixtureType;
    }

    public void setFixtureType(String fixtureType) {
        this.fixtureType = fixtureType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOldContents() {
        return oldContents;
    }

    public void setOldContents(String oldContents) {
        this.oldContents = oldContents;
    }

    public String getNewContents() {
        return newContents;
    }

    public void setNewContents(String newContents) {
        this.newContents = newContents;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getScenePhoto() {
        return scenePhoto;
    }

    public void setScenePhoto(String scenePhoto) {
        this.scenePhoto = scenePhoto;
    }

    public String getProcessPositionId() {
        return processPositionId;
    }

    public void setProcessPositionId(String processPositionId) {
        this.processPositionId = processPositionId;
    }

    public String getFormatId(){
        if(getId()!=null){
            return IdUtils.getFormatId(getId(), FormatterConstant.SHOP_BUILD);
        }else {
            return null;
        }
    }

    public String getAccountNameAndAccountPhone(){
        String accountName = getApplyAccountName()!=null?getApplyAccountName():"";
        String accountPhone = getApplyAccountPhone()!=null?getApplyAccountPhone():"";
        return accountName+accountPhone;
    }

    public Boolean getIsAuditable(){
        if(RequestUtils.getPositionId().equals(getProcessPositionId())|| RequestUtils.getAccountId().equalsIgnoreCase("1")){
            return true;
        }else {
            return false;
        }
    }

    public Boolean getIsEditable(){
        if (RequestUtils.getAccountId().equals(getCreatedBy())|| RequestUtils.getAccountId().equalsIgnoreCase("1")){
            return true;
        }else {
            return false;
        }
    }

    public String getExportAreaName(){
        if(StringUtils.isNotBlank(this.areaName)){
            if(this.areaName.contains("办事处")){
                return this.areaName.replaceAll("办事处", RequestUtils.getCompanyName());
            }else{
                return this.areaName;
            }
        }else{
            return null;
        }
    }
}
