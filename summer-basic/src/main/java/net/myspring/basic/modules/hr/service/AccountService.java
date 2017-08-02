package net.myspring.basic.modules.hr.service;

import com.google.common.collect.Lists;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.common.utils.RequestUtils;
import net.myspring.basic.modules.hr.domain.Account;
import net.myspring.basic.modules.hr.domain.AccountPermission;
import net.myspring.basic.modules.hr.domain.Employee;
import net.myspring.basic.modules.hr.dto.AccountDto;
import net.myspring.basic.modules.hr.repository.AccountPermissionRepository;
import net.myspring.basic.modules.hr.repository.AccountRepository;
import net.myspring.basic.modules.hr.repository.EmployeeRepository;
import net.myspring.basic.modules.hr.repository.PositionRepository;
import net.myspring.basic.modules.hr.web.form.AccountForm;
import net.myspring.basic.modules.hr.web.query.AccountQuery;
import net.myspring.basic.modules.sys.domain.Permission;
import net.myspring.basic.modules.sys.manager.OfficeManager;
import net.myspring.basic.modules.sys.manager.RoleManager;
import net.myspring.basic.modules.sys.repository.PermissionRepository;
import net.myspring.common.constant.CharConstant;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.ExcelUtils;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import net.myspring.util.text.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by liuj on 2017/3/19.
 */
@Service
@Transactional(readOnly = true)
public class AccountService {

    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RoleManager roleManager;
    @Autowired
    private OfficeManager officeManager;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private AccountPermissionRepository accountPermissionRepository;

    public List<AccountDto> findByOfficeIdList(List<String> officeIdList){
        List<Account> accountList=accountRepository.findByOfficeIdIn(officeIdList);
        List<AccountDto> accountDtoList=BeanUtil.map(accountList,AccountDto.class);
        return accountDtoList;
    }

    public Account findByEmployeeIdAndType(String employeeId,String type){
        Account account=accountRepository.findByEmployeeIdAndType(employeeId,type);
        return account;
    }

    public AccountDto findOne(AccountDto accountDto) {
        if(!accountDto.isCreate()){
            accountDto = accountRepository.findDto(accountDto.getId());
            cacheUtils.initCacheInput(accountDto);
        }
        return accountDto;
    }

    public Account findById(String id) {
        return accountRepository.findOne(id);
    }

    public AccountDto findByLoginName(String loginName) {
        Account account = accountRepository.findByLoginName(loginName);
        AccountDto accountDto = BeanUtil.map(account, AccountDto.class);
        cacheUtils.initCacheInput(accountDto);
        return accountDto;
    }

    public Page<AccountDto> findPage(Pageable pageable, AccountQuery accountQuery) {
        Page<AccountDto> accountDtoPage = accountRepository.findPage(pageable, accountQuery);
        cacheUtils.initCacheInput(accountDtoPage.getContent());
        return accountDtoPage;
    }

    public List<AccountDto> findByFilter(AccountQuery accountQuery) {
        List<Account> accountList = accountRepository.findByFilter(accountQuery);
        List<AccountDto> accountDtoList = BeanUtil.map(accountList, AccountDto.class);
        cacheUtils.initCacheInput(accountList);
        return accountDtoList;
    }
    @Transactional
    public Account save(AccountForm accountForm) {
        Account account;
        if(CollectionUtil.isEmpty(accountForm.getOfficeIdList())||accountForm.getOfficeIdList().size()==1){
            accountForm.setOfficeIdList(Lists.newArrayList(accountForm.getOfficeId()));
        }
        accountForm.setOfficeIds(StringUtils.join(accountForm.getOfficeIdList(),CharConstant.COMMA));
        if (accountForm.isCreate()) {
            if (StringUtils.isNotBlank(accountForm.getPassword())) {
                accountForm.setPassword(StringUtils.getEncryptPassword(accountForm.getPassword()));
            } else {
                accountForm.setPassword(StringUtils.getEncryptPassword("123456"));
            }
            accountForm.setPositionIds(accountForm.getPositionId());
            account = BeanUtil.map(accountForm, Account.class);
            accountRepository.save(account);
        } else {
            if (StringUtils.isNotBlank(accountForm.getPassword())) {
                accountForm.setPassword(StringUtils.getEncryptPassword(accountForm.getPassword()));
            } else {
                accountForm.setPassword(accountRepository.findOne(accountForm.getId()).getPassword());
            }
            account = accountRepository.findOne(accountForm.getId());
            ReflectionUtil.copyProperties(accountForm,account);
            accountRepository.save(account);
        }
        if ("主账号".equals(accountForm.getType())) {
            Employee employee=employeeRepository.findOne(accountForm.getEmployeeId());
            employee.setAccountId(account.getId());
            employeeRepository.save(employee);
        }
        return account;
    }
    @Transactional
    public void logicDelete(String id) {
        accountRepository.logicDelete(id);
    }

