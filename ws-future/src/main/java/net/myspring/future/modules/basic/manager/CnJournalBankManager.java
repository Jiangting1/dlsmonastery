package net.myspring.future.modules.basic.manager;

import com.google.common.collect.Lists;
import net.myspring.cloud.common.enums.ExtendTypeEnum;
import net.myspring.cloud.modules.input.dto.CnJournalEntityForBankDto;
import net.myspring.cloud.modules.input.dto.CnJournalForBankDto;
import net.myspring.cloud.modules.sys.dto.KingdeeSynReturnDto;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.enums.SettleTypeEnum;
import net.myspring.common.exception.ServiceException;
import net.myspring.common.enums.CompanyNameEnum;
import net.myspring.future.common.enums.ShopDepositTypeEnum;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.client.CloudClient;
import net.myspring.future.modules.basic.domain.Bank;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.domain.EmployeePhoneDeposit;
import net.myspring.future.modules.basic.repository.BankRepository;
import net.myspring.future.modules.basic.repository.DepotRepository;
import net.myspring.future.modules.layout.domain.ShopDeposit;
import net.myspring.future.modules.layout.domain.ShopGoodsDeposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 银行存款日记账
 * Created by lihx on 2017/6/30.
 */
@Component
public class CnJournalBankManager {
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private CloudClient cloudClient;

    public KingdeeSynReturnDto synForShopDeposit(ShopDeposit shopDeposit,String departmentNumber,ShopDepositTypeEnum type){
        if (!CompanyNameEnum.WZOPPO.name().equals(RequestUtils.getCompanyName())) {
            List<CnJournalForBankDto> cnJournalForBankDtoList = Lists.newArrayList();
            Bank bank = bankRepository.findOne(shopDeposit.getBankId());
            Depot depot = depotRepository.findOne(shopDeposit.getShopId());
            CnJournalForBankDto cnJournalForBankDto = new CnJournalForBankDto();
            cnJournalForBankDto.setExtendId(shopDeposit.getId());
            cnJournalForBankDto.setExtendType(ExtendTypeEnum.押金列表.name());
            cnJournalForBankDto.setDate(shopDeposit.getBillDate());
            cnJournalForBankDto.setAccountNumberForBank("1002");//银行存款
            List<CnJournalEntityForBankDto> cnJournalEntityForBankDtoList = Lists.newArrayList();
            CnJournalEntityForBankDto entityForBankDto = new CnJournalEntityForBankDto();
            if (shopDeposit.getAmount().compareTo(BigDecimal.ZERO) == 1) {
                entityForBankDto.setDebitAmount(shopDeposit.getAmount());
            } else {
                entityForBankDto.setCreditAmount(shopDeposit.getAmount().multiply(new BigDecimal(-1)));
            }
            if (departmentNumber != null) {
                entityForBankDto.setDepartmentNumber(departmentNumber);
            } else {
                throw new ServiceException("部门不能为空");
            }
            if (bank.getCode() != null) {
                entityForBankDto.setBankAccountNumber(bank.getCode());
            } else {
                throw new ServiceException(bank.getName() + ",该银行没有编码，不能开单");
            }
            entityForBankDto.setAccountNumber("2241");//其他应付款
            entityForBankDto.setSettleTypeNumber(SettleTypeEnum.电汇.getFNumber());//电汇
            entityForBankDto.setEmpInfoNumber("0001");//员工
            if (CompanyNameEnum.IDVIVO.name().equals(RequestUtils.getCompanyName())) {
                if (ShopDepositTypeEnum.市场保证金.equals(type)) {
                    entityForBankDto.setOtherTypeNumber("2241.002B");//其他应付款-客户押金（批发）-市场保证金
                    entityForBankDto.setComment(depot.getName() + CharConstant.COMMA + ShopDepositTypeEnum.市场保证金.name() + CharConstant.COMMA + shopDeposit.getRemarks());
                } else if (ShopDepositTypeEnum.形象保证金.equals(type)) {
                    entityForBankDto.setOtherTypeNumber("2241.002A");//其他应付款-客户押金（批发）-形象押金
                    entityForBankDto.setComment(depot.getName() + CharConstant.COMMA + ShopDepositTypeEnum.形象保证金.name() + CharConstant.COMMA + shopDeposit.getRemarks());
                } else if (ShopDepositTypeEnum.演示机押金.equals(type)) {
                    entityForBankDto.setOtherTypeNumber("2241.002C");//其他应付款-客户押金（批发）-演示机押金
                }
            } else if (CompanyNameEnum.JXOPPO.name().equals(RequestUtils.getCompanyName())) {
                if (ShopDepositTypeEnum.市场保证金.equals(type)) {
                    entityForBankDto.setOtherTypeNumber("2241.00002B");//其他应付款-客户押金（批发）-市场保证金
                    entityForBankDto.setComment(depot.getName() + CharConstant.COMMA + ShopDepositTypeEnum.市场保证金.name() + CharConstant.COMMA + shopDeposit.getRemarks());
                } else if (ShopDepositTypeEnum.形象保证金.equals(type)) {
                    entityForBankDto.setOtherTypeNumber("2241.00002A");//其他应付款-客户押金（批发）-形象押金
                    entityForBankDto.setComment(depot.getName() + CharConstant.COMMA + ShopDepositTypeEnum.形象保证金.name() + CharConstant.COMMA + shopDeposit.getRemarks());
                } else if (ShopDepositTypeEnum.演示机押金.equals(type)) {
                    throw new ServiceException("财务暂时未开--其他应付款-客户押金（批发）-演示机押金");//其他应付款-客户押金（批发）-演示机押金
                }
            } else if (CompanyNameEnum.JXDJ.name().equals(RequestUtils.getCompanyName())) {
                if (ShopDepositTypeEnum.市场保证金.equals(type)) {
                    entityForBankDto.setOtherTypeNumber("2241.102");//其他应付款-客户押金（批发）-市场保证金
                    entityForBankDto.setComment(depot.getName() + CharConstant.COMMA + ShopDepositTypeEnum.市场保证金.name() + CharConstant.COMMA + shopDeposit.getRemarks());
                } else if (ShopDepositTypeEnum.形象保证金.equals(type)) {
                    entityForBankDto.setOtherTypeNumber("2241.101");//其他应付款-客户押金（批发）-形象押金
                    entityForBankDto.setComment(depot.getName() + CharConstant.COMMA + ShopDepositTypeEnum.形象保证金.name() + CharConstant.COMMA + shopDeposit.getRemarks());
                } else if (ShopDepositTypeEnum.演示机押金.equals(type)) {
                    throw new ServiceException("财务暂时未开--其他应付款-客户押金（批发）-演示机押金");//其他应付款-客户押金（批发）-演示机押金
                }
            } else if (CompanyNameEnum.JXVIVO.name().equals(RequestUtils.getCompanyName())) {
                if (ShopDepositTypeEnum.市场保证金.equals(type)) {
                    entityForBankDto.setOtherTypeNumber("2241.00002B");//其他应付款-客户押金（批发）-市场保证金
                    entityForBankDto.setComment(depot.getName() + CharConstant.COMMA + ShopDepositTypeEnum.市场保证金.name() + CharConstant.COMMA + shopDeposit.getRemarks());
                } else if (ShopDepositTypeEnum.形象保证金.equals(type)) {
                    entityForBankDto.setOtherTypeNumber("2241.00002A");//其他应付款-客户押金（批发）-形象押金
                    entityForBankDto.setComment(depot.getName() + CharConstant.COMMA + ShopDepositTypeEnum.形象保证金.name() + CharConstant.COMMA + shopDeposit.getRemarks());
                } else if (ShopDepositTypeEnum.演示机押金.equals(type)) {
                    throw new ServiceException("财务暂时未开--其他应付款-客户押金（批发）-演示机押金");//其他应付款-客户押金（批发）-演示机押金
                }
            }
            entityForBankDto.setExpenseTypeNumber("6602.000");//无
            entityForBankDto.setCustomerNumber(null);
            entityForBankDto.setComment(depot.getName() + shopDeposit.getRemarks());
            cnJournalEntityForBankDtoList.add(entityForBankDto);
            cnJournalForBankDto.setEntityForBankDtoList(cnJournalEntityForBankDtoList);
            cnJournalForBankDtoList.add(cnJournalForBankDto);
            return cloudClient.synJournalBank(cnJournalForBankDtoList).get(0);
        }
        return null;
    }

