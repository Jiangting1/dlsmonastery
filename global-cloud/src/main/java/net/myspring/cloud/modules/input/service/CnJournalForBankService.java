package net.myspring.cloud.modules.input.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.common.enums.*;
import net.myspring.common.utils.HandsontableUtils;
import net.myspring.cloud.modules.input.dto.CnJournalEntityForBankDto;
import net.myspring.cloud.modules.input.dto.CnJournalForBankDto;
import net.myspring.cloud.modules.input.dto.KingdeeSynDto;
import net.myspring.cloud.modules.input.manager.KingdeeManager;
import net.myspring.cloud.modules.input.web.form.CnJournalForBankForm;
import net.myspring.cloud.modules.kingdee.domain.*;
import net.myspring.cloud.modules.kingdee.repository.*;
import net.myspring.cloud.modules.sys.domain.AccountKingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.common.exception.ServiceException;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 手工日记账之銀行存款日记账
 * Created by lihx on 2017/6/9.
 */
@Service
@KingdeeDataSource
@Transactional(readOnly = true)
public class CnJournalForBankService {
    @Autowired
    private BdCustomerRepository bdCustomerRepository;
    @Autowired
    private BdDepartmentRepository bdDepartmentRepository;
    @Autowired
    private HrEmpInfoRepository hrEmpInfoRepository;
    @Autowired
    private BasAssistantRepository basAssistantRepository;
    @Autowired
    private BdAccountRepository bdAccountRepository;
    @Autowired
    private BdSettleTypeRepository bdSettleTypeRepository;
    @Autowired
    private CnBankAcntRepository cnBankAcntRepository;
    @Autowired
    private KingdeeManager kingdeeManager;

    private KingdeeSynDto save(CnJournalForBankDto cnJournalForBankDto, KingdeeBook kingdeeBook){
        KingdeeSynDto kingdeeSynDto = new KingdeeSynDto(
                    cnJournalForBankDto.getExtendId(),
                    cnJournalForBankDto.getExtendType(),
                    KingdeeFormIdEnum.CN_JOURNAL.name(),
                    cnJournalForBankDto.getJson(),
                    kingdeeBook);
        kingdeeSynDto = kingdeeManager.save(kingdeeSynDto);
        if (!kingdeeSynDto.getSuccess()){
            throw new ServiceException("銀行存取款日记账失败："+kingdeeSynDto.getResult());
        }
        return kingdeeSynDto;
    }

    @Transactional
    public KingdeeSynDto save(CnJournalForBankDto cnJournalForBankDto, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook){
        KingdeeSynDto kingdeeSynDto;
        Boolean isLogin = kingdeeManager.login(kingdeeBook.getKingdeePostUrl(),kingdeeBook.getKingdeeDbid(),accountKingdeeBook.getUsername(),accountKingdeeBook.getPassword());
        if(isLogin) {
            kingdeeSynDto = save(cnJournalForBankDto,kingdeeBook);
        }else{
            throw new ServiceException("登入金蝶系统失败，请检查您的账户密码是否正确");
        }
        return kingdeeSynDto;
    }

    @Transactional
    public List<KingdeeSynDto> save(List<CnJournalForBankDto> cnJournalForBankDtoList, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook){
        List<KingdeeSynDto> kingdeeSynExtendDtoList = Lists.newArrayList();
        //财务出库开单
        if (CollectionUtil.isNotEmpty(cnJournalForBankDtoList)) {
            Boolean isLogin = kingdeeManager.login(kingdeeBook.getKingdeePostUrl(),kingdeeBook.getKingdeeDbid(),accountKingdeeBook.getUsername(),accountKingdeeBook.getPassword());
            if(isLogin) {
                for (CnJournalForBankDto cnJournalForBankDto : cnJournalForBankDtoList) {
                    KingdeeSynDto kingdeeSynExtendDto = save(cnJournalForBankDto,kingdeeBook);
                    kingdeeSynExtendDtoList.add(kingdeeSynExtendDto);
                }
            }
        }else{
            throw new ServiceException("登入金蝶系统失败，请检查您的账户密码是否正确");
        }
        return kingdeeSynExtendDtoList;
    }

