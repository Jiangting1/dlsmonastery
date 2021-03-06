package net.myspring.cloud.modules.input.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.cloud.common.enums.KingdeeNameEnum;
import net.myspring.cloud.common.enums.KingdeeTypeEnum;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 手工日记账之现金日记账
 * Created by lihx on 2017/6/8.
 */
public class CnJournalForCashDto {
    //附加-业务系统单据Id
    private String extendId;
    //附加-单据类型
    private String extendType;
    //创建人
    private String creator;
    //日期
    private LocalDate date;
    //本线程金蝶数据源
    private KingdeeBook kingdeeBook;

    private List<CnJournalEntityForCashDto> entityForCashDtoList = Lists.newArrayList();

    public String getExtendId() {
        return extendId;
    }

    public void setExtendId(String extendId) {
        this.extendId = extendId;
    }

    public String getExtendType() {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public KingdeeBook getKingdeeBook() {
        return kingdeeBook;
    }

    public void setKingdeeBook(KingdeeBook kingdeeBook) {
        this.kingdeeBook = kingdeeBook;
    }

    public List<CnJournalEntityForCashDto> getEntityForCashDtoList() {
        return entityForCashDtoList;
    }

    public void setEntityForCashDtoList(List<CnJournalEntityForCashDto> entityForCashDtoList) {
        this.entityForCashDtoList = entityForCashDtoList;
    }

    @JsonIgnore
    public String getJson() {
        Map<String, Object> root = Maps.newLinkedHashMap();
        root.put("Creator", getCreator());
        root.put("NeedUpDateFields", Lists.newArrayList());
        Map<String, Object> model = Maps.newLinkedHashMap();
        model.put("FID", 0);
        model.put("FDATE", LocalDateUtils.format(getDate(),"yyyy-M-d"));
        model.put("FBillTypeID", CollectionUtil.getMap("FNumber", "SGRJZ02_SYS"));
        model.put("FPAYORGID", CollectionUtil.getMap("FNumber", "100"));
        model.put("FAcctBookId", CollectionUtil.getMap("FNumber", "001"));
        model.put("FSTARTPERIOD", LocalDateUtils.format(getDate(),"yyyy.M"));
        model.put("FACCOUNTID", CollectionUtil.getMap("FNumber", "1001"));
        model.put("FCURRENCYID", CollectionUtil.getMap("FNumber", "PRE001"));
        model.put("FMAINBOOKID", CollectionUtil.getMap("FNumber", "PRE001"));
        model.put("FEXCHANGETYPE", CollectionUtil.getMap("FNumber", "HLTX01_SYS"));
        model.put("FEXCHANGERATE", 1);
        List<Object> entity = Lists.newArrayList();
        BigDecimal debitAmounts = BigDecimal.ZERO;
        BigDecimal creditAmounts = BigDecimal.ZERO;
        for (CnJournalEntityForCashDto entityForCashDto : getEntityForCashDtoList()){
            Map<String, Object> detail = Maps.newLinkedHashMap();
            detail.put("F_PAEC_Base", CollectionUtil.getMap("FNumber", entityForCashDto.getDepartmentNumber()));
            detail.put("F_PAEC_Base1", CollectionUtil.getMap("FStaffNumber", entityForCashDto.getStaffNumber()));
            detail.put("F_PAEC_Assistant", CollectionUtil.getMap("FNumber", entityForCashDto.getOtherTypeNumber()));
            detail.put("F_PAEC_Assistant1", CollectionUtil.getMap("FNumber", entityForCashDto.getExpenseTypeNumber()));
            if (StringUtils.isNotBlank(entityForCashDto.getCustomerNumberFor())){
                if (KingdeeNameEnum.WZOPPO.name().equals(kingdeeBook.getName())) {
                    detail.put("F_PAEC_Base2", CollectionUtil.getMap("FNumber", entityForCashDto.getCustomerNumberFor()));
                }else if (KingdeeTypeEnum.proxy.name().equals(kingdeeBook.getType())){
                    detail.put("F_YLG_BASE", CollectionUtil.getMap("FNumber", entityForCashDto.getCustomerNumberFor()));
                }
            }
            detail.put("FSETTLETYPEID", CollectionUtil.getMap("FNumber", "JSFS01_SYS"));
            detail.put("FCREDITAMOUNT", entityForCashDto.getCreditAmount());
            // 借方
            detail.put("FDEBITAMOUNT", entityForCashDto.getDebitAmount());
            detail.put("FVOUCHERGROUPID", CollectionUtil.getMap("FNumber", "PRE001"));
            detail.put("FOPPOSITEACCOUNTID", CollectionUtil.getMap("FNumber", entityForCashDto.getAccountNumber()));
            detail.put("FCOMMENT", entityForCashDto.getRemarks());
            entity.add(detail);
            if (entityForCashDto.getDebitAmount() != null){
                debitAmounts = debitAmounts.add(entityForCashDto.getDebitAmount());
            }
            if (entityForCashDto.getCreditAmount() != null){
                creditAmounts = creditAmounts.add(entityForCashDto.getCreditAmount());
            }

        }
        model.put("FCREDITSUMAMOUNTLOC", creditAmounts);
        model.put("FCREDITSUMAMOUNT", creditAmounts);
        // 借方
        model.put("FDEBITSUMAMOUNT", debitAmounts);
        model.put("FDEBITSUMAMOUNTLOC", debitAmounts);
        model.put("FJOURNALENTRY", entity);
        root.put("Model", model);
        String result = ObjectMapperUtils.writeValueAsString(root);
        //System.out.println(result);
        return result;
    }
}
