package net.myspring.basic.modules.hr.service;

import net.myspring.basic.common.enums.AccountChangeTypeEnum;
import net.myspring.basic.common.enums.EmployeeStatusEnum;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.modules.hr.domain.Account;
import net.myspring.basic.modules.hr.domain.AccountChange;
import net.myspring.basic.modules.hr.domain.Employee;
import net.myspring.basic.modules.hr.domain.Position;
import net.myspring.basic.modules.hr.dto.AccountChangeDto;
import net.myspring.basic.modules.hr.repository.AccountChangeRepository;
import net.myspring.basic.modules.hr.repository.AccountRepository;
import net.myspring.basic.modules.hr.repository.EmployeeRepository;
import net.myspring.basic.modules.hr.repository.PositionRepository;
import net.myspring.basic.modules.hr.web.form.AccountChangeForm;
import net.myspring.basic.modules.hr.web.query.AccountChangeQuery;
import net.myspring.basic.modules.sys.client.ActivitiClient;
import net.myspring.basic.modules.sys.domain.Office;
import net.myspring.basic.modules.sys.manager.OfficeManager;
import net.myspring.basic.modules.sys.repository.OfficeRepository;
import net.myspring.common.constant.CharConstant;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AccountChangeService {

    @Autowired
    private AccountChangeRepository accountChangeRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private OfficeRepository officeRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private ActivitiClient activitiClient;
    @Autowired
    private OfficeManager officeManager;


    public AccountChange findOne(String id){
        AccountChange accountChange=accountChangeRepository.findOne(id);
        return accountChange;
    }

    public AccountChangeForm getForm(AccountChangeQuery accountChangeQuery){
        AccountChangeForm accountChangeForm =new AccountChangeForm();
        if(StringUtils.isNotBlank(accountChangeQuery.getId())||StringUtils.isNotBlank(accountChangeQuery.getAccountId())){
            accountChangeForm = accountChangeRepository.getForm(accountChangeQuery);
            if(StringUtils.isNotBlank(accountChangeQuery.getId())){
                AccountChange accountChange=accountChangeRepository.findOne(accountChangeQuery.getId());
                accountChangeForm.setType(accountChange.getType());
                accountChangeForm.setNewValue(accountChange.getNewValue());
                accountChangeForm.setRemarks(accountChange.getRemarks());
            }
            cacheUtils.initCacheInput(accountChangeForm);
        }
        return accountChangeForm;
    }
    @Transactional
    public void audit(String id,boolean pass,String comment) {
        AccountChange accountChange = accountChangeRepository.findOne(id);
        if (pass) {
            accountChange.setProcessStatus("已通过");
            Account account = accountRepository.findOne(accountChange.getAccountId());
            Employee employee=employeeRepository.findOne(account.getEmployeeId());
            if (accountChange.getType().equals(AccountChangeTypeEnum.部门.toString())) {
                account.setOfficeId(accountChange.getNewValue());
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.岗位.toString())) {
                account.setPositionId(accountChange.getNewValue());
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.上级.toString())) {
                account.setLeaderId(accountChange.getNewValue());
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.手机.toString())) {
                employee.setMobilePhone(accountChange.getNewValue());
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.身份证.toString())) {
                employee.setIdcard(accountChange.getNewValue());
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.银行卡号.toString())) {
                employee.setBankNumber(accountChange.getNewValue());
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.转正.toString())) {
                employee.setRegularDate(LocalDateUtils.parse(accountChange.getNewValue()));
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.离职.toString())) {
                employee.setLeaveDate(LocalDateUtils.parse(accountChange.getNewValue()));
                if(StringUtils.isNotBlank(accountChange.getNewValue())){
                    employee.setStatus(EmployeeStatusEnum.离职.name());
                }else {
                    employee.setStatus(EmployeeStatusEnum.在职.name());
                }
            }else if(accountChange.getType().equals(AccountChangeTypeEnum.入职.name())){
                employee.setEntryDate(LocalDateUtils.parse(accountChange.getNewValue()));
            }else if(accountChange.getType().equals(AccountChangeTypeEnum.底薪.name())){
                employee.setSalary(new BigDecimal(accountChange.getNewValue()));
            }else if(accountChange.getType().equals(AccountChangeTypeEnum.功能岗位.name())){
                account.setPositionIds(accountChange.getNewValue());
            }
            accountRepository.save(account);
            employeeRepository.save(employee);
        }else {
            accountChange.setProcessStatus("未通过");
        }
        accountChangeRepository.save(accountChange);
    }
    @Transactional
    public AccountChange save(AccountChangeForm accountChangeForm) {
        Account account=accountRepository.findOne(accountChangeForm.getAccountId());
        Employee employee=employeeRepository.findOne(account.getEmployeeId());
        AccountChange accountChange=new AccountChange();
        accountChange.setAccountId(accountChangeForm.getAccountId());
        accountChange.setNewValue(accountChangeForm.getNewValue());
        accountChange.setType(accountChangeForm.getType());
        accountChange.setRemarks(accountChangeForm.getRemarks());
        if (accountChange.getType().equals(AccountChangeTypeEnum.部门.toString())) {
            if (StringUtils.isNotBlank(account.getOfficeId())) {
                Office office=officeRepository.findOne(account.getOfficeId());
                accountChange.setOldValue(office.getId());
                accountChange.setOldLabel(office.getName());
            }
            accountChange.setNewLabel(officeRepository.findOne(accountChange.getNewValue()).getName());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.岗位.toString())) {
            if (StringUtils.isNotBlank(account.getPositionId())) {
                Position position=positionRepository.findOne(account.getPositionId());
                accountChange.setOldValue(position.getId());
                accountChange.setOldLabel(position.getName());
            }
            accountChange.setNewLabel(positionRepository.findOne(accountChange.getNewValue()).getName());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.上级.toString())) {
            if (StringUtils.isNotBlank(account.getLeaderId())) {
                Account leader=accountRepository.findOne(account.getLeaderId());
                accountChange.setOldValue(leader.getId());
                accountChange.setOldLabel(leader.getLoginName());
            }
            accountChange.setNewLabel(accountRepository.findOne(accountChange.getNewValue()).getLoginName());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.手机.toString())) {
            accountChange.setOldLabel(employee.getMobilePhone());
            accountChange.setOldValue(employee.getMobilePhone());
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.身份证.toString())) {
            accountChange.setOldLabel(employee.getIdcard());
            accountChange.setOldValue(employee.getIdcard());
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.银行卡号.toString())) {
            accountChange.setOldLabel(employee.getBankNumber());
            accountChange.setOldValue(employee.getBankNumber());
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.底薪.toString())) {
            accountChange.setOldLabel(employee.getSalary()!=null?employee.getSalary().toString():null);
            accountChange.setOldValue(employee.getSalary()!=null?employee.getSalary().toString():null);
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.转正.toString())) {
            if (employee.getRegularDate() != null) {
                accountChange.setOldLabel(LocalDateUtils.format(employee.getRegularDate()));
                accountChange.setOldValue(LocalDateUtils.format(employee.getRegularDate()));
                accountChange.setNewLabel(accountChangeForm.getNewValue());
            }
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.离职.toString())) {
            if (employee.getLeaveDate() != null) {
                accountChange.setOldLabel(LocalDateUtils.format(employee.getLeaveDate()));
                accountChange.setOldValue(LocalDateUtils.format(employee.getLeaveDate()));
            }
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        }else if(accountChange.getType().equals(AccountChangeTypeEnum.入职.name())){
            if (employee.getEntryDate() != null) {
                accountChange.setOldLabel(LocalDateUtils.format(employee.getEntryDate()));
                accountChange.setOldValue(LocalDateUtils.format(employee.getEntryDate()));
            }
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        }else if(accountChange.getType().equals(AccountChangeTypeEnum.功能岗位.name())){
            List<String> positionIdList=StringUtils.getSplitList(account.getPositionIds(), CharConstant.COMMA);
            List<Position> positionList=positionRepository.findByIdInAndEnabledIsTrue(positionIdList);
            accountChange.setOldValue(account.getPositionIds());
            accountChange.setOldLabel(StringUtils.join(CollectionUtil.extractToList(positionList,"name")));
            List<String> positionIds=StringUtils.getSplitList(accountChangeForm.getNewValue(), CharConstant.COMMA);
            List<Position> positions=positionRepository.findByIdInAndEnabledIsTrue(positionIds);
            accountChange.setNewLabel(StringUtils.join(CollectionUtil.extractToList(positions,"name")));
        }
        accountChange.setProcessStatus("省公司人事审核");
        accountChangeRepository.save(accountChange);
        if(accountChange.getType().equals(AccountChangeTypeEnum.离职.toString())&&employee.getLeaveDate()==null){
            audit(accountChange.getId(),true,null);
        }
        return accountChange;
    }


    public Page<AccountChangeDto> findPage(Pageable pageable, AccountChangeQuery accountChangeQuery){
        Page<AccountChangeDto> page=accountChangeRepository.findPage(pageable,accountChangeQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    @Transactional
    public void batchPass(String[] ids, boolean pass){
        List<String> idList= Arrays.asList(ids);
        for(String id:idList){
            audit(id,pass,"批量审核");
        }
    }

    @Transactional
    public void pass(String id,boolean pass){
        audit(id,pass,"审核");
    }
    @Transactional
    public void logicDelete(String id){
        accountChangeRepository.logicDelete(id);
    }
}
