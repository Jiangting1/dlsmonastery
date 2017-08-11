package net.myspring.basic.modules.hr.service;

import com.google.common.collect.Lists;
import net.myspring.basic.common.enums.DutyDateTypeEnum;
import net.myspring.basic.common.enums.DutyRestTypeEnum;
import net.myspring.basic.common.enums.DutyTypeEnum;
import net.myspring.basic.common.enums.WorkTimeTypeEnum;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.common.utils.RequestUtils;
import net.myspring.basic.modules.hr.domain.*;
import net.myspring.basic.modules.hr.dto.*;
import net.myspring.basic.modules.hr.repository.*;
import net.myspring.common.enums.AuditTypeEnum;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.time.LocalDateUtils;
import net.myspring.util.time.LocalTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/12/23.
 */
@Service
@Transactional(readOnly = true)
public class DutyService {

    @Autowired
    private DutyWorktimeRepository dutyWorktimeRepository;
    @Autowired
    private DutyLeaveRepository dutyLeaveRepository;
    @Autowired
    private DutyOvertimeRepository dutyOvertimeRepository;
    @Autowired
    private DutyRestRepository dutyRestRepository;
    @Autowired
    private DutySignRepository dutySignRepository;
    @Autowired
    private DutyAnnualRepository dutyAnnualRepository;
    @Autowired
    private DutyFreeRepository dutyFreeRepository;
    @Autowired
    private DutyPublicFreeRepository dutyPublicFreeRepository;
    @Autowired
    private DutyTripRepository dutyTripRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private DutyRestOvertimeRepository dutyRestOvertimeRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private EmployeeRepository employeeRepository;


    public List<DutyDto> findByAuditable(String leaderId, String status, LocalDateTime dateStart) {
        List<DutyDto> leaveList = dutyLeaveRepository.findByAuditable(leaderId, status, dateStart);
        List<DutyDto> overtimeList = dutyOvertimeRepository.findByAuditable(leaderId, status, dateStart);
        List<DutyDto> restList = dutyRestRepository.findByAuditable(leaderId, status, dateStart);
        List<DutyDto> signList = dutySignRepository.findByAuditable(leaderId, status, dateStart);
        List<DutyDto> freeList = dutyFreeRepository.findByAuditable(leaderId, status, dateStart);
        List<DutyDto> publicFreeList = dutyPublicFreeRepository.findByAuditable(leaderId, status, dateStart);
        List<DutyDto> tripList = dutyTripRepository.findByAuditable(leaderId, status, dateStart);
        List<DutyDto> dutyDtoList = Lists.newArrayList();
        dutyDtoList.addAll(leaveList);
        dutyDtoList.addAll(overtimeList);
        dutyDtoList.addAll(restList);
        dutyDtoList.addAll(signList);
        dutyDtoList.addAll(freeList);
        dutyDtoList.addAll(publicFreeList);
        dutyDtoList.addAll(tripList);
        return dutyDtoList;
    }

    public Object findDutyItem(String id, String dutyType) {
        Object item;
        if (DutyTypeEnum.请假.name().equals(dutyType)) {
            DutyLeave dutyLeave = dutyLeaveRepository.findOne(id);
            item= BeanUtil.map(dutyLeave, DutyLeaveDto.class);
        } else if (DutyTypeEnum.免打卡.name().equals(dutyType)) {
            DutyFree dutyFree = dutyFreeRepository.findOne(id);
            item= BeanUtil.map(dutyFree, DutyFreeDto.class);
        } else if (DutyTypeEnum.加班.name().equals(dutyType)) {
            DutyOvertime dutyOvertime = dutyOvertimeRepository.findOne(id);
            item= BeanUtil.map(dutyOvertime, DutyOvertimeDto.class);
        } else if (DutyTypeEnum.公休.name().equals(dutyType)) {
            DutyPublicFree dutyPublicFree = dutyPublicFreeRepository.findOne(id);
            item= BeanUtil.map(dutyPublicFree, DutyPublicFreeDto.class);
        } else if (DutyTypeEnum.出差.name().equals(dutyType)) {
            DutyTrip dutyTrip = dutyTripRepository.findOne(id);
            item= BeanUtil.map(dutyTrip, DutyTripDto.class);
        } else if (DutyTypeEnum.签到.name().equals(dutyType)) {
            DutySign dutySign = dutySignRepository.findOne(id);
            item= BeanUtil.map(dutySign, DutySignDto.class);
        } else {
            DutyRest dutyRest = dutyRestRepository.findOne(id);
            item= BeanUtil.map(dutyRest, DutyRestDto.class);
        }
        cacheUtils.initCacheInput(item);
        return item;
    }