    public List<KingdeeSynReturnDto> synEmployeePhoneDeposit(List<EmployeePhoneDeposit> employeePhoneDepositList){
        if (!CompanyNameEnum.IDVIVO.name().equals(RequestUtils.getCompanyName()) && !CompanyNameEnum.JXDJ.name().equals(RequestUtils.getCompanyName()) && !CompanyNameEnum.WZOPPO.name().equals(RequestUtils.getCompanyName())) {
            List<CnJournalForBankDto> cnJournalForBankDtoList = Lists.newArrayList();
            for (EmployeePhoneDeposit employeePhoneDeposit : employeePhoneDepositList) {
                Bank bank = bankRepository.findOne(employeePhoneDeposit.getBankId());
                Depot depot = depotRepository.findOne(employeePhoneDeposit.getDepotId());
                CnJournalForBankDto cnJournalForBankDto = new CnJournalForBankDto();
                cnJournalForBankDto.setExtendId(employeePhoneDeposit.getId());
                cnJournalForBankDto.setExtendType(ExtendTypeEnum.导购用机.name());
                cnJournalForBankDto.setDate(LocalDate.now());
                cnJournalForBankDto.setAccountNumberForBank("1002");
                List<CnJournalEntityForBankDto> cnJournalEntityForBankDtoList = Lists.newArrayList();
                CnJournalEntityForBankDto entityForBankDto = new CnJournalEntityForBankDto();
                if (employeePhoneDeposit.getAmount().compareTo(BigDecimal.ZERO) == 1) {
                    entityForBankDto.setDebitAmount(employeePhoneDeposit.getAmount());
                } else {
                    entityForBankDto.setCreditAmount(employeePhoneDeposit.getAmount().multiply(new BigDecimal(-1)));
                }
                if (employeePhoneDeposit.getDepartment() != null) {
                    entityForBankDto.setDepartmentNumber(employeePhoneDeposit.getDepartment());
                } else {
                    throw new ServiceException("部门不能为空");
                }
                if (bank.getCode() != null) {
                    entityForBankDto.setBankAccountNumber(bank.getCode());
                } else {
                    throw new ServiceException(bank.getName() + ",该银行没有编码，不能开单");
                }
                entityForBankDto.setAccountNumber("2241");//其他应付款
                entityForBankDto.setSettleTypeNumber(SettleTypeEnum.电汇.getFNumber());//电汇
                entityForBankDto.setEmpInfoNumber("0001");//员工
                if (CompanyNameEnum.JXVIVO.name().equals(RequestUtils.getCompanyName())){
                    entityForBankDto.setOtherTypeNumber("2241.00019");//其他应付款-导购业务机押金
                }else if (CompanyNameEnum.JXOPPO.name().equals(RequestUtils.getCompanyName())) {
                    entityForBankDto.setOtherTypeNumber("2241.00029");//其他应付款-导购业务机押金
                }
                entityForBankDto.setExpenseTypeNumber("6602.000");//无
                entityForBankDto.setCustomerNumber(null);
                entityForBankDto.setComment(depot.getName() + employeePhoneDeposit.getRemarks());
                cnJournalEntityForBankDtoList.add(entityForBankDto);
                cnJournalForBankDto.setEntityForBankDtoList(cnJournalEntityForBankDtoList);
                cnJournalForBankDtoList.add(cnJournalForBankDto);
            }
            return cloudClient.synJournalBank(cnJournalForBankDtoList);
        }
        return null;
    }

