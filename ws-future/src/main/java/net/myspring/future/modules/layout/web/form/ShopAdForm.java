package net.myspring.future.modules.layout.web.form;

import net.myspring.common.form.BaseForm;
import net.myspring.future.modules.layout.domain.ShopAd;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by zhangyf on 2017/5/10.
 */
public class ShopAdForm extends BaseForm<ShopAd> {
    private String shopId;
    private String shopAdTypeId;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal thickness;
    private BigDecimal area;
    private Integer qty;
    private String content;
    private Boolean specialArea;
    private String attachment;
    private BigDecimal price;
    private String processInstanceId;


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

    public String getShopAdTypeId() {
        return shopAdTypeId;
    }

    public void setShopAdTypeId(String shopAdTypeId) {
        this.shopAdTypeId = shopAdTypeId;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getSpecialArea() {
        return specialArea;
    }

    public void setSpecialArea(Boolean specialArea) {
        this.specialArea = specialArea;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getThickness() {
        return thickness;
    }

    public void setThickness(BigDecimal thickness) {
        this.thickness = thickness;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getArea() {
        if(this.length !=null&&this.width!=null&&this.qty!=null&&this.area==null){
            return length.multiply(width).multiply(BigDecimal.valueOf(qty));
        }else{
            return area;
        }
    }
}
