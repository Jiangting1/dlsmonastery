package net.myspring.future.modules.layout.dto;

import net.myspring.common.dto.DataDto;
import net.myspring.future.common.constant.FormatterConstant;
import net.myspring.future.common.enums.TotalPriceTypeEnum;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.layout.domain.ShopAd;
import net.myspring.util.cahe.annotation.CacheInput;
import net.myspring.util.text.IdUtils;
import net.myspring.util.text.StringUtils;

import java.math.BigDecimal;

/**
 * Created by zhangyf on 2017/5/10.
 */
public class ShopAdDto extends DataDto<ShopAd>{

    private String shopId;
    private String shopName;
    private String officeId;
    @CacheInput(inputKey = "offices", inputInstance = "officeId", outputInstance = "name")
    private String officeName;
    private String areaId;
    @CacheInput(inputKey = "offices", inputInstance = "areaId", outputInstance = "name")
    private String areaName;
    private Boolean specialArea;
    private String shopAdTypeId;
    private String shopAdTypeName;
    private BigDecimal shopAdTypePrice;
    private String totalPriceType;
    private BigDecimal length;
    private BigDecimal width;
    private Integer qty;
    private String attachment;
    private BigDecimal price;
    private String content;
    private String processStatus;
    private String processPositionId;
    private String processInstanceId;
    private Boolean locked;


    public String getTotalPriceType() {
        return totalPriceType;
    }

    public void setTotalPriceType(String totalPriceType) {
        this.totalPriceType = totalPriceType;
    }

    public BigDecimal getShopAdTypePrice() {
        return shopAdTypePrice;
    }

    public void setShopAdTypePrice(BigDecimal shopAdTypePrice) {
        this.shopAdTypePrice = shopAdTypePrice;
    }

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

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
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

    public Boolean getSpecialArea() {
        return specialArea;
    }

    public void setSpecialArea(Boolean specialArea) {
        this.specialArea = specialArea;
    }

    public String getShopAdTypeId() {
        return shopAdTypeId;
    }

    public void setShopAdTypeId(String shopAdTypeId) {
        this.shopAdTypeId = shopAdTypeId;
    }

    public String getShopAdTypeName() {
        return shopAdTypeName;
    }

    public void setShopAdTypeName(String shopAdTypeName) {
        this.shopAdTypeName = shopAdTypeName;
    }

    public BigDecimal getPrice() {
        BigDecimal price=BigDecimal.ZERO;
        if(TotalPriceTypeEnum.按数量.toString().equals(getTotalPriceType())){
            price=shopAdTypePrice.multiply(new BigDecimal(getQty()));
        }
        if(TotalPriceTypeEnum.按面积.toString().equals(getTotalPriceType())){
            price=getLength().multiply(getWidth()).multiply(shopAdTypePrice).multiply(new BigDecimal(getQty()));
        }
        return price.setScale(0,BigDecimal.ROUND_HALF_UP);
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getProcessPositionId() {
        return processPositionId;
    }

    public void setProcessPositionId(String processPositionId) {
        this.processPositionId = processPositionId;
    }

    public BigDecimal getLength() {
        return length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public Integer getQty() {
        return qty;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getLengthWidthQty() {
        if(this.length !=null&&this.width!=null&&this.qty!=null){
            return String.valueOf(length)+"*"+String.valueOf(width)+"*"+String.valueOf(qty);
        }else{
            return null;
        }
    }

    public BigDecimal getArea() {
        if(this.length !=null&&this.width!=null&&this.qty!=null){
            return length.multiply(width).multiply(BigDecimal.valueOf(qty));
        }else{
            return null;
        }
    }

    public String getFormatId(){
        if(getId()!=null){
            return IdUtils.getFormatId(getId(), FormatterConstant.SHOP_AD);
        }else {
            return null;
        }
    }

    public Boolean getIsAuditable(){
        if(RequestUtils.getPositionIdList().contains(getProcessPositionId())|| RequestUtils.getAdmin()){
            return true;
        }else {
            return false;
        }
    }

    public Boolean getIsEditable(){
        if (RequestUtils.getAccountId().equals(getCreatedBy())|| RequestUtils.getAdmin()){
            return true;
        }else {
            return false;
        }
    }

    public String getSpecialAreaToString(){
        if(this.specialArea!=null){
            if(this.specialArea){
                return "是";
            }else{
                return "否";
            }
        }else{
            return "";
        }
    }

    public String getAttachmentToExport(){
        if(StringUtils.isNotBlank(this.attachment)){
            return "是";
        }else{
            return "否";
        }
    }
}