    public KingdeeSynReturnDto synForShopGoodsDeposit(ShopGoodsDeposit shopGoodsDeposit, String departmentNumber){
        if (!CompanyNameEnum.IDVIVO.name().equals(RequestUtils.getCompanyName()) && !CompanyNameEnum.JXDJ.name().equals(RequestUtils.getCompanyName()) && !CompanyNameEnum.WZOPPO.name().equals(RequestUtils.getCompanyName())) {
            List<CnJournalForBankDto> cnJournalForBankDtoList = Lists.newArrayList();
            Bank bank = bankRepository.findOne(shopGoodsDeposit.getBankId());
            Depot depot = depotRepository.findOne(shopGoodsDeposit.getShopId());
            CnJournalForBankDto cnJournalForBankDto = new CnJournalForBankDto();
            cnJournalForBankDto.setExtendId(shopGoodsDeposit.getId());
            cnJournalForBankDto.setExtendType(ExtendTypeEnum.定金收款.name());
            cnJournalForBankDto.setDate(LocalDate.now());
            cnJournalForBankDto.setAccountNumberForBank("1002");//银行存款
            List<CnJournalEntityForBankDto> cnJournalEntityForBankDtoList = Lists.newArrayList();

            CnJournalEntityForBankDto entityForBankDto = new CnJournalEntityForBankDto();
            if (shopGoodsDeposit.getAmount().compareTo(BigDecimal.ZERO) == 1) {
                entityForBankDto.setDebitAmount(shopGoodsDeposit.getAmount());
            } else {
                entityForBankDto.setCreditAmount(shopGoodsDeposit.getAmount().multiply(new BigDecimal(-1)));
            }
            if (departmentNumber != null) {
                entityForBankDto.setDepartmentNumber(departmentNumber);
            } else {
                throw new ServiceException("部门不能为空");
            }
            if (bank.getCode() != null) {
                entityForBankDto.setBankAccountNumber(bank.getCode());
            } else {
                throw new ServiceException(bank.getName() + ",该银行没有编码，不能开单");
            }
            entityForBankDto.setAccountNumber("2241");//其他应付款
            entityForBankDto.setSettleTypeNumber(SettleTypeEnum.电汇.getFNumber());//电汇
            entityForBankDto.setEmpInfoNumber("0001");//员工
            if (CompanyNameEnum.JXVIVO.name().equals(RequestUtils.getCompanyName())){
                entityForBankDto.setOtherTypeNumber("2241.00018");//其他应付款-订货会订金
            }else if (CompanyNameEnum.JXOPPO.name().equals(RequestUtils.getCompanyName())) {
                entityForBankDto.setOtherTypeNumber("2241.00028");//其他应付款-订货会订金
            }
            entityForBankDto.setExpenseTypeNumber("6602.000");//无
            entityForBankDto.setCustomerNumber(null);
            entityForBankDto.setComment(depot.getName() + shopGoodsDeposit.getRemarks());
            cnJournalEntityForBankDtoList.add(entityForBankDto);
            cnJournalForBankDto.setEntityForBankDtoList(cnJournalEntityForBankDtoList);
            cnJournalForBankDtoList.add(cnJournalForBankDto);
            return cloudClient.synJournalBank(cnJournalForBankDtoList).get(0);
        }
        return null;
    }
}