    @Transactional
    public StringBuilder audit(Map<String,String> map){
        StringBuilder stringBuilder=new StringBuilder();
        for(Map.Entry<String,String> entry:map.entrySet()){
            stringBuilder.append(audit(entry.getKey(),entry.getValue(),true,null));
        }
        return stringBuilder;
    }

    @Transactional
    public StringBuilder audit(String id, String dutyType, Boolean pass, String auditRemarks) {
        StringBuilder stringBuilder=new StringBuilder();
        String auditBy = RequestUtils.getAccountId();
        if (DutyTypeEnum.请假.toString().equals(dutyType)) {
            DutyLeave dutyLeave = dutyLeaveRepository.findOne(id);
            if (AuditTypeEnum.APPLY.getValue().equals(dutyLeave.getStatus())) {
                dutyLeave.setStatus(pass ? AuditTypeEnum.PASS.getValue() : AuditTypeEnum.NOT_PASS.getValue());
                dutyLeave.setAuditBy(auditBy);
                dutyLeave.setAuditDate(LocalDateTime.now());
                dutyLeave.setAuditRemarks(auditRemarks);
                dutyLeave.setLocked(true);
                dutyLeaveRepository.save(dutyLeave);
            }
        } else if (DutyTypeEnum.免打卡.toString().equals(dutyType)) {
            DutyFree dutyFree = dutyFreeRepository.findOne(id);
            if (AuditTypeEnum.APPLY.getValue().equals(dutyFree.getStatus())) {
                dutyFree.setStatus(pass ? AuditTypeEnum.PASS.getValue() : AuditTypeEnum.NOT_PASS.getValue());
                dutyFree.setAuditBy(auditBy);
                dutyFree.setAuditDate(LocalDateTime.now());
                dutyFree.setLocked(true);
                dutyFree.setAuditRemarks(auditRemarks);
                dutyFreeRepository.save(dutyFree);
            }
        } else if (DutyTypeEnum.公休.toString().equals(dutyType)) {
            DutyPublicFree dutyPublicFree = dutyPublicFreeRepository.findOne(id);
            if (AuditTypeEnum.APPLY.getValue().equals(dutyPublicFree.getStatus())) {
                dutyPublicFree.setStatus(pass ? AuditTypeEnum.PASS.getValue() : AuditTypeEnum.NOT_PASS.getValue());
                dutyPublicFree.setAuditBy(auditBy);
                dutyPublicFree.setAuditDate(LocalDateTime.now());
                dutyPublicFree.setLocked(true);
                dutyPublicFree.setAuditRemarks(auditRemarks);
                dutyPublicFreeRepository.save(dutyPublicFree);
            }
        } else if (DutyTypeEnum.出差.toString().equals(dutyType)) {
            DutyTrip dutyTrip = dutyTripRepository.findOne(id);
            if (AuditTypeEnum.APPLY.getValue().equals(dutyTrip.getStatus())) {
                dutyTrip.setStatus(pass ? AuditTypeEnum.PASS.getValue() : AuditTypeEnum.NOT_PASS.getValue());
                dutyTrip.setAuditBy(auditBy);
                dutyTrip.setAuditDate(LocalDateTime.now());
                dutyTrip.setLocked(true);
                dutyTrip.setAuditRemarks(auditRemarks);
                dutyTripRepository.save(dutyTrip);
            }
        } else if (DutyTypeEnum.加班.toString().equals(dutyType)) {
            DutyOvertime dutyOvertime = dutyOvertimeRepository.findOne(id);
            if (AuditTypeEnum.APPLY.getValue().equals(dutyOvertime.getStatus())) {
                dutyOvertime.setStatus(pass ? AuditTypeEnum.PASS.getValue() : AuditTypeEnum.NOT_PASS.getValue());
                dutyOvertime.setAuditBy(auditBy);
                dutyOvertime.setAuditDate(LocalDateTime.now());
                dutyOvertime.setAuditRemarks(auditRemarks);
                dutyOvertime.setLocked(true);
                dutyOvertimeRepository.save(dutyOvertime);
            }
        } else if (DutyTypeEnum.调休.toString().equals(dutyType)) {
            DutyRest dutyRest = dutyRestRepository.findOne(id);
            if (AuditTypeEnum.APPLY.getValue().equals(dutyRest.getStatus())) {
                return restAudit(dutyRest, auditBy, pass, auditRemarks);
            }
        } else if (DutyTypeEnum.签到.toString().equals(dutyType)) {
            DutySign dutySign = dutySignRepository.findOne(id);
            if (AuditTypeEnum.APPLY.getValue().equals(dutySign.getStatus())) {
                dutySign.setStatus(pass ? AuditTypeEnum.PASS.getValue() : AuditTypeEnum.NOT_PASS.getValue());
                dutySign.setAuditBy(auditBy);
                dutySign.setAuditRemarks(auditRemarks);
                dutySign.setLocked(true);
                if (pass) {
                    DutyWorktime dutyWorktime = new DutyWorktime();
                    Account account = accountRepository.findOne(dutySign.getCreatedBy());
                    dutyWorktime.setEmployeeId(account.getEmployeeId());
                    dutyWorktime.setDutyDate(dutySign.getCreatedDate().toLocalDate());
                    dutyWorktime.setDutyTime(dutySign.getCreatedDate().toLocalTime());
                    dutyWorktime.setType(WorkTimeTypeEnum.外勤.toString());
                    dutyWorktimeRepository.save(dutyWorktime);
                    dutySign.setDutyWorktimeId(dutyWorktime.getId());
                }
                dutySignRepository.save(dutySign);
            }
        }
        return stringBuilder;
    }

private StringBuilder restAudit(DutyRest dutyRest, String auditById, Boolean pass, String auditRemarks) {
        if (pass) {
            Double restHour = 0.0;
            Employee employee=employeeRepository.findOne(dutyRest.getEmployeeId());
            if (DutyRestTypeEnum.加班调休.toString().equals(dutyRest.getType())) {
                LocalDate dateStart = LocalDateUtils.getFirstDayOfThisMonth(LocalDate.now().minusMonths(3));
                LocalDate dateEnd = dutyRest.getDutyDate();;
                List<DutyOvertime> overtimeList = dutyOvertimeRepository.findByDutyDateAndStatus(dutyRest.getEmployeeId(), dateStart, dateEnd, AuditTypeEnum.PASS.getValue());
                List<DutyRestOvertime> dutyRestOvertimes = Lists.newArrayList();
                restHour = dutyRest.getHour().doubleValue();
                Double overTime = 0.0;
                for (DutyOvertime dutyOvertime : overtimeList) {
                    overTime += dutyOvertime.getLeftHour();
                }
                if (overTime < restHour) {
                    return new StringBuilder(employee.getName()+"调休时间不足");
                }
                for (DutyOvertime dutyOvertime : overtimeList) {
                    if (dutyOvertime.getLeftHour() > 0 && restHour > 0) {
                        if (dutyOvertime.getLeftHour() >= restHour) {
                            DutyRestOvertime dutyRestOvertime = new DutyRestOvertime();
                            dutyRestOvertime.setRestHour(restHour);
                            dutyRestOvertime.setRestId(dutyRest.getId());
                            dutyRestOvertime.setOvertimeId(dutyOvertime.getId());
                            dutyRestOvertimes.add(dutyRestOvertime);
                            dutyOvertime.setLeftHour(dutyOvertime.getLeftHour() - restHour);
                            dutyOvertime.setLocked(true);
                            dutyOvertimeRepository.save(dutyOvertime);
                            break;
                        } else if (dutyOvertime.getLeftHour() < restHour) {
                            DutyRestOvertime dutyRestOvertime = new DutyRestOvertime();
                            dutyRestOvertime.setRestHour(dutyOvertime.getLeftHour());
                            restHour = restHour - dutyOvertime.getLeftHour();
                            dutyRestOvertime.setRestId(dutyRest.getId());
                            dutyRestOvertime.setOvertimeId(dutyOvertime.getId());
                            dutyRestOvertimes.add(dutyRestOvertime);
                            dutyOvertime.setLeftHour(0.0);
                            dutyOvertime.setLocked(true);
                            dutyOvertimeRepository.save(dutyOvertime);
                        }
                    }
                }
               if(CollectionUtil.isNotEmpty(dutyRestOvertimes)){
                   dutyRestOvertimeRepository.save(dutyRestOvertimes);
               }
            } else {
                restHour = DutyDateTypeEnum.全天.toString().equals(dutyRest.getDateType()) ? 8.0 : 4.0;
                DutyAnnual dutyAnnual = dutyAnnualRepository.findByEmployeeId(dutyRest.getEmployeeId()).get(0);
                if(dutyAnnual==null||dutyAnnual.getLeftHour()<restHour){
                    return new StringBuilder(employee.getName()+"调休时间不足");
                }
                dutyAnnual.setLeftHour(dutyAnnual.getLeftHour() - restHour);
                dutyAnnualRepository.save(dutyAnnual);
                dutyRest.setDutyAnnualId(dutyAnnual.getId());
            }
        }
        dutyRest.setStatus(pass ? AuditTypeEnum.PASS.getValue() : AuditTypeEnum.NOT_PASS.getValue());
        dutyRest.setAuditBy(auditById);
        dutyRest.setAuditDate(LocalDateTime.now());
        dutyRest.setAuditRemarks(auditRemarks);
        dutyRest.setLocked(true);
        dutyRestRepository.save(dutyRest);
        return null;
    }

