package net.myspring.future.modules.crm.web.query;

import com.google.common.collect.Lists;
import net.myspring.common.constant.CharConstant;
import net.myspring.future.common.query.BaseQuery;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by wangzm on 2017/6/7.
 */
public class ProductImeSaleReportQuery {
    //区域，促销，型号
    private String sumType ;
    //电子保卡，核销
    private String outType;
    //区域类型
    private String areaType;
    //日期范围
    private String dateRange;
    //乡镇类型
    private String townType;
    //打分型号：是，否
    private boolean scoreType;
    //货品
    private List<String> productTypeIdList= Lists.newArrayList();

    private String officeId;

    private List<String> officeIdList=Lists.newArrayList();

    public List<String> getOfficeIdList() {
        return officeIdList;
    }

    public void setOfficeIdList(List<String> officeIdList) {
        this.officeIdList = officeIdList;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getSumType() {
        return sumType;
    }

    public void setSumType(String sumType) {
        this.sumType = sumType;
    }

    public String getOutType() {
        return outType;
    }

    public void setOutType(String outType) {
        this.outType = outType;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public String getTownType() {
        return townType;
    }

    public void setTownType(String townType) {
        this.townType = townType;
    }

    public boolean getScoreType() {
        return scoreType;
    }

    public boolean isScoreType() {
        return scoreType;
    }

    public void setScoreType(boolean scoreType) {
        this.scoreType = scoreType;
    }

    public List<String> getProductTypeIdList() {
        return productTypeIdList;
    }

    public void setProductTypeIdList(List<String> productTypeIdList) {
        this.productTypeIdList = productTypeIdList;
    }

    public LocalDate getDateStart() {
        if(StringUtils.isNotBlank(dateRange)) {
            return LocalDateUtils.parse(dateRange.split(CharConstant.DATE_RANGE_SPLITTER)[0]);
        } else {
            return null;
        }
    }

    public LocalDate getDateEnd() {
        if(StringUtils.isNotBlank(dateRange)) {
            return LocalDateUtils.parse(dateRange.split(CharConstant.DATE_RANGE_SPLITTER)[1]).plusDays(1);
        } else {
            return null;
        }
    }
}