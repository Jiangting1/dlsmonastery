package net.myspring.cloud.modules.input.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.cloud.common.enums.SalOutStockBillTypeEnum;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.time.LocalDateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by liuj on 2017/5/11.
 */
public class SalOutStockDto {
    //附加-业务系统单据Id
    private String extendId;
    //附加-单据类型
    private String extendType;
    //创建人
    private String creator;
    // 客户编码
    private String customerNumber;
    // 日期
    private LocalDate date;
    // 备注
    private String note;
    //部门编码
    private String departmentNumber;
    //单据类型
    private String billType;
    //单据类型编码
    private String FBillTypeIdNumber;

    private List<SalOutStockFEntityDto> salOutStockFEntityDtoList = Lists.newArrayList();

    public String getExtendId() {
        return extendId;
    }

    public void setExtendId(String extendId) {
        this.extendId = extendId;
    }

    public String getExtendType() {
        if (extendType == null){
            return getBillType()+"-K3";
        }
        return extendType;
    }

    public void setExtendType(String extendType) {
        this.extendType = extendType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(String departmentNumber) {
        this.departmentNumber = departmentNumber;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public List<SalOutStockFEntityDto> getSalOutStockFEntityDtoList() {
        return salOutStockFEntityDtoList;
    }

    public void setSalOutStockFEntityDtoList(List<SalOutStockFEntityDto> entityDtoList) {
        this.salOutStockFEntityDtoList = entityDtoList;
    }

    public String getFBillTypeIdNumber() {
        return FBillTypeIdNumber;
    }

    public void setFBillTypeIdNumber(String FBillTypeIdNumber) {
        this.FBillTypeIdNumber = FBillTypeIdNumber;
    }

    @JsonIgnore
    public String getJson() {
        Map<String, Object> root = Maps.newLinkedHashMap();
        root.put("Creator", getCreator());
        root.put("NeedUpDateFields", Lists.newArrayList());
        Map<String, Object> model = Maps.newLinkedHashMap();
        model.put("FID", 0);
        model.put("FDate", LocalDateUtils.format(getDate(),"yyyy-M-d"));
        model.put("FBillTypeID", CollectionUtil.getMap("FNumber", getFBillTypeIdNumber()));
        model.put("FDeliveryDeptID", CollectionUtil.getMap("FNumber", getDepartmentNumber()));
        model.put("FSaleOrgId", CollectionUtil.getMap("FNumber", 100));
        model.put("FStockOrgId", CollectionUtil.getMap("FNumber", 100));
        model.put("FOwnerIdHead", CollectionUtil.getMap("FNumber", 100));
        model.put("FSettleCurrID", CollectionUtil.getMap("FNumber", "PRE001"));
        //客户
        model.put("FCustomerID", CollectionUtil.getMap("FNumber", getCustomerNumber()));
        model.put("FNote", getNote());
        List<Object> entity = Lists.newArrayList();
        for (SalOutStockFEntityDto entityDto: getSalOutStockFEntityDtoList()) {
            if (entityDto.getQty() != null && entityDto.getQty() > 0) {
                Map<String, Object> detail = Maps.newLinkedHashMap();
                //仓库
                detail.put("FStockID", CollectionUtil.getMap("FNumber", entityDto.getStockNumber()));
                //物料编码
                detail.put("FMaterialId", CollectionUtil.getMap("FNumber", entityDto.getMaterialNumber()));
                //库存状态--可用
                detail.put("FStockStatusID", CollectionUtil.getMap("FNumber", "KCZT01_SYS"));
                //库存单位--Pcs
                detail.put("FUnitID", CollectionUtil.getMap("FNumber", "Pcs"));
                //销售部门
                detail.put("FSaleDeptID", CollectionUtil.getMap("FNumber", getDepartmentNumber()));
                detail.put("FRealQty", entityDto.getQty());
                detail.put("FBaseUnitQty", entityDto.getQty());
                detail.put("FPriceUnitQty", entityDto.getQty());
                detail.put("FTaxNetPrice", entityDto.getPrice());
                detail.put("FSALBASEQTY", entityDto.getQty());
                detail.put("FSALUNITQTY", entityDto.getQty());
                detail.put("FPRICEBASEQTY", entityDto.getQty());
                // 是否赠品
                detail.put("FIsFree", entityDto.getPrice().compareTo(BigDecimal.ZERO) == 0 ? 1 : 0);
                detail.put("FPrice", entityDto.getPrice());
                detail.put("FTaxPrice", entityDto.getPrice());
                detail.put("FAmount", new BigDecimal(entityDto.getQty()).multiply(entityDto.getPrice()));
                detail.put("FBefDisAllAmt", new BigDecimal(entityDto.getQty()).multiply(entityDto.getPrice()));
                detail.put("FEntrynote", entityDto.getEntryNote());
                entity.add(detail);
            }
        }
        model.put("FEntity", entity);
        root.put("Model", model);
        String result = ObjectMapperUtils.writeValueAsString(root);
        //System.out.println(result);
        return result;
    }
}
