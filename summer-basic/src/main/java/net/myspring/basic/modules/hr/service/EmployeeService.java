package net.myspring.basic.modules.hr.service;

import com.google.common.collect.Lists;
import net.myspring.basic.common.enums.EmployeeStatusEnum;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.modules.hr.domain.Account;
import net.myspring.basic.modules.hr.domain.Employee;
import net.myspring.basic.modules.hr.dto.EmployeeDto;
import net.myspring.basic.modules.hr.repository.AccountRepository;
import net.myspring.basic.modules.hr.repository.EmployeeRepository;
import net.myspring.basic.modules.hr.web.form.AccountForm;
import net.myspring.basic.modules.hr.web.form.EmployeeForm;
import net.myspring.basic.modules.hr.web.query.EmployeeQuery;
import net.myspring.basic.modules.sys.manager.OfficeManager;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private OfficeManager officeManager;
    @Autowired
    private AccountRepository accountRepository;


    public EmployeeDto findOne(String id){
        Employee employee = employeeRepository.findOne(id);
        EmployeeDto employeeDto = BeanUtil.map(employee,EmployeeDto.class);
        return employeeDto;
    }

    public List<EmployeeDto> findAll(){
        List<Employee> employeeList = employeeRepository.findAll();
        List<EmployeeDto> employeeDtos = BeanUtil.map(employeeList,EmployeeDto.class);
        return employeeDtos;
    }

    public EmployeeDto findOne(EmployeeDto employeeDto){
        if(!employeeDto.isCreate()){
            Employee employee=employeeRepository.findOne(employeeDto.getId());
            employeeDto= BeanUtil.map(employee,EmployeeDto.class);
            cacheUtils.initCacheInput(employeeDto);
        }
        return employeeDto;
    }

    public Page<EmployeeDto> findPage(Pageable pageable, EmployeeQuery employeeQuery){
        Page<EmployeeDto> page=employeeRepository.findPage(pageable,employeeQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public List<EmployeeDto> findByNameLike(String name){
        List<EmployeeDto> employeeDtoList=employeeRepository.findByNameLike(name);
        cacheUtils.initCacheInput(employeeDtoList);
        return employeeDtoList;
    }

    @Transactional
    public Employee save(EmployeeForm employeeForm) {
        Employee employee;
        Account account;
        AccountForm accountForm=employeeForm.getAccountForm();
        if(CollectionUtil.isEmpty(accountForm.getOfficeIdList())||accountForm.getOfficeIdList().size()==1){
            accountForm.setOfficeIds(accountForm.getOfficeId());
        }
        if(CollectionUtil.isEmpty(accountForm.getPositionIdList())||accountForm.getPositionIdList().size()==1){
            accountForm.setPositionIds(accountForm.getPositionId());
        }
        if(employeeForm.isCreate()) {
            employee=BeanUtil.map(employeeForm,Employee.class);
            employeeRepository.save(employee);
            accountForm.setPassword(StringUtils.getEncryptPassword("123456"));
            account = BeanUtil.map(accountForm, Account.class);
            account.setEmployeeId(employee.getId());
            accountRepository.save(account);
        } else {
            employee = employeeRepository.findOne(employeeForm.getId());
            ReflectionUtil.copyProperties(employeeForm,employee);
            account = accountRepository.findOne(accountForm.getId());
            accountForm.setPassword(account.getPassword());
            ReflectionUtil.copyProperties(accountForm,account);
            employeeRepository.save(employee);
            account.setEmployeeId(employee.getId());
            accountRepository.save(account);
        }
        if(employeeForm.getLeaveDate()==null){
            employee.setStatus(EmployeeStatusEnum.在职.name());
        }else {
            employee.setStatus(EmployeeStatusEnum.离职.name());
        }
        employee.setAccountId(account.getId());
        employeeRepository.save(employee);
        return employee;
    }

    @Transactional
    public void logicDelete(String id) {
        employeeRepository.logicDelete(id);
    }

    public List<EmployeeDto> findByIds(List<String> ids){
        if(CollectionUtil.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        List<EmployeeDto> employeeDto=employeeRepository.findDtoByIdList(ids);
        return employeeDto;
    }

    public SimpleExcelBook exportData(EmployeeQuery employeeQuery){
        Workbook workbook = new SXSSFWorkbook(10000);
        List<EmployeeDto> employeeDtoList = employeeRepository.findFilter(employeeQuery);
        cacheUtils.initCacheInput(employeeDtoList);
        List<SimpleExcelColumn> simpleExcelColumnList = Lists.newArrayList();
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"accountName","登陆名"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"name","姓名"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"sex","性别"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"areaName","办事处"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"officeName","部门"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"positionName","岗位"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"salary","底薪"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"entryDate","入职日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"regularDate","转正日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"leaveDate","离职日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"idcard","身份证号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"mobilePhone","手机号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"education","学历"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"school","学校"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"major","专业"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"bankNumber","银行卡号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"bankName","开户行"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"status","状态"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"leaderName","上级"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"birthday","生日"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"remarks","备注"));
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("导出数据",employeeDtoList,simpleExcelColumnList);
        ExcelUtils.doWrite(workbook,simpleExcelSheet);
        return new SimpleExcelBook(workbook,"员工详情"+ LocalDate.now()+".xlsx",simpleExcelSheet);
    }
}
