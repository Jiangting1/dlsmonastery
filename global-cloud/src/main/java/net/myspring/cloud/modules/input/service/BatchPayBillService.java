package net.myspring.cloud.modules.input.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.common.enums.CharEnum;
import net.myspring.cloud.common.enums.K3CloudFormIdEnum;
import net.myspring.cloud.common.handsontable.HandSonTableUtils;
import net.myspring.cloud.common.utils.CacheUtils;
import net.myspring.cloud.modules.input.dto.BatchPayBillDto;
import net.myspring.cloud.modules.input.dto.K3CloudSave;
import net.myspring.cloud.modules.input.dto.NameNumberDto;
import net.myspring.cloud.modules.input.mapper.*;
import net.myspring.cloud.modules.input.utils.K3cloudUtils;
import net.myspring.cloud.modules.remote.dto.AccountDto;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 应付单
 * Created by liuj on 2016-06-27.
 */
@Service
@KingdeeDataSource
public class BatchPayBillService {
    @Autowired
    private BdSupplierMapper bdSupplierMapper;
    @Autowired
    private CnBankMapper cnBankMapper;
    @Autowired
    private BdSettleTypeMapper bdSettleTypeMapper;
    @Autowired
    private BdAccountMapper bdAccountMapper;
    @Autowired
    private BdDepartmentMapper bdDepartmentMapper;
    @Autowired
    private CacheUtils cacheUtils;

