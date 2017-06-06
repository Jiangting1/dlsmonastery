package net.myspring.basic.modules.hr.service;

import com.google.common.collect.Lists;
import net.myspring.basic.common.enums.*;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.common.utils.RequestUtils;
import net.myspring.basic.modules.hr.domain.*;
import net.myspring.basic.modules.hr.dto.CalendarEventDto;
import net.myspring.basic.modules.hr.repository.*;
import net.myspring.basic.modules.hr.dto.DutyDto;
import net.myspring.common.enums.AuditTypeEnum;
import net.myspring.util.collection.CollectionUtil;
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
@Transactional
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
            item = dutyLeaveRepository.findOne(id);
        } else if (DutyTypeEnum.免打卡.name().equals(dutyType)) {
            item = dutyFreeRepository.findOne(id);
        } else if (DutyTypeEnum.加班.name().equals(dutyType)) {
            item = dutyOvertimeRepository.findOne(id);
        } else if (DutyTypeEnum.公休.name().equals(dutyType)) {
            item = dutyPublicFreeRepository.findOne(id);
        } else if (DutyTypeEnum.出差.name().equals(dutyType)) {
            item = dutyTripRepository.findOne(id);
        } else if (DutyTypeEnum.签到.name().equals(dutyType)) {
            item = dutySignRepository.findOne(id);
        } else {
            item = dutyRestRepository.findOne(id);
        }
        return item;
    }
    public void audit(Map<String,String> map){
        for(Map.Entry<String,String> entry:map.entrySet()){
            audit(entry.getKey(),entry.toString(),true,null);
        }
    }

    public void audit(String id, String dutyType, Boolean pass, String auditRemarks) {
        String auditBy = RequestUtils.getAccountId();
        if (DutyTypeEnum.请假.toString().equals(dutyType)) {
            DutyLeave dutyLeave = dutyLeaveRepository.findOne(id);
            if (AuditTypeEnum.APPLYING.toString().equals(dutyLeave.getStatus())) {
                dutyLeave.setStatus(pass ? AuditTypeEnum.PASSED.toString() : AuditTypeEnum.NOT_PASS.toString());
                dutyLeave.setAuditBy(auditBy);
                dutyLeave.setAuditDate(LocalDateTime.now());
                dutyLeave.setAuditRemarks(auditRemarks);
                dutyLeave.setLocked(true);
                dutyLeaveRepository.save(dutyLeave);
            }
        } else if (DutyTypeEnum.免打卡.toString().equals(dutyType)) {
            DutyFree dutyFree = dutyFreeRepository.findOne(id);
            if (AuditTypeEnum.APPLYING.toString().equals(dutyFree.getStatus())) {
                dutyFree.setStatus(pass ? AuditTypeEnum.PASSED.toString() : AuditTypeEnum.NOT_PASS.toString());
                dutyFree.setAuditBy(auditBy);
                dutyFree.setAuditDate(LocalDateTime.now());
                dutyFree.setLocked(true);
                dutyFree.setAuditRemarks(auditRemarks);
                dutyFreeRepository.save(dutyFree);
            }
        } else if (DutyTypeEnum.公休.toString().equals(dutyType)) {
            DutyPublicFree dutyPublicFree = dutyPublicFreeRepository.findOne(id);
            if (AuditTypeEnum.APPLYING.toString().equals(dutyPublicFree.getStatus())) {
                dutyPublicFree.setStatus(pass ? AuditTypeEnum.PASSED.toString() : AuditTypeEnum.NOT_PASS.toString());
                dutyPublicFree.setAuditBy(auditBy);
                dutyPublicFree.setAuditDate(LocalDateTime.now());
                dutyPublicFree.setLocked(true);
                dutyPublicFree.setAuditRemarks(auditRemarks);
                dutyPublicFreeRepository.save(dutyPublicFree);
            }
        } else if (DutyTypeEnum.出差.toString().equals(dutyType)) {
            DutyTrip dutyTrip = dutyTripRepository.findOne(id);
            if (AuditTypeEnum.APPLYING.toString().equals(dutyTrip.getStatus())) {
                dutyTrip.setStatus(pass ? AuditTypeEnum.PASSED.toString() : AuditTypeEnum.NOT_PASS.toString());
                dutyTrip.setAuditBy(auditBy);
                dutyTrip.setAuditDate(LocalDateTime.now());
                dutyTrip.setLocked(true);
                dutyTrip.setAuditRemarks(auditRemarks);
                dutyTripRepository.save(dutyTrip);
            }
        } else if (DutyTypeEnum.加班.toString().equals(dutyType)) {
            DutyOvertime dutyOvertime = dutyOvertimeRepository.findOne(id);
            if (AuditTypeEnum.APPLYING.toString().equals(dutyOvertime.getStatus())) {
                dutyOvertime.setStatus(pass ? AuditTypeEnum.PASSED.toString() : AuditTypeEnum.NOT_PASS.toString());
                dutyOvertime.setAuditBy(auditBy);
                dutyOvertime.setAuditDate(LocalDateTime.now());
                dutyOvertime.setAuditRemarks(auditRemarks);
                dutyOvertime.setLocked(true);
                dutyOvertimeRepository.save(dutyOvertime);
            }
        } else if (DutyTypeEnum.调休.toString().equals(dutyType)) {
            DutyRest dutyRest = dutyRestRepository.findOne(id);
            if (AuditTypeEnum.APPLYING.toString().equals(dutyRest.getStatus())) {
                restAudit(dutyRest, auditBy, pass, auditRemarks);
            }
        } else if (DutyTypeEnum.签到.toString().equals(dutyType)) {
            DutySign dutySign = dutySignRepository.findOne(id);
            if (AuditTypeEnum.APPLYING.toString().equals(dutySign.getStatus())) {
                dutySign.setStatus(pass ? AuditTypeEnum.PASSED.toString() : AuditTypeEnum.NOT_PASS.toString());
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
    }

    private Boolean restAudit(DutyRest dutyRest, String auditById, Boolean pass, String auditRemarks) {
        if (pass) {
            Double restHour = 0.0;
            if (DutyRestTypeEnum.加班调休.toString().equals(dutyRest.getType())) {
                LocalDate dateStart = LocalDate.now().minusMonths(3);
                LocalDate dateEnd = dutyRest.getDutyDate();
                List<DutyOvertime> overtimeList = dutyOvertimeRepository.findByDutyDateAndStatus(dutyRest.getEmployeeId(), dateStart, dateEnd, AuditTypeEnum.PASSED.toString());
                List<DutyRestOvertime> dutyRestOvertimes = Lists.newArrayList();
                restHour = dutyRest.getHour().doubleValue();
                Double overTime = 0.0;
                for (DutyOvertime dutyOvertime : overtimeList) {
                    overTime += dutyOvertime.getLeftHour();
                }
                if (overTime < restHour) {
                   return false;
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
                DutyAnnual dutyAnnual = dutyAnnualRepository.findByEmployee(dutyRest.getEmployeeId()).get(0);
                if(dutyAnnual==null||dutyAnnual.getLeftHour()<restHour){
                    return false;
                }
                dutyAnnual.setLeftHour(dutyAnnual.getLeftHour() - restHour);
                dutyAnnualRepository.save(dutyAnnual);
                dutyRest.setDutyAnnualId(dutyAnnual.getId());
            }
        }
        dutyRest.setStatus(pass ? AuditTypeEnum.PASSED.toString() : AuditTypeEnum.NOT_PASS.toString());
        dutyRest.setAuditBy(auditById);
        dutyRest.setAuditDate(LocalDateTime.now());
        dutyRest.setAuditRemarks(auditRemarks);
        dutyRest.setLocked(true);
        dutyRestRepository.save(dutyRest);
        return true;
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
            calendarEventDto.setCssClass(getCssClass(AuditTypeEnum.APPLYING.toString()));
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
        if (AuditTypeEnum.APPLYING.toString().equals(status)) {
            return "warning";
        } else if (AuditTypeEnum.PASSED.toString().equals(status)) {
            return "info";
        } else {
            return "danger";
        }
    }
}