    public List<AccountDto> findByLoginNameLikeAndType(String type,String key) {
        List<AccountDto> accountDtoList = accountRepository.findByLoginNameLikeAndType(type,key);
        return accountDtoList;
    }

    @CachePut(key = "#p0",value="authorityCache")
    public List<String> getAuthorityList(String accountId) {
        List<String> roleIdList = RequestUtils.getRoleIdList();
        List<String> authorityList;
        List<Permission> permissionList;
        if(RequestUtils.getAdmin()){
            permissionList=permissionRepository.findAllEnabled();
        }else {
            List<String> accountPermissions=accountPermissionRepository.findPermissionIdByAccountId(accountId);
            if(CollectionUtil.isNotEmpty(accountPermissions)){
                permissionList=permissionRepository.findByAccountId(accountId);
            }else {
                permissionList=permissionRepository.findByRoleIdList(roleIdList);
            }
        }
        authorityList= CollectionUtil.extractToList(permissionList,"permission");
        return authorityList;
    }

    public AccountDto getAccountDto(String accountId){
        AccountDto accountDto=accountRepository.findDto(accountId);
        cacheUtils.initCacheInput(accountDto);
        return accountDto;
    }


    public SimpleExcelBook findSimpleExcelSheet(AccountQuery accountQuery) throws IOException {
        Workbook workbook = new SXSSFWorkbook(10000);
        List<Account> accountList = accountRepository.findByFilter(accountQuery);
        List<AccountDto> accountDtoList = BeanUtil.map(accountList, AccountDto.class);
        cacheUtils.initCacheInput(accountDtoList);
        List<SimpleExcelColumn> simpleExcelColumnList=Lists.newArrayList();
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "type", "类型"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "loginName", "登录名"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "employeeName", "姓名"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "leaderName", "上级"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "officeName", "部门"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "employeeStatus", "是否在职"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "entryDate", "入职日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "regularDate", "转正日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "leaveDate", "离职日期"));
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("账户信息模版",accountDtoList,simpleExcelColumnList);
        ExcelUtils.doWrite(workbook,simpleExcelSheet);
        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook,"账户信息模版"+ UUID.randomUUID()+".xlsx",simpleExcelSheet);
        return simpleExcelBook;
    }
    @Transactional
    public void saveAccountAndPermission(AccountForm accountForm){
        accountPermissionRepository.setEnabledByAccountId(true, accountForm.getId());
        List<String> permissionIdList=accountPermissionRepository.findPermissionIdByAccountId(accountForm.getId());
        if (CollectionUtil.isNotEmpty(accountForm.getPermissionIdList())) {
            List<String> removeIdList = CollectionUtil.subtract(permissionIdList, accountForm.getPermissionIdList());
            List<String> addIdList = CollectionUtil.subtract(accountForm.getPermissionIdList(), permissionIdList);
            List<AccountPermission> addAccountPermissions = Lists.newArrayList();
            for (String permissionId : addIdList) {
                addAccountPermissions.add(new AccountPermission(accountForm.getId(), permissionId));
            }
            if (CollectionUtil.isNotEmpty(removeIdList)) {
                accountPermissionRepository.setEnabledByAccountAndPermissionIdList(false,removeIdList,accountForm.getId());
            }
            if (CollectionUtil.isNotEmpty(addIdList)) {
                accountPermissionRepository.save(addAccountPermissions);
            }
        } else if (CollectionUtil.isNotEmpty(permissionIdList)) {
            accountPermissionRepository.setEnabledByAccountId(false, accountForm.getId());
        }
    }

    public List<AccountDto> findByIds(List<String> ids){
        List<AccountDto> accountDtoList= accountRepository.findDtoByIdList(ids);
        cacheUtils.initCacheInput(accountDtoList);
        return accountDtoList;
    }


    public Boolean checkLoginName(AccountQuery accountQuery){
        Account account = accountRepository.findByLoginName(accountQuery.getLoginName());
        return account == null || (account.getId().equals(accountQuery.getId()));
    }

    public List<AccountDto> findByLoginNameList(List<String> loginNameList){
        List<Account> accountList=accountRepository.findByLoginNameList(loginNameList);
        List<AccountDto> accountDtoList=BeanUtil.map(accountList,AccountDto.class);
        return accountDtoList;
    }
}