    public List<CalendarEventDto> findEvent(String employeeId, LocalDate start, LocalDate end) {
        List<CalendarEventDto> list = Lists.newArrayList();
        List<DutyWorktime> worktimeList = dutyWorktimeRepository.findByEmployeeAndDate(employeeId, start, end);
        List<DutyLeave> leaveList = dutyLeaveRepository.findByEmployeeAndDate(employeeId, start, end);
        List<DutyOvertime> overtimeList = dutyOvertimeRepository.findByEmployeeAndDate(employeeId, start, end);
        List<DutyRest> restList = dutyRestRepository.findByEmployeeAndDate(employeeId, start, end);
        List<DutyFree> freeList = dutyFreeRepository.findByEmployeeAndDate(employeeId, start, end);
        List<DutyPublicFree> publicFreeList = dutyPublicFreeRepository.findByEmployeeAndDate(employeeId, start, end);
        List<DutyTrip> tripList = dutyTripRepository.findByEmployeeAndDate(employeeId, start, end);
        List<DutySign>signList=dutySignRepository.findByEmployeeAndDate(employeeId, start, end);
        for (DutyWorktime dutyWorktime : worktimeList) {
            CalendarEventDto calendarEventDto = new CalendarEventDto();
            calendarEventDto.setId(dutyWorktime.getId());
            calendarEventDto.setStart(dutyWorktime.getDutyDate());
            calendarEventDto.setTitle("卡:" + LocalTimeUtils.format(dutyWorktime.getDutyTime()));
            calendarEventDto.setCssClass(getCssClass(AuditTypeEnum.APPLY.getValue()));
            list.add(calendarEventDto);
        }
        for (DutyLeave dutyLeave : leaveList) {
            CalendarEventDto calendarEventDto = new CalendarEventDto();
            calendarEventDto.setStart(dutyLeave.getDutyDate());
            calendarEventDto.setTitle("假:" + dutyLeave.getDateType());
            calendarEventDto.setContent("请假申请<br/>状态：" + dutyLeave.getStatus());
            calendarEventDto.setCssClass(getCssClass(dutyLeave.getStatus()));
            list.add(calendarEventDto);
        }
        for (DutyOvertime dutyOvertime : overtimeList) {
            CalendarEventDto calendarEventDto = new CalendarEventDto();
            calendarEventDto.setStart(dutyOvertime.getDutyDate());
            calendarEventDto.setTitle("加:" + LocalTimeUtils.format(dutyOvertime.getTimeStart()) + "~" + LocalTimeUtils.format(dutyOvertime.getTimeEnd()));
            calendarEventDto.setContent("加班申请<br/>状态：" + dutyOvertime.getStatus());
            calendarEventDto.setCssClass(getCssClass(dutyOvertime.getStatus()));
            list.add(calendarEventDto);
        }
        for (DutyRest dutyRest : restList) {
            CalendarEventDto calendarEventDto = new CalendarEventDto();
            calendarEventDto.setStart(dutyRest.getDutyDate());
            calendarEventDto.setTitle("调:" + LocalTimeUtils.format(dutyRest.getTimeStart()) + "~" + LocalTimeUtils.format(dutyRest.getTimeEnd()));
            calendarEventDto.setContent("调休申请<br/>状态：" + dutyRest.getStatus());
            calendarEventDto.setCssClass(getCssClass(dutyRest.getStatus()));
            list.add(calendarEventDto);
        }
        for (DutyFree dutyFree : freeList) {
            CalendarEventDto calendarEventDto = new CalendarEventDto();
            calendarEventDto.setStart(dutyFree.getFreeDate());
            calendarEventDto.setTitle("免:" + dutyFree.getDateType());
            calendarEventDto.setContent("免打卡申请<br/>状态：" + dutyFree.getStatus());
            calendarEventDto.setCssClass(getCssClass(dutyFree.getStatus()));
            list.add(calendarEventDto);
        }
        for (DutyPublicFree dutyPublicFree : publicFreeList) {
            CalendarEventDto calendarEventDto = new CalendarEventDto();
            calendarEventDto.setStart(dutyPublicFree.getFreeDate());
            calendarEventDto.setTitle("公:" + dutyPublicFree.getDateType());
            calendarEventDto.setContent("公休申请<br/>状态：" + dutyPublicFree.getStatus());
            calendarEventDto.setCssClass(getCssClass(dutyPublicFree.getStatus()));
            list.add(calendarEventDto);
        }
        for (DutyTrip dutyTrip : tripList) {
            for(LocalDate date : LocalDateUtils.getDateList(dutyTrip.getDateStart(), dutyTrip.getDateEnd())){
                CalendarEventDto calendarEventDto = new CalendarEventDto();
                calendarEventDto.setStart(date);
                calendarEventDto.setTitle("差:" + LocalDateUtils.format(date));
                calendarEventDto.setContent("出差申请<br/>状态：" + dutyTrip.getStatus());
                calendarEventDto.setCssClass(getCssClass(dutyTrip.getStatus()));
                list.add(calendarEventDto);
            }
        }
        //签到
        for(DutySign dutySign:signList){
            CalendarEventDto calendarEventDto = new CalendarEventDto();
            calendarEventDto.setStart(dutySign.getDutyDate());
            calendarEventDto.setTitle("签:" + LocalTimeUtils.format(dutySign.getDutyTime()));
            calendarEventDto.setContent("签到申请<br/>状态：" + dutySign.getStatus());
            list.add(calendarEventDto);
        }
        return list;
    }


    private String getCssClass(String status) {
        if (AuditTypeEnum.APPLY.getValue().equals(status)) {
            return "warning";
        } else if (AuditTypeEnum.PASS.getValue().equals(status)) {
            return "info";
        } else {
            return "danger";
        }
    }
}