    public List<String> save(List<List<Object>> datas, LocalDate billDate) {
        Map<String, BatchPayBillDto> payBillMap = Maps.newLinkedHashMap();
        Map<String, String> supplierMap = Maps.newHashMap();
        Map<String, String> bankMap = Maps.newHashMap();
        Map<String, String> settleTypeMap = Maps.newHashMap();
        Map<String, String> subjectMap = Maps.newHashMap();
        Map<String, String> departmentMap = Maps.newHashMap();
        for (NameNumberDto bdSettleType : bdSupplierMapper.findNameAndNumber()) {
            supplierMap.put(bdSettleType.getName(), bdSettleType.getNumber());
        }
        for (NameNumberDto cnBank : cnBankMapper.findNameAndNumber()) {
            bankMap.put(cnBank.getName(), cnBank.getNumber());
        }
        for (NameNumberDto bdSettleType : bdSettleTypeMapper.findNameAndNumber()) {
            settleTypeMap.put(bdSettleType.getName(), bdSettleType.getNumber());
        }
        for (NameNumberDto bdSettleType : bdAccountMapper.findNameAndNumber()) {
            subjectMap.put(bdSettleType.getName(), bdSettleType.getNumber());
        }
        for (NameNumberDto bdSettleType : bdDepartmentMapper.findNameAndNumber()) {
            departmentMap.put(bdSettleType.getName(), bdSettleType.getNumber());
        }
        for (List<Object> row : datas) {
            String supplierName = HandSonTableUtils.getValue(row, 0);
            String departName = HandSonTableUtils.getValue(row, 1);
            String bankName = HandSonTableUtils.getValue(row, 2);
            String settleType = HandSonTableUtils.getValue(row, 3);
            String priceStr = HandSonTableUtils.getValue(row, 4);
            BigDecimal amount = StringUtils.isEmpty(priceStr) ? BigDecimal.ZERO : new BigDecimal(priceStr);
            String note = HandSonTableUtils.getValue(row, 5);
            String subject = HandSonTableUtils.getValue(row, 6);

            String billKey = supplierName + CharEnum.COMMA + departName + CharEnum.COMMA + bankName + CharEnum.COMMA + settleType + CharEnum.COMMA + amount + CharEnum.COMMA + note + CharEnum.COMMA + subject;

            if (!payBillMap.containsKey(billKey)) {
                BatchPayBillDto payBill = new BatchPayBillDto();
                payBill.setDate(billDate);
                payBill.setDepartment(departmentMap.get(departName));
                payBill.setSupplier(supplierMap.get(supplierName));
                if (StringUtils.isNotBlank(bankName)) {
                    payBill.setBank(bankMap.get(bankName));
                }
                payBill.setSettleType(settleTypeMap.get(settleType));
                payBill.setAmout(amount);
                payBill.setNote(note);
                payBill.setSubject(subjectMap.get(subject));
                payBillMap.put(billKey, payBill);
            } else {
                payBillMap.get(billKey).setAmout(amount.add(amount));
            }
        }
        AccountDto accountDto = new AccountDto();
        cacheUtils.initCacheInput(accountDto);
        List<BatchPayBillDto> billList = Lists.newArrayList(payBillMap.values());
        List<String> billNos = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(billList)) {
            for (BatchPayBillDto payBill : billList) {
                K3CloudSave k3CloudSave = new K3CloudSave(K3CloudFormIdEnum.AP_PAYBILL.name(), getPayBill(payBill,accountDto));
                String billNo = K3cloudUtils.save(k3CloudSave,accountDto).getBillNo();
                billNos.add(billNo);
            }
        }
        return billNos;
    }

    //批量应付单
    public String getPayBill(BatchPayBillDto payBill, AccountDto accountDto) {
        Map<String, Object> root = Maps.newLinkedHashMap();
        root.put("Creator", accountDto.getName());
        root.put("NeedUpDateFields", Lists.newArrayList());
        Map<String, Object> model = Maps.newLinkedHashMap();
        model.put("FID", 0);
        model.put("FDate", payBill.getDate());
        model.put("FBillTypeID", K3cloudUtils.getMap("FNumber", "FKDLX01_SYS"));
        model.put("FCONTACTUNITTYPE", "BD_Supplier");
        model.put("FCONTACTUNIT", K3cloudUtils.getMap("FNumber", payBill.getSupplier()));
        model.put("FRECTUNITTYPE", "BD_Supplier");
        model.put("FRECTUNIT", K3cloudUtils.getMap("FNumber", payBill.getSupplier()));
        model.put("FCURRENCYID", K3cloudUtils.getMap("FNumber", "PRE001"));
        model.put("FSETTLECUR", K3cloudUtils.getMap("FNumber", "PRE001"));
        model.put("FSETTLERATE", 1);
        model.put("FPAYORGID", K3cloudUtils.getMap("FNumber", "100"));
        model.put("FSETTLEORGID", K3cloudUtils.getMap("FNumber", "100"));
        model.put("FPURCHASEDEPTID", K3cloudUtils.getMap("FNumber", payBill.getDepartment()));
        model.put("FPAYTOTALAMOUNTFOR_H", payBill.getAmout());
        model.put("FREALPAYAMOUNTFOR_H", payBill.getAmout());
        model.put("FEXCHANGERATE", 1);

        List<Object> entity = Lists.newArrayList();
        Map<String, Object> detail = Maps.newLinkedHashMap();
        detail.put("FSETTLETYPEID", K3cloudUtils.getMap("FNumber", payBill.getSettleType()));
        if (StringUtils.isNotBlank(payBill.getBank())) {
            detail.put("FACCOUNTID", K3cloudUtils.getMap("FNumber", payBill.getBank()));
        }
        detail.put("FPAYTOTALAMOUNTFOR", payBill.getAmout());
        detail.put("FREALPAYAMOUNTFOR_D", payBill.getAmout());
        detail.put("FSETTLEPAYAMOUNTFOR", payBill.getAmout());
        detail.put("F_YLG_Base", K3cloudUtils.getMap("FNumber", payBill.getSubject()));
        detail.put("FCOMMENT", payBill.getNote());
        entity.add(detail);
        model.put("AP_PAYBILL__FPAYBILLENTRY", entity);
        root.put("Model", model);
        String result = ObjectMapperUtils.writeValueAsString(root);
        return result;
    }

}