package net.myspring.future.modules.crm.dto;

import net.myspring.common.dto.DataDto;
import net.myspring.future.modules.crm.domain.ReportScore;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by haos on 2017/5/12.
 */
public class ReportScoreDto extends DataDto<ReportScore> {


    private LocalDate scoreDate;
    private BigDecimal companyScore;
    private BigDecimal companyMonthScore;
    private BigDecimal score;
    private BigDecimal monthScore;
    private Integer cardQty;
    private Integer monthCardQty;
    private Integer rank;
    private Integer saleQty;
    private Integer monthSaleQty;
    private Integer recentMonthSaleQty;
    private BigDecimal saleMoney;
    private BigDecimal monthSaleMoney;
    private String productTypeNameStr;

    public String getProductTypeNameStr() {
        return productTypeNameStr;
    }

    public void setProductTypeNameStr(String productTypeNameStr) {
        this.productTypeNameStr = productTypeNameStr;
    }

    public LocalDate getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(LocalDate scoreDate) {
        this.scoreDate = scoreDate;
    }

    public BigDecimal getCompanyScore() {
        return companyScore;
    }

    public void setCompanyScore(BigDecimal companyScore) {
        this.companyScore = companyScore;
    }

    public BigDecimal getCompanyMonthScore() {
        return companyMonthScore;
    }

    public void setCompanyMonthScore(BigDecimal companyMonthScore) {
        this.companyMonthScore = companyMonthScore;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getMonthScore() {
        return monthScore;
    }

    public void setMonthScore(BigDecimal monthScore) {
        this.monthScore = monthScore;
    }

    public Integer getCardQty() {
        return cardQty;
    }

    public void setCardQty(Integer cardQty) {
        this.cardQty = cardQty;
    }

    public Integer getMonthCardQty() {
        return monthCardQty;
    }

    public void setMonthCardQty(Integer monthCardQty) {
        this.monthCardQty = monthCardQty;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getSaleQty() {
        return saleQty;
    }

    public void setSaleQty(Integer saleQty) {
        this.saleQty = saleQty;
    }

    public Integer getMonthSaleQty() {
        return monthSaleQty;
    }

    public void setMonthSaleQty(Integer monthSaleQty) {
        this.monthSaleQty = monthSaleQty;
    }

    public Integer getRecentMonthSaleQty() {
        return recentMonthSaleQty;
    }

    public void setRecentMonthSaleQty(Integer recentMonthSaleQty) {
        this.recentMonthSaleQty = recentMonthSaleQty;
    }

    public BigDecimal getSaleMoney() {
        return saleMoney;
    }

    public void setSaleMoney(BigDecimal saleMoney) {
        this.saleMoney = saleMoney;
    }

    public BigDecimal getMonthSaleMoney() {
        return monthSaleMoney;
    }

    public void setMonthSaleMoney(BigDecimal monthSaleMoney) {
        this.monthSaleMoney = monthSaleMoney;
    }

    public String getCardQtyStr() {
        Integer realCardQry=cardQty==null?0:cardQty;
        Integer realSaleQty=saleQty==null?0:saleQty;
        return realCardQry + "/" + realSaleQty;
    }


    public String getMonthCardQtyStr() {
        Integer realMonthCardQty=monthCardQty==null?0:monthCardQty;
        Integer realMonthSaleQty=monthSaleQty==null?0:monthSaleQty;
        return realMonthCardQty + "/" + realMonthSaleQty;
    }

    public int getCardQtyDiff() {
        Integer realCardQry=cardQty==null?0:cardQty;
        Integer realSaleQty=saleQty==null?0:saleQty;
        Integer diff = realCardQry-realSaleQty;
        return Math.abs(diff);
    }
}