    @Transactional
    public KingdeeSynDto save(CnJournalForBankForm cnJournalForBankForm, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook) {
        Map<String, String> customerNameMap = Maps.newHashMap();
        LocalDate billDate = cnJournalForBankForm.getBillDate();
        String accountNumberForBank = cnJournalForBankForm.getAccountNumber();
        String json = HtmlUtils.htmlUnescape(cnJournalForBankForm.getJson());
        List<List<Object>> data = ObjectMapperUtils.readValue(json, ArrayList.class);
        List<String> settleTypeNameList = Lists.newArrayList();
        List<String> bankAcntNameList = Lists.newArrayList();
        List<String> empInfoNameList = Lists.newArrayList();
        List<String> departmentNameList = Lists.newArrayList();
        List<String> assistantNameList = Lists.newArrayList();
        List<String> customerNameForList = Lists.newArrayList();
        for (List<Object> row : data){
            settleTypeNameList.add(HandsontableUtils.getValue(row,1));
            bankAcntNameList.add(HandsontableUtils.getValue(row,4));
            empInfoNameList.add(HandsontableUtils.getValue(row, 7));
            departmentNameList.add(HandsontableUtils.getValue(row, 8));
            assistantNameList.add(HandsontableUtils.getValue(row, 9));
            assistantNameList.add(HandsontableUtils.getValue(row, 10));
            if (row.size() > 11) {
                customerNameForList.add(HandsontableUtils.getValue(row, 11));
            }
        }
        Map<String, String> settleTypeNameMap = bdSettleTypeRepository.findByNameList(settleTypeNameList).stream().collect(Collectors.toMap(BdSettleType::getFName,BdSettleType::getFNumber));
        Map<String, String> bankAcntNameMap = cnBankAcntRepository.findByNameList(bankAcntNameList).stream().collect(Collectors.toMap(CnBankAcnt::getFName,CnBankAcnt::getFNumber));
        Map<String, String> empInfoNameMap = hrEmpInfoRepository.findByNameList(empInfoNameList).stream().collect(Collectors.toMap(HrEmpInfo::getFName,HrEmpInfo::getFNumber));
        Map<String, String> departmentNameMap  = bdDepartmentRepository.findByNameList(departmentNameList).stream().collect(Collectors.toMap(BdDepartment::getFFullName,BdDepartment::getFNumber));
        List<BasAssistant> basAssistantList = basAssistantRepository.findByNameList(assistantNameList);
        Map<String, String> otherTypeNameMap = Maps.newHashMap();
        Map<String, String> expenseTypeNameMap = Maps.newHashMap();
        for (BasAssistant basAssistant :basAssistantList){
            if (BasAssistantTypeEnum.其他类.name().equals(basAssistant.getFType())){
                otherTypeNameMap.put(basAssistant.getFDataValue(),basAssistant.getFNumber());
            }else if(BasAssistantTypeEnum.费用类.name().equals(basAssistant.getFType())){
                expenseTypeNameMap.put(basAssistant.getFDataValue(),basAssistant.getFNumber());
            }
        }
        if (customerNameForList.size() > 0){
            customerNameMap = bdCustomerRepository.findByNameList(customerNameForList).stream().collect(Collectors.toMap(BdCustomer::getFName,BdCustomer::getFNumber));
        }
        CnJournalForBankDto cnJournalForBankDto = new CnJournalForBankDto();
        cnJournalForBankDto.setExtendType(ExtendTypeEnum.銀行存款日记账_K3.name());
        cnJournalForBankDto.setCreator(accountKingdeeBook.getUsername());
        cnJournalForBankDto.setDate(billDate);
        cnJournalForBankDto.setAccountNumberForBank(accountNumberForBank);
        cnJournalForBankDto.setKingdeeName(kingdeeBook.getName());
        cnJournalForBankDto.setKingdeeType(kingdeeBook.getType());
        for (List<Object> row : data) {
            String accountNumber = HandsontableUtils.getValue(row, 0);
            String settleTypeName = HandsontableUtils.getValue(row,1);
            String debitAmountStr =  HandsontableUtils.getValue(row, 2);
            BigDecimal debitAmount = StringUtils.isBlank(debitAmountStr) ? BigDecimal.ZERO : new BigDecimal(debitAmountStr);
            String creditAmountStr =  HandsontableUtils.getValue(row, 3);
            BigDecimal creditAmount = StringUtils.isBlank(creditAmountStr) ? BigDecimal.ZERO : new BigDecimal(creditAmountStr);
            String bankAcountName = HandsontableUtils.getValue(row,4);
            String remarks =  HandsontableUtils.getValue(row, 5);
            String empInfoName =  HandsontableUtils.getValue(row, 7);
            String departmentName = HandsontableUtils.getValue(row, 8);
            String otherTypeName =  HandsontableUtils.getValue(row, 9);
            String expenseTypeName =  HandsontableUtils.getValue(row, 10);
            String customerNameFor = "";
            if (row.size() > 11) {
                customerNameFor =  HandsontableUtils.getValue(row, 11);
            }
            CnJournalEntityForBankDto cnJournalEntityForBankDto = new CnJournalEntityForBankDto();
            cnJournalEntityForBankDto.setAccountNumber(accountNumber);
            cnJournalEntityForBankDto.setSettleTypeNumber(settleTypeNameMap.get(settleTypeName));
            cnJournalEntityForBankDto.setDebitAmount(debitAmount);
            cnJournalEntityForBankDto.setCreditAmount(creditAmount);
            cnJournalEntityForBankDto.setBankAccountNumber(bankAcntNameMap.get(bankAcountName));
            cnJournalEntityForBankDto.setComment(remarks);
            cnJournalEntityForBankDto.setEmpInfoNumber(empInfoNameMap.get(empInfoName));
            cnJournalEntityForBankDto.setDepartmentNumber(departmentNameMap.get(departmentName));
            cnJournalEntityForBankDto.setOtherTypeNumber(otherTypeNameMap.get(otherTypeName));
            cnJournalEntityForBankDto.setExpenseTypeNumber(expenseTypeNameMap.get(expenseTypeName));
            cnJournalEntityForBankDto.setCustomerNumber(customerNameMap.get(customerNameFor));
            cnJournalForBankDto.getEntityForBankDtoList().add(cnJournalEntityForBankDto);
        }
        return save(cnJournalForBankDto,kingdeeBook,accountKingdeeBook);
    }

    @Transactional
    public List<KingdeeSynDto> saveForWS(List<CnJournalForBankDto> cnJournalForBankDtoList, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook){
        for (CnJournalForBankDto cnJournalForBankDto : cnJournalForBankDtoList) {
            cnJournalForBankDto.setCreator(accountKingdeeBook.getUsername());
            cnJournalForBankDto.setKingdeeName(kingdeeBook.getName());
            cnJournalForBankDto.setKingdeeType(kingdeeBook.getType());
        }
        return save(cnJournalForBankDtoList,kingdeeBook,accountKingdeeBook);
    }

    public CnJournalForBankForm getForm(KingdeeBook kingdeeBook){
        CnJournalForBankForm cnJournalForBankForm = new CnJournalForBankForm();
        Map<String,Object> map = Maps.newHashMap();
        map.put("accountNumberList",bdAccountRepository.findAll().stream().map(BdAccount::getFNumber).collect(Collectors.toList()));
        map.put("settleTypeNameList",bdSettleTypeRepository.findAllForDefault().stream().map(BdSettleType::getFName).collect(Collectors.toList()));
        map.put("bankAcntNameList",cnBankAcntRepository.findAll().stream().map(CnBankAcnt::getFName).collect(Collectors.toList()));
        map.put("empInfoNameList",hrEmpInfoRepository.findAll().stream().map(HrEmpInfo::getFName).collect(Collectors.toList()));
        map.put("departmentNameList",bdDepartmentRepository.findAll().stream().map(BdDepartment::getFFullName).collect(Collectors.toList()));
        map.put("otherTypeNameList",basAssistantRepository.findByType(BasAssistantTypeEnum.其他类.name()).stream().map(BasAssistant::getFDataValue).collect(Collectors.toList()));
        map.put("expenseTypeNameList",basAssistantRepository.findByType(BasAssistantTypeEnum.费用类.name()).stream().map(BasAssistant::getFDataValue).collect(Collectors.toList()));
        if (KingdeeNameEnum.WZOPPO.name().equals(kingdeeBook.getName()) || KingdeeTypeEnum.proxy.name().equals(kingdeeBook.getType())) {
            map.put("customerForFlag",true);
            map.put("customerNameForList",bdCustomerRepository.findAll().stream().map(BdCustomer::getFName).collect(Collectors.toList()));
        }else {
            map.put("customerForFlag",false);
        }
        map.put("accountForBankList",bdAccountRepository.findByIsBank(true));
        cnJournalForBankForm.setExtra(map);
        return cnJournalForBankForm;
    }
}
